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

public class ObjectPair<O, T> {
   public final O object1;
   public final T object2;

   public ObjectPair(O object1, T object2) {
      this.object1 = object1;
      this.object2 = object2;
   }

   @Override
   public String toString() {
      return "<" + object1 + ", " + object2 + ">";
   }
}
