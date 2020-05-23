/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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
 * This class implements a boolean that can be passed around and modified through a group of methods.
 * 
 * @author Chris Austin
 */
public class MutableBoolean {
   private boolean value;

   public MutableBoolean(boolean value) {
      this.value = value;
   }

   public boolean getValue() {
      return value;
   }

   public void setValue(boolean value) {
      this.value = value;
   }

   @Override
   public String toString() {
      return Boolean.toString(value);
   }
}
