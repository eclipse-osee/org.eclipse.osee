/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.framework.jdk.core.type;

/**
 * @author Stephen J. Molaro
 */

@FunctionalInterface
public interface QuinFunction<T, U, V, W, X, R> {

   /**
    * Applies this function to the given arguments.
    *
    * @param t the first function argument
    * @param u the second function argument
    * @return the function result
    */
   R apply(T t, U u, V v, W w, X x);

}
