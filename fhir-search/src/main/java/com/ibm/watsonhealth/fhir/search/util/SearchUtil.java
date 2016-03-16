/**
 * (C) Copyright IBM Corp. 2016,2017,2018,2019
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.ibm.watsonhealth.fhir.search.util;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.bind.Binder;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.watsonhealth.fhir.model.Bundle;
import com.ibm.watsonhealth.fhir.model.BundleEntry;
import com.ibm.watsonhealth.fhir.model.Resource;
import com.ibm.watsonhealth.fhir.model.ResourceContainer;
import com.ibm.watsonhealth.fhir.model.SearchParameter;
import com.ibm.watsonhealth.fhir.model.util.FHIRUtil;
import com.ibm.watsonhealth.fhir.model.util.FHIRUtil.Format;
import com.ibm.watsonhealth.fhir.search.Parameter;
import com.ibm.watsonhealth.fhir.search.ParameterValue;
import com.ibm.watsonhealth.fhir.search.Parameter.Modifier;
import com.ibm.watsonhealth.fhir.search.Parameter.Type;
import com.ibm.watsonhealth.fhir.search.ParameterValue.Prefix;

public class SearchUtil {
	private static final Map<String, Map<String, SearchParameter>> searchParameterMap = buildSearchParameterMap();
	private static final XPath xpath = createXPath();
	private static final Map<String, XPathExpression> expressionMap = new HashMap<String, XPathExpression>();
	private static final DatatypeFactory datatypeFactory = createDatatypeFactory();
	
	private SearchUtil() { }
	
	private static DatatypeFactory createDatatypeFactory() {
		try {
			return DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
			throw new Error(e);
		}
	}
	
	private static XPath createXPath() {
		XPathFactory xf = XPathFactory.newInstance();
		XPath xp = xf.newXPath();
		xp.setNamespaceContext(new NamespaceContext() {
			@Override
			public String getNamespaceURI(String prefix) {
				if ("f".equals(prefix)) {
					return "http://hl7.org/fhir";
				}
				return null;
			}

			@Override
			public String getPrefix(String namespaceURI) {
				if ("http://hl7.org/fhir".equals(namespaceURI)) {
					return "f";
				}
				return null;
			}

			@Override
			public Iterator<String> getPrefixes(String namespaceURI) {
				return null;
			}
		});
		return xp;
	}
	
	private static XPathExpression getExpression(String expr) throws XPathExpressionException {
		XPathExpression expression = expressionMap.get(expr);
		if (expression == null) {
			// double-checked locking pattern
			synchronized (expressionMap) {
				expression = expressionMap.get(expr);
				if (expression == null) {
					expression = xpath.compile(expr);
					expressionMap.put(expr, expression);
				}
			}
		}
		return expression;
	}
	
	private static Map<String, Map<String, SearchParameter>> buildSearchParameterMap() {
		Map<String, Map<String, SearchParameter>> searchParameterMap = new HashMap<String, Map<String, SearchParameter>>();
		try {
			InputStream stream = SearchUtil.class.getClassLoader().getResourceAsStream("search-parameters.xml");
			Bundle bundle = FHIRUtil.read(Bundle.class, Format.XML, stream);
			for (BundleEntry entry : bundle.getEntry()) {
				ResourceContainer container = entry.getResource();
				SearchParameter parameter = container.getSearchParameter();
				if (parameter != null) {
					String base = parameter.getBase().getValue();
					Map<String, SearchParameter> map = searchParameterMap.get(base);
					if (map == null) {
						map = new TreeMap<String, SearchParameter>();
						searchParameterMap.put(base, map);
					}
					String name = parameter.getName().getValue();
					map.put(name, parameter);
				}
			}
			return searchParameterMap;
		} catch (JAXBException e) {
			throw new Error(e);
		}
	}
	
	public static List<SearchParameter> getSearchParameters(Class<? extends Resource> resourceType) {
		Map<String, SearchParameter> map = searchParameterMap.get(resourceType.getSimpleName());
		if (map != null) {
			return new ArrayList<SearchParameter>(map.values());
		}
		return Collections.emptyList();
	}
	
	public static SearchParameter getSearchParameter(Class<? extends Resource> resourceType, String name) {
		Map<String, SearchParameter> map = searchParameterMap.get(resourceType.getSimpleName());
		if (map != null) {
			return map.get(name);
		}
		return null;
	}
	
	public static <T extends Resource> Map<SearchParameter, List<Object>> extractParameterValues(T resource) throws JAXBException, XPathExpressionException {
		Map<SearchParameter, List<Object>> result = new TreeMap<SearchParameter, List<Object>>(new Comparator<SearchParameter>() {
			@Override
			public int compare(SearchParameter first, SearchParameter second) {
				return first.getName().getValue().compareTo(second.getName().getValue());
			}
		});
		
		Class<? extends Resource> resourceType = resource.getClass();
		
		Binder<Node> binder = FHIRUtil.createBinder(resource);
		Document document = binder.getXMLNode(resource).getOwnerDocument();
		
		List<SearchParameter> parameters = new ArrayList<SearchParameter>(getSearchParameters(Resource.class));
		parameters.addAll(getSearchParameters(resourceType));
		
		for (SearchParameter parameter : parameters) {
			if (parameter.getXpath() != null) {
				String xpath = parameter.getXpath().getValue();
				if (xpath.startsWith("f:Resource")) {
					xpath = xpath.replaceFirst("f:Resource", "f:" + resource.getClass().getSimpleName());
				}
				for (String s : xpath.split("\\|")) {
					XPathExpression expr = getExpression(s);
					NodeList nodeList = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
					if (nodeList.getLength() > 0) {
						List<Object> values = result.get(parameter);
						if (values == null) {
							values = new ArrayList<Object>();
							result.put(parameter, values);
						}
						for (int i = 0; i < nodeList.getLength(); i++) {
							values.add(binder.getJAXBNode(nodeList.item(i)));
						}
					}
				}
			}
		}
		
		return result;
	}
	
	public static List<Parameter> parseQueryParameters(Class<? extends Resource> resourceType, Map<String, List<String>> queryParameters) {
		List<Parameter> parameters = new ArrayList<Parameter>();
		
		for (String name : queryParameters.keySet()) {
			// get the definition for this search parameter based on resource type and name
			SearchParameter searchParameter = name.startsWith("_") ? 
					SearchUtil.getSearchParameter(Resource.class, name) : 
					SearchUtil.getSearchParameter(resourceType, name);
					
			// get the type of parameter so that we can use it to parse the value
			Type type = Type.fromValue(searchParameter.getType().getValue());
			
			// parse name
			Modifier modifier = null;
			String modifierResourceTypeName = null;
			if (name.contains(":")) {
				String mod = name.substring(name.indexOf(":") + 1);
				if (FHIRUtil.isValidResourceTypeName(mod)) {
					modifier = Modifier.TYPE;
					modifierResourceTypeName = mod;
				} else {
					modifier = Modifier.fromValue(mod);
				}
				name = name.substring(0, name.indexOf(":"));
			}
			
			// parse values		
			for (String value : queryParameters.get(name)) {
				Parameter parameter = new Parameter(type, name, modifier, modifierResourceTypeName);
				parameters.add(parameter);
				for (String v : value.split(",")) {
					ParameterValue parameterValue = new ParameterValue();
					Prefix prefix = null;
					switch (type) {
					case DATE: {
						// date
						// [parameter]=[prefix][value]
						prefix = getPrefix(v);
						if (prefix != null) {
							v = v.substring(2);
							parameterValue.setPrefix(prefix);
						}
						parameterValue.setPart("value", datatypeFactory.newXMLGregorianCalendar(v));
						break;
					}
					case NUMBER: {
						// number
						// [parameter]=[prefix][value]
						prefix = getPrefix(v);
						if (prefix != null) {
							v = v.substring(2);
							parameterValue.setPrefix(prefix);
						}
						parameterValue.setPart("value", Double.parseDouble(v));
						break;
					}
					case REFERENCE: {
						// reference
						if (isURL(v)) {
							// [parameter]=[url]
							parameterValue.setPart("url", v);
						} else {
							if (v.contains("/")) {
								// [parameter]=[type]/[id]
								String[] parts = v.split("/");
								parameterValue.setPart("type", parts[0]);
								parameterValue.setPart("id", parts[1]);
								parameterValue.setSeparator("/");
							} else {
								// [parameter]=[id]
								parameterValue.setPart("id", v);
							}
						}
						break;
					}
					case QUANTITY: {
						// quantity
						// [parameter]=[prefix][number]|[system]|[code]
						prefix = getPrefix(v);
						if (prefix != null) {
							v = v.substring(2);
							parameterValue.setPrefix(prefix);
						}
						String[] parts = v.split("|");
						String number = parts[0];
						parameterValue.setPart("number", Double.parseDouble(number));
						String system = parts[1];	// could be empty string
						parameterValue.setPart("system", system);
						String code = parts[2];
						parameterValue.setPart("code", code);
						parameterValue.setSeparator("|");
						break;
					}
					case STRING: {
						// string
						// [parameter]=[value]
						parameterValue.setPart("value", v);
						break;
					}
					case TOKEN: {
						// token
						// [parameter]=[system]|[code]
						String[] parts = v.split("|");
						if (parts.length == 2) {
							parameterValue.setPart("system", parts[0]);	// could be empty string
							parameterValue.setPart("code", parts[1]);
							parameterValue.setSeparator("|");
						} else {
							parameterValue.setPart("code", v);
						}
						break;
					}
					case URI: {
						// [parameter]=[value]
						if (isURL(v)) {
							parameterValue.setPart("value", v);
						}
						break;
					}
					default:
						break;
					}
					parameter.getValues().add(parameterValue);
				}
			}
		}
		
		return parameters;
	}
	
	private static Prefix getPrefix(String s) {
		for (Prefix prefix : Prefix.values()) {
			if (s.startsWith(prefix.value())) {
				return prefix;
			}
		}
		return null;
	}
	
	private static boolean isURL(String s) {
		try {
			new URL(s);
			return true;
		} catch (MalformedURLException e) {
			return false;
		}
	}
}
