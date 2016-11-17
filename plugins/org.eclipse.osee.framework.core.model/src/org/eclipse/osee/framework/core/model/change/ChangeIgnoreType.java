/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.model.change;

/**
 * @author Megumi Telles
 */
public enum ChangeIgnoreType {

   SENTINEL,
   CREATED_AND_DELETED,
   ALREADY_ON_DESTINATION,
   DELETED_AND_DNE_ON_DESTINATION,
   DELETED_ON_DESTINATION,
   REPLACED_WITH_VERSION,
   RESURRECTED,
   DELETED_ON_DEST_AND_NOT_RESURRECTED,
   REPLACED_WITH_VERSION_AND_NOT_RESURRECTED,
   NONE;

   public boolean isCreatedAndDeleted() {
      return this == CREATED_AND_DELETED;
   }

   public boolean isAlreadyOnDestination() {
      return this == ALREADY_ON_DESTINATION;
   }

   public boolean isDeletedAndDneOnDest() {
      return this == DELETED_AND_DNE_ON_DESTINATION;
   }

   public boolean isDeletedOnDestination() {
      return this == DELETED_ON_DESTINATION;
   }

   public boolean isReplacedWithVersion() {
      return this == REPLACED_WITH_VERSION;
   }

   public boolean isResurrected() {
      return this == RESURRECTED;
   }

   public boolean isDeletedOnDestAndNotResurrected() {
      return this == DELETED_ON_DEST_AND_NOT_RESURRECTED;
   }

   public boolean isReplacedWithVerAndNotResurrected() {
      return this == REPLACED_WITH_VERSION_AND_NOT_RESURRECTED;
   }

   public boolean isNone() {
      return this == NONE;
   }

   public boolean isValid() {
      return this != SENTINEL;
   }

   public boolean isInvalid() {
      return !isValid();
   }
}
