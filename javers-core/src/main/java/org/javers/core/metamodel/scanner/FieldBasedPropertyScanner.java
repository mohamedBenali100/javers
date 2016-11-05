package org.javers.core.metamodel.scanner;

import org.javers.common.reflection.JaversField;
import org.javers.common.reflection.ReflectionUtil;
import org.javers.core.metamodel.property.Property;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pawel szymczyk
 */
class FieldBasedPropertyScanner implements PropertyScanner {

    private final AnnotationNamesProvider annotationNamesProvider;

    public FieldBasedPropertyScanner(AnnotationNamesProvider annotationNamesProvider) {
        this.annotationNamesProvider = annotationNamesProvider;
    }

    @Override
    public PropertyScan scan(Class<?> managedClass, ClassAnnotationsScan classScan) {
        List<JaversField> fields = ReflectionUtil.getAllPersistentFields(managedClass);
        List<Property> propertyList = new ArrayList<>(fields.size());

        for (JaversField field : fields) {
            boolean isIgnoredInType = classScan.hasIgnoreDeclaredProperties() && field.getDeclaringClass().equals(managedClass);
            boolean hasTransientAnn = field.hasAnyAnnotation(annotationNamesProvider.getTransientAliases());
            boolean hasShallowReferenceAnn = field.hasAnyAnnotation(annotationNamesProvider.getShallowReferenceAliases());
            propertyList.add(new Property(field, hasTransientAnn || isIgnoredInType, hasShallowReferenceAnn));
        }
        return new PropertyScan(propertyList);
    }

}
