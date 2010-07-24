/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.branch.management.change;

import org.eclipse.osee.framework.core.enums.ConflictStatus;
import org.eclipse.osee.framework.core.enums.ConflictType;
import org.eclipse.osee.framework.core.enums.StorageState;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.IOseeStorable;
import org.eclipse.osee.framework.core.model.MergeBranch;

public final class Conflict implements IOseeStorable {
   private final MergeBranch mergeBranch;
   private final ConflictStatus conflictStatus;
   private final int itemId;
   private final Long sourceGammaId;
   private final Long destinationGammaId;
   private final ConflictType conflictType;

   private StorageState storageState;
   private boolean isDirty;

   public Conflict(StorageState storageState, int itemId, ConflictType conflictType, MergeBranch mergeBranch, ConflictStatus conflictStatus, long sourceGammaId, long destinationGammaId) {
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

   public Long getSourceGammaId() {
      return sourceGammaId;
   }

   public Long getDestinationGammaId() {
      return destinationGammaId;
   }

   public int getId() {
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
   public boolean isIdValid() {
      return IOseeStorable.UNPERSISTED_VALUE != getId();
   }

   @Override
   public void setId(int uniqueId) throws OseeCoreException {
      throw new UnsupportedOperationException();
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