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
package org.eclipse.osee.framework.messaging.id;

import java.io.Serializable;

/**
 * @author Andrew M. Finkbeiner
 */
public class StringName implements Name, Serializable {
   private static final long serialVersionUID = -7215226960243262972L;
   private String name;

   public StringName(String name) {
      this.name = name;
   }
   
   @Override
   public String toString() {
      return name;
   }

   @Override
   public boolean equals(Object arg0) {
      if (arg0 instanceof StringName) {
         return name.equals(((StringName) arg0).name);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return name.hashCode();
   }

}
