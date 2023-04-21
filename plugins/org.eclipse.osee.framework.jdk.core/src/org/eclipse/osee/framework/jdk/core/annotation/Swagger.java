/*********************************************************************
 * Copyright (c) 2023 Boeing
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

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Custom annotation to allow the Swagger implementation to include the classes in its automated definition files
 * generation. Any endpoint classes utilizing a @Path annotation that are called by other endpoint classes with @Path
 * annotations should not utilize this annotation, as Swagger cannot associate the class being called properly. Refer to
 * the BranchesResource class for an example.
 *
 * @author Dominic Guss
 */
@Documented
@Retention(RUNTIME)
@Target({TYPE, PARAMETER, FIELD})
public @interface Swagger {
   // Do nothing
}
