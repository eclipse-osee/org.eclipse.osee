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

import java.lang.reflect.Field;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.db.mock.internal.OsgiUtil;
import org.junit.rules.TestWatchman;
import org.junit.runners.model.FrameworkMethod;

public class OsgiRule extends TestWatchman {

   private final Object[] objects;

   public OsgiRule(Object... objects) {
      this.objects = objects;
   }

   @Override
   public void starting(FrameworkMethod method) {
      try {
         for (Object object : objects) {
            initAnnotations(object);
         }
      } catch (Exception ex) {
         throw new RuntimeException(ex);
      }
   }

   public static void initAnnotations(Object testClass) throws Exception {
      if (testClass == null) {
         throw new OseeCoreException(
            "testClass cannot be null. For info how to use @OsgiService annotations see examples");
      }

      Class<?> clazz = testClass.getClass();
      while (clazz != Object.class) {
         scan(testClass, clazz);
         clazz = clazz.getSuperclass();
      }
   }

   private static void scan(Object object, Class<?> clazz) throws Exception {
      Field[] fields = clazz.getDeclaredFields();
      for (Field field : fields) {
         if (field.isAnnotationPresent(OsgiService.class)) {
            OsgiService annotation = field.getAnnotation(OsgiService.class);
            injectToFields(annotation, object, field);
         }
      }
   }

   private static void injectToFields(OsgiService annotation, Object object, Field field) throws Exception {
      boolean wasAccessible = field.isAccessible();
      field.setAccessible(true);
      try {
         Object service = OsgiUtil.getService(field.getType());
         field.set(object, service);
      } catch (IllegalAccessException e) {
         throw new Exception("Problems injecting dependencies in " + field.getName(), e);
      } finally {
         field.setAccessible(wasAccessible);
      }
   }
}