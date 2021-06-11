/*********************************************************************
 * Copyright (c) 2021 Boeing
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

package org.eclipse.osee.orcs.db.internal.loader.data;

import org.eclipse.osee.framework.core.data.BranchCategoryToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.orcs.core.ds.BranchCategoryData;
import org.eclipse.osee.orcs.core.ds.OrcsVersionedObjectImpl;
import org.eclipse.osee.orcs.core.ds.VersionData;

/**
 * @author Audrey Denk
 */
public class BranchCategoryDataImpl extends OrcsVersionedObjectImpl<BranchCategoryToken> implements BranchCategoryData {

   private BranchId branchId;
   private BranchCategoryToken category;
   private boolean useBackingData = false;

   public BranchCategoryDataImpl(VersionData version) {
      super(version);
   }

   @Override
   public boolean isExistingVersionUsed() {
      return useBackingData;
   }

   @Override
   public void setUseBackingData(boolean useBackingData) {
      this.useBackingData = useBackingData;
   }

   @Override
   public BranchId getBranchId() {
      return branchId;
   }

   @Override
   public BranchCategoryToken getCategory() {
      return category;
   }

   @Override
   public void setBranchId(BranchId branchId) {
      this.branchId = branchId;
   }

   @Override
   public void setCategory(BranchCategoryToken category) {
      this.category = category;
   }

}
