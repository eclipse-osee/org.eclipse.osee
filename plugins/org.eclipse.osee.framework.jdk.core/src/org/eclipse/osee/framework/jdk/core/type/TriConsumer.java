/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.framework.jdk.core.type;

import java.util.function.Consumer;

/**
 * This is the tri-arity specialization of {@link Consumer}. Unlike most other functional interfaces,
 * {@code TriConsumer} is expected to operate via side-effects.
 *
 * @author Ryan D. Brooks
 */
public interface TriConsumer<T, U, V> {

   /**
    * Performs this operation on the given arguments.
    *
    * @param t the first input argument
    * @param u the second input argument
    */
   void accept(T t, U u, V v);
}
