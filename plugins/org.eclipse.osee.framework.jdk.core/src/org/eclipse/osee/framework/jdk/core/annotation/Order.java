/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.jdk.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@code @Order} is an annotation that is used to configure the {@linkplain #value order} in which the annotated method
 * or field will be evaluated or executed relative to other elements of the same category. {@code @Order} is patterned
 * after {@code org.junit.jupiter.api.Order}
 *
 * @author Ryan D. Brooks
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Order {

   /**
    * The value for the annotated method or field which will be used to place it in ascending value order
    */
   int value();
}