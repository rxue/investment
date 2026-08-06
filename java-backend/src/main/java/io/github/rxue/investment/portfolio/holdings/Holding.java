package io.github.rxue.investment.portfolio.holdings;

import java.util.*;

public class Holding {
    private final LinkedHashMap<Field, Object> fieldValues;

    private Holding(Builder builder) {
        this.fieldValues = new LinkedHashMap<>(builder.fieldValues);
    }
    public List<Field> fields() {
        return Collections.unmodifiableList(new ArrayList<>(fieldValues.keySet()));
    }
    public Map<Field, Object> asMap() {
        return Collections.unmodifiableMap(fieldValues);
    }

    @SuppressWarnings("unchecked")
    public <T> T value(Field field) {
        return (T) field.type().cast(fieldValues.get(field));
    }
    @Override
    public boolean equals(Object that){
        return that instanceof Holding t
                && Objects.equals(t.fieldValues, fieldValues);
    }
    @Override
    public int hashCode() {
        return fieldValues.hashCode();
    }


    public static class Builder {
        private final LinkedHashMap<Field, Object> fieldValues;
        public Builder(String companyId, int position) {
            this.fieldValues = new LinkedHashMap<>();
            this.fieldValues.put(CompulsoryField.COMPANY_ID, companyId);
            this.fieldValues.put(CompulsoryField.POSITION, position);
        }

        public Builder set(Field field, Object value) {
            validateType(field, value);
            fieldValues.put(field, value);
            return this;
        }

        @SuppressWarnings("unchecked")
        public <T> T value(Field field) {
            return (T) field.type().cast(fieldValues.get(field));
        }

        public Holding build() {
            return new Holding(this);
        }

        private static void validateType(Field field, Object value) {
            if (value != null && !field.type().isInstance(value)) {
                throw new IllegalArgumentException(
                        "Invalid value for field " + field + ": expected " + field.type().getName()
                                + " but got " + value.getClass().getName());
            }
        }
    }
}
