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
package org.eclipse.osee.framework.jdk.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractFieldAnnotationHandler<T extends Annotation> implements FieldAnnotationHandler<T> {

   protected void injectToFields(T annotation, Object object, Field field, Object toInject) throws Exception {
      boolean wasAccessible = field.isAccessible();
      field.setAccessible(true);
      try {
         field.set(object, toInject);
      } catch (Error e) {
         String msg = String.format("Problems injecting dependencies in [%s]", field.getName());
         throw new Exception(msg, e);
      } finally {
         field.setAccessible(wasAccessible);
      }
   }
}