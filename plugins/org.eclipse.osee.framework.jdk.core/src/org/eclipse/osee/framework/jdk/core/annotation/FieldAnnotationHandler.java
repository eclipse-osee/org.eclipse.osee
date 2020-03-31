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
public interface FieldAnnotationHandler<T extends Annotation> {

   void handleAnnotation(T annotation, Object object, Field field) throws Exception;

}