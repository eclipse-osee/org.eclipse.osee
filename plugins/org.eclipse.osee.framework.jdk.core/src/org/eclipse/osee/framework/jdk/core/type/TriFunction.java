/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.jdk.core.type;

/**
 * @author Stephen J. Molaro
 */

@FunctionalInterface
public interface TriFunction<T, U, V, R> {

   /**
    * Applies this function to the given arguments.
    *
    * @param t the first function argument
    * @param u the second function argument
    * @return the function result
    */
   R apply(T t, U u, V v);

}
