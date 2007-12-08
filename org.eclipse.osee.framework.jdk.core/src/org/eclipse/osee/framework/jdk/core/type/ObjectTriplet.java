/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.jdk.core.type;

public class ObjectTriplet<T, U, V> {
   public final T object1;
   public final U object2;
   public final V object3;

   public ObjectTriplet(T object1, U object2, V object3) {
      this.object1 = object1;
      this.object2 = object2;
      this.object3 = object3;
   }
}