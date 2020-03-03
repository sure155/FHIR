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

@System("http://hl7.org/fhir/participantrequired")
@Generated("com.ibm.fhir.tools.CodeGenerator")
public class ParticipantRequired extends Code {
    /**
     * Required
     * 
     * <p>The participant is required to attend the appointment.
     */
    public static final ParticipantRequired REQUIRED = ParticipantRequired.builder().value(ValueSet.REQUIRED).build();

    /**
     * Optional
     * 
     * <p>The participant may optionally attend the appointment.
     */
    public static final ParticipantRequired OPTIONAL = ParticipantRequired.builder().value(ValueSet.OPTIONAL).build();

    /**
     * Information Only
     * 
     * <p>The participant is excluded from the appointment, and might not be informed of the appointment taking place. 
     * (Appointment is about them, not for them - such as 2 doctors discussing results about a patient's test).
     */
    public static final ParticipantRequired INFORMATION_ONLY = ParticipantRequired.builder().value(ValueSet.INFORMATION_ONLY).build();

    private volatile int hashCode;

    private ParticipantRequired(Builder builder) {
        super(builder);
    }

    public ValueSet getValueAsEnumConstant() {
        return (value != null) ? ValueSet.from(value) : null;
    }

    public static ParticipantRequired of(ValueSet value) {
        switch (value) {
        case REQUIRED:
            return REQUIRED;
        case OPTIONAL:
            return OPTIONAL;
        case INFORMATION_ONLY:
            return INFORMATION_ONLY;
        default:
            throw new IllegalStateException(value.name());
        }
    }

    public static ParticipantRequired of(java.lang.String value) {
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
        ParticipantRequired other = (ParticipantRequired) obj;
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
        public ParticipantRequired build() {
            return new ParticipantRequired(this);
        }
    }

    public enum ValueSet {
        /**
         * Required
         * 
         * <p>The participant is required to attend the appointment.
         */
        REQUIRED("required"),

        /**
         * Optional
         * 
         * <p>The participant may optionally attend the appointment.
         */
        OPTIONAL("optional"),

        /**
         * Information Only
         * 
         * <p>The participant is excluded from the appointment, and might not be informed of the appointment taking place. 
         * (Appointment is about them, not for them - such as 2 doctors discussing results about a patient's test).
         */
        INFORMATION_ONLY("information-only");

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
