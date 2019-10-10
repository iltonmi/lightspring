package org.lightspring.core.annotation;


import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

public abstract class AnnotationUtils {

    public static <T extends Annotation> T getAnnotation(AnnotatedElement ae, Class<T> annotationType) {
        //获取注解
        T ann = ae.getAnnotation(annotationType);
        //若不存在此注解
        if (ann == null) {
            //遍历注解
            for (Annotation metaAnn : ae.getAnnotations()) {
                //获得注解的注解
                ann = metaAnn.annotationType().getAnnotation(annotationType);
                if (ann != null) {
                    break;
                }
            }
        }
        return ann;
    }


}
