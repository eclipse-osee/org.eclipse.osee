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
 * @author Ryan Schmitt
 */

public enum DeletionFlag {
   INCLUDE_DELETED,
   INCLUDE_HARD_DELETED,
   EXCLUDE_DELETED;

   public boolean areDeletedAllowed() {
      return this == INCLUDE_DELETED;
   }

   public static DeletionFlag allowDeleted(boolean areAllowed) {
      return areAllowed ? DeletionFlag.INCLUDE_DELETED : DeletionFlag.EXCLUDE_DELETED;
   }
}
