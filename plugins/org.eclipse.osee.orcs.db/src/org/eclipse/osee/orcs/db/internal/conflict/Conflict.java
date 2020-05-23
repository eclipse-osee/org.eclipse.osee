/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.orcs.db.internal.conflict;

import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.enums.ConflictStatus;
import org.eclipse.osee.framework.core.enums.ConflictType;
import org.eclipse.osee.framework.core.enums.StorageState;
import org.eclipse.osee.framework.core.model.IOseeStorable;
import org.eclipse.osee.framework.core.model.MergeBranch;
import org.eclipse.osee.framework.jdk.core.type.Id;

public final class Conflict implements IOseeStorable {
   private final MergeBranch mergeBranch;
   private final ConflictStatus conflictStatus;
   private final Id itemId;
   private final GammaId sourceGammaId;
   private final GammaId destinationGammaId;
   private final ConflictType conflictType;

   private StorageState storageState;
   private boolean isDirty;

   public Conflict(StorageState storageState, Id itemId, ConflictType conflictType, MergeBranch mergeBranch, ConflictStatus conflictStatus, GammaId sourceGammaId, GammaId destinationGammaId) {
      this.mergeBranch = mergeBranch;
      this.storageState = storageState;
      this.conflictStatus = conflictStatus;
      this.itemId = itemId;
      this.conflictType = conflictType;
      this.sourceGammaId = sourceGammaId;
      this.destinationGammaId = destinationGammaId;
      this.isDirty = false;
   }

   public ConflictType getType() {
      return conflictType;
   }

   public GammaId getSourceGammaId() {
      return sourceGammaId;
   }

   public GammaId getDestinationGammaId() {
      return destinationGammaId;
   }

   public Id getId() {
      return itemId;
   }

   public MergeBranch getMergeBranch() {
      return mergeBranch;
   }

   public ConflictStatus getStatus() {
      return conflictStatus;
   }

   @Override
   public StorageState getStorageState() {
      return storageState;
   }

   @Override
   public void clearDirty() {
      this.isDirty = false;
      this.storageState = StorageState.LOADED;
   }

   @Override
   public boolean isDirty() {
      return isDirty;
   }
}