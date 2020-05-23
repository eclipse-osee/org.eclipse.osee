/*********************************************************************
 * Copyright (c) 2012 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.jdk.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author Roberto E. Escobar
 */
public class AnnotationProcessor {

   private final Map<Class<? extends Annotation>, FieldAnnotationHandler<?>> annotationHandlers;

   public AnnotationProcessor(Map<Class<? extends Annotation>, FieldAnnotationHandler<? extends Annotation>> annotationHandlers) {
      this.annotationHandlers = annotationHandlers;
   }

   public void initAnnotations(Object... objects) throws Exception {
      if (objects == null) {
         throw new Exception("objects cannot be null");
      }
      for (Object object : objects) {
         initAnnotations(object);
      }
   }

   public void initAnnotations(Object object) throws Exception {
      if (object == null) {
         throw new Exception("object cannot be null");
      }
      Class<?> clazz = object.getClass();
      while (clazz != Object.class) {
         scan(object, clazz);
         clazz = clazz.getSuperclass();
      }
   }

   private void scan(Object object, Class<?> clazz) throws Exception {
      Field[] fields = clazz.getDeclaredFields();
      for (Field field : fields) {
         Annotation[] annotations = field.getAnnotations();
         for (Annotation annotation : annotations) {
            processAnnotation(object, field, annotation);
         }
      }
   }

   private <A extends Annotation> void processAnnotation(Object object, Field field, A annotation) throws Exception {
      FieldAnnotationHandler<A> handler = getHandler(annotation);
      if (handler != null) {
         handler.handleAnnotation(annotation, object, field);
      }
   }

   @SuppressWarnings("unchecked")
   private <T extends FieldAnnotationHandler<? extends Annotation>> T getHandler(Annotation annotation) {
      return (T) annotationHandlers.get(annotation.annotationType());
   }
}