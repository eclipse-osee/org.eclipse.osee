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
package org.eclipse.osee.orcs.db.mock;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.jdk.core.util.annotation.AnnotationProcessor;
import org.eclipse.osee.framework.jdk.core.util.annotation.FieldAnnotationHandler;
import org.eclipse.osee.orcs.db.mock.internal.OsgiServiceFieldAnnotationHandler;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

/**
 * @author Roberto E. Escobar
 */
public class OsgiRule extends TestWatcher {

   private static final AnnotationProcessor processor = createProcessor();
   private final Object[] objects;

   public OsgiRule(Object... objects) {
      this.objects = objects;
   }

   @Override
   protected void starting(Description description) {
      super.starting(description);
      try {
         processor.initAnnotations(objects);
      } catch (Exception ex) {
         throw new RuntimeException(ex);
      }
   }

   private static AnnotationProcessor createProcessor() {
      Map<Class<? extends Annotation>, FieldAnnotationHandler<?>> annotationHandlers =
         new HashMap<>();

      annotationHandlers.put(OsgiService.class, new OsgiServiceFieldAnnotationHandler());
      return new AnnotationProcessor(annotationHandlers);
   }
}