package io.github.rxue.investment.portfolio.holdings;

import java.util.*;

public class Holding {
    private final List<Field> fields;
    private final List<Object> values;

    private Holding(Builder builder) {
        this.fields = builder.fields;
        this.values = Collections.unmodifiableList(builder.values);
    }
    public List<Field> fields() {
        return fields;
    }
    public List<Object> values() {
        return values;
    }
    @SuppressWarnings("unchecked")
    public <T> T value(Field field) {
        Object value = values.get(fields.indexOf(field));
        return (T) field.type().cast(value);
    }
    public static class Builder {
        private final List<Field> fields;
        private final List<Object> values;
        public Builder(List<OptionalField> fields) {
            this.fields = allFields(fields);
            this.values = Arrays.asList(new Object[fields.size() + CompulsoryField.values().length]);
        }

        private static List<Field> allFields(List<OptionalField> optionalFields) {
            List<Field> allFields = new ArrayList<>();
            Collections.addAll(allFields, CompulsoryField.values());
            allFields.addAll(optionalFields);
            return Collections.unmodifiableList(allFields);
        }

        public Builder add(Field field, Object value) {
            values.set(fields.indexOf(field), value);
            return this;
        }

        @SuppressWarnings("unchecked")
        public <T> T value(Field field) {
            Object value = values.get(fields.indexOf(field));
            return (T) field.type().cast(value);
        }


        public Holding build() {
            return new Holding(this);
        }
    }
}
