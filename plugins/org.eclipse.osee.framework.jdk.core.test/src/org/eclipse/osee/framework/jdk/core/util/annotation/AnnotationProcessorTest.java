/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.jdk.core.util.annotation;

import static org.junit.Assert.assertEquals;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.jdk.core.annotation.AbstractFieldAnnotationHandler;
import org.eclipse.osee.framework.jdk.core.annotation.AnnotationProcessor;
import org.eclipse.osee.framework.jdk.core.annotation.FieldAnnotationHandler;
import org.junit.Before;
import org.junit.Test;

/**
 * Test Case for {@link AnnotationProcessor}
 * 
 * @author Roberto E. Escobar
 */
public class AnnotationProcessorTest {

   private Map<Class<? extends Annotation>, FieldAnnotationHandler<?>> annotationHandlers;
   private AnnotationProcessor processor;

   @Before
   public void setup() {
      annotationHandlers = new HashMap<>();

      annotationHandlers.put(Annotation1.class, new Handler1());
      annotationHandlers.put(Annotation2.class, new Handler2());

      processor = new AnnotationProcessor(annotationHandlers);
   }

   @Test
   public void testFieldInjection() throws Exception {
      Child child = new Child();
      processor.initAnnotations(child);

      assertEquals("Handler1", child.getField1());
      assertEquals("Handler2", child.getField2());
   }

   private class Base {

      @Annotation2
      private String field2;

      public String getField2() {
         return field2;
      }
   };

   private class Child extends Base {

      @Annotation1
      private String field1;

      public String getField1() {
         return field1;
      }
   }

   private class Handler1 extends AbstractFieldAnnotationHandler<Annotation1> {

      @Override
      public void handleAnnotation(Annotation1 annotation, Object object, Field field) throws Exception {
         injectToFields(annotation, object, field, "Handler1");
      }
   }

   private class Handler2 extends AbstractFieldAnnotationHandler<Annotation2> {

      @Override
      public void handleAnnotation(Annotation2 annotation, Object object, Field field) throws Exception {
         injectToFields(annotation, object, field, "Handler2");
      }

   }

}
