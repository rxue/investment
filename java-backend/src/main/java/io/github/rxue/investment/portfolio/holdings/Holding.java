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
        requireKnownField(fieldValues, field);
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

    private static void requireKnownField(Map<Field, Object> fieldValues, Field field) {
        if (!fieldValues.containsKey(field)) {
            throw new IllegalArgumentException("Unknown field: " + field);
        }
    }

    public static class Builder {
        private final LinkedHashMap<Field, Object> fieldValues;
        public Builder(List<OptionalField> optionalFields) {
            this.fieldValues = new LinkedHashMap<>();
            for (CompulsoryField field : CompulsoryField.values()) {
                fieldValues.put(field, null);
            }
            for (OptionalField field : optionalFields) {
                fieldValues.put(field, null);
            }
        }

        public Builder set(Field field, Object value) {
            requireKnownField(fieldValues, field);
            fieldValues.put(field, value);
            return this;
        }

        @SuppressWarnings("unchecked")
        public <T> T value(Field field) {
            requireKnownField(fieldValues, field);
            return (T) field.type().cast(fieldValues.get(field));
        }

        public Holding build() {
            return new Holding(this);
        }
    }
}
