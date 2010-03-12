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

/**
 * @author Donald G. Dunne
 */
public abstract class SimpleEnum {

   public final transient int value;
   public final String name;

   protected SimpleEnum(int value, String name) {
      this.value = value;
      this.name = name;
   }

   public final String toString() {
      return name;
   }

   public final boolean equals(Object obj) {
      return super.equals(obj);
   }

   public final int hashCode() {
      return value;
   }

}
