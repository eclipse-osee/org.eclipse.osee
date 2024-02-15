/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.framework.core.data;

import org.eclipse.osee.framework.core.enums.ConflictStatus;
import org.eclipse.osee.framework.core.enums.ConflictType;

public class ConflictUpdateData {
   private final GammaId sourceGammaId;
   private final GammaId destGammaId;
   private final ConflictStatus status;
   private final ConflictType type;
   private final Long conflictId;
   private final BranchId mergeBranchId;

   public ConflictUpdateData(ConflictType type, ConflictStatus status, GammaId sourceGammaId, BranchId mergeBranchId, GammaId destDestGammaId, Long conflictId) {
      this.sourceGammaId = sourceGammaId;
      this.destGammaId = destDestGammaId;
      this.status = status;
      this.type = type;
      this.conflictId = conflictId;
      this.mergeBranchId = mergeBranchId;
   }

   public ConflictUpdateData() {
      this.sourceGammaId = null;
      this.destGammaId = null;
      this.status = null;
      this.type = null;
      this.conflictId = null;
      this.mergeBranchId = null;
   }

   public GammaId getSourceGammaId() {
      return sourceGammaId;
   }

   public GammaId getDestGammaId() {
      return destGammaId;
   }

   public ConflictStatus getStatus() {
      return status;
   }

   public ConflictType getType() {
      return type;
   }

   public Long getConflictId() {
      return conflictId;
   }

   public BranchId getMergeBranchId() {
      return mergeBranchId;
   }
}
