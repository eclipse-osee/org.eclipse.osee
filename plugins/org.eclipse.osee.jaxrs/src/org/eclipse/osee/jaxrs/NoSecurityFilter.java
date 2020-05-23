/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.jaxrs;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation for resource classes or resource methods that should be ignored by the REST Security Filter. The filter
 * will not perform authentication/authorization checks for resources with this annotation and will leave the
 * {@link javax.ws.rs.core.SecurityContext} unchanged.
 * <p/>
 * The annotation can be placed on a resource method directly or a resource class.
 * 
 * @author Roberto E. Escobar
 */
@Documented
@Retention(RUNTIME)
@Target({TYPE, METHOD})
public @interface NoSecurityFilter {
   //
}
