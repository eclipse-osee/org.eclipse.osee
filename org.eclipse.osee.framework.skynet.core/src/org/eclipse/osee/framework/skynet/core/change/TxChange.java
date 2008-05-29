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

package org.eclipse.osee.framework.skynet.core.change;

/**
 * @author Ryan D. Brooks
 */
public enum TxChange {
   NOT_CURRENT(0), CURRENT(1), DELETED(2);

   private int value;

   private TxChange(int value) {
      this.value = value;
   }

   public int getValue() {
      return value;
   }

   public static TxChange getChangeType(int value) {
      for (TxChange change : values())
         if (change.getValue() == value) return change;
      return null;
   }

}
