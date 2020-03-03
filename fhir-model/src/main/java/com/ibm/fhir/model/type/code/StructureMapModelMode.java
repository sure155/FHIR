/*
 * (C) Copyright IBM Corp. 2019, 2020
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.ibm.fhir.model.type.code;

import com.ibm.fhir.model.annotation.System;
import com.ibm.fhir.model.type.Code;
import com.ibm.fhir.model.type.Extension;
import com.ibm.fhir.model.type.String;

import java.util.Collection;
import java.util.Objects;

import javax.annotation.Generated;

@System("http://hl7.org/fhir/map-model-mode")
@Generated("com.ibm.fhir.tools.CodeGenerator")
public class StructureMapModelMode extends Code {
    /**
     * Source Structure Definition
     * 
     * <p>This structure describes an instance passed to the mapping engine that is used a source of data.
     */
    public static final StructureMapModelMode SOURCE = StructureMapModelMode.builder().value(ValueSet.SOURCE).build();

    /**
     * Queried Structure Definition
     * 
     * <p>This structure describes an instance that the mapping engine may ask for that is used a source of data.
     */
    public static final StructureMapModelMode QUERIED = StructureMapModelMode.builder().value(ValueSet.QUERIED).build();

    /**
     * Target Structure Definition
     * 
     * <p>This structure describes an instance passed to the mapping engine that is used a target of data.
     */
    public static final StructureMapModelMode TARGET = StructureMapModelMode.builder().value(ValueSet.TARGET).build();

    /**
     * Produced Structure Definition
     * 
     * <p>This structure describes an instance that the mapping engine may ask to create that is used a target of data.
     */
    public static final StructureMapModelMode PRODUCED = StructureMapModelMode.builder().value(ValueSet.PRODUCED).build();

    private volatile int hashCode;

    private StructureMapModelMode(Builder builder) {
        super(builder);
    }

    public ValueSet getValueAsEnumConstant() {
        return (value != null) ? ValueSet.from(value) : null;
    }

    public static StructureMapModelMode of(ValueSet value) {
        switch (value) {
        case SOURCE:
            return SOURCE;
        case QUERIED:
            return QUERIED;
        case TARGET:
            return TARGET;
        case PRODUCED:
            return PRODUCED;
        default:
            throw new IllegalStateException(value.name());
        }
    }

    public static StructureMapModelMode of(java.lang.String value) {
        return of(ValueSet.from(value));
    }

    public static String string(java.lang.String value) {
        return of(ValueSet.from(value));
    }

    public static Code code(java.lang.String value) {
        return of(ValueSet.from(value));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        StructureMapModelMode other = (StructureMapModelMode) obj;
        return Objects.equals(id, other.id) && Objects.equals(extension, other.extension) && Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        int result = hashCode;
        if (result == 0) {
            result = Objects.hash(id, extension, value);
            hashCode = result;
        }
        return result;
    }

    public Builder toBuilder() {
        Builder builder = new Builder();
        builder.id(id);
        builder.extension(extension);
        builder.value(value);
        return builder;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends Code.Builder {
        private Builder() {
            super();
        }

        @Override
        public Builder id(java.lang.String id) {
            return (Builder) super.id(id);
        }

        @Override
        public Builder extension(Extension... extension) {
            return (Builder) super.extension(extension);
        }

        @Override
        public Builder extension(Collection<Extension> extension) {
            return (Builder) super.extension(extension);
        }

        @Override
        public Builder value(java.lang.String value) {
            return (value != null) ? (Builder) super.value(ValueSet.from(value).value()) : this;
        }

        public Builder value(ValueSet value) {
            return (value != null) ? (Builder) super.value(value.value()) : this;
        }

        @Override
        public StructureMapModelMode build() {
            return new StructureMapModelMode(this);
        }
    }

    public enum ValueSet {
        /**
         * Source Structure Definition
         * 
         * <p>This structure describes an instance passed to the mapping engine that is used a source of data.
         */
        SOURCE("source"),

        /**
         * Queried Structure Definition
         * 
         * <p>This structure describes an instance that the mapping engine may ask for that is used a source of data.
         */
        QUERIED("queried"),

        /**
         * Target Structure Definition
         * 
         * <p>This structure describes an instance passed to the mapping engine that is used a target of data.
         */
        TARGET("target"),

        /**
         * Produced Structure Definition
         * 
         * <p>This structure describes an instance that the mapping engine may ask to create that is used a target of data.
         */
        PRODUCED("produced");

        private final java.lang.String value;

        ValueSet(java.lang.String value) {
            this.value = value;
        }

        public java.lang.String value() {
            return value;
        }

        public static ValueSet from(java.lang.String value) {
            for (ValueSet c : ValueSet.values()) {
                if (c.value.equals(value)) {
                    return c;
                }
            }
            throw new IllegalArgumentException(value);
        }
    }
}
