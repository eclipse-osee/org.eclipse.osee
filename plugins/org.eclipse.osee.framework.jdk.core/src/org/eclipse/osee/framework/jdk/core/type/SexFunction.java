/*********************************************************************
 * Copyright (c) 2021 Boeing
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
public interface SexFunction<T, U, V, W, X, Y, R> {

   /**
    * Applies this function to the given arguments.
    *
    * @param t the first function argument
    * @param u the second function argument
    * @param v the third function argument
    * @param w the fourth function argument
    * @param x the fifth function argument
    * @param y the sixth function argument
    * @return the function result
    */
   R apply(T t, U u, V v, W w, X x, Y y);

}