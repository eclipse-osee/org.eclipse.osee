/*********************************************************************
 * Copyright (c) 2020 Boeing
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