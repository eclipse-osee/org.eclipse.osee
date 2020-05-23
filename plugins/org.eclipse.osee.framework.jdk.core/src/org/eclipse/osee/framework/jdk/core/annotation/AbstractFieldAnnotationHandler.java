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