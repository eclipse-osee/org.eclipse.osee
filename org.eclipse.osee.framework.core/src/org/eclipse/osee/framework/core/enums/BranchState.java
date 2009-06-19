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
package org.eclipse.osee.framework.core.enums;

/**
 * @author Roberto E. Escobar
 */
public enum BranchState {
   UNKNOWN(-1),
   CREATED(0),
   MODIFIED(1),
   COMMITTED(2),
   REBASELINED(3),
   DELETED(4),
   REBASELINE_IN_PROGRESS(5),
   COMMIT_IN_PROGRESS(6);

   private final int value;

   BranchState(int value) {
      this.value = value;
   }

   public final int getValue() {
      return value;
   }

   public static BranchState getBranchState(int value) {
      for (BranchState type : values()) {
         if (type.getValue() == value) {
            return type;
         }
      }
      return null;
   }
}
