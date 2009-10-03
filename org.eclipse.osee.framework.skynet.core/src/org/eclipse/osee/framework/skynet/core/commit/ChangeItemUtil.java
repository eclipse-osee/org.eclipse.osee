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
package org.eclipse.osee.framework.skynet.core.commit;

/**
 * @author Roberto E. Escobar
 */
public class ChangeItemUtil {

   private ChangeItemUtil() {
   }

   public static boolean wasNewOnSource(ChangeItem changeItem) {
      return changeItem.getFirst().isNew() || changeItem.getCurrent().isNew();
   }

   public static boolean wasIntroducedOnSource(ChangeItem changeItem) {
      return changeItem.getFirst().isIntroduced() || changeItem.getCurrent().isIntroduced();
   }

   public static boolean wasNewOrIntroducedOnSource(ChangeItem changeItem) {
      return wasNewOnSource(changeItem) || wasIntroducedOnSource(changeItem);
   }

   public static boolean isAlreadyOnDestination(ChangeItem changeItem) {
      return changeItem.getCurrent().sameGammaAs(changeItem.getDestination()) && //
      changeItem.getCurrent().getModType().isDeleted() == changeItem.getDestination().getModType().isDeleted();
   }

   public static boolean isIgnoreCase(boolean hasDestinationBranch, ChangeItem changeItem) {
      return //
      wasCreatedAndDeleted(changeItem) || //
      isAlreadyOnDestination(changeItem) || //
      hasDestinationBranch && isDeletedAndDoestNotExistInDestination(changeItem) || //
      hasBeenDeletedInDestination(changeItem) || //
      isDestinationEqualOrNewerThanCurrent(changeItem);
   }

   public static boolean wasCreatedAndDeleted(ChangeItem changeItem) {
      return wasNewOrIntroducedOnSource(changeItem) && changeItem.getCurrent().getModType().isDeleted();
   }

   public static boolean isDeletedAndDoestNotExistInDestination(ChangeItem changeItem) {
      return !changeItem.getDestination().exists() && changeItem.getCurrent().getModType().isDeleted();
   }

   public static boolean hasBeenDeletedInDestination(ChangeItem changeItem) {
      return changeItem.getDestination().exists() && changeItem.getDestination().getModType().isDeleted();
   }

   public static boolean isDestinationEqualOrNewerThanCurrent(ChangeItem changeItem) {
      return (changeItem.getCurrent().isNew() || changeItem.getCurrent().isIntroduced()) && changeItem.getDestination().exists();
   }
}
