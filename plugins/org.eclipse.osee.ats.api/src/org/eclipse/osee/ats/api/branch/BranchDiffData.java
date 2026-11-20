/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.ats.api.branch;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.model.change.ChangeItem;

/**
 * In-memory carrier for the branch diff (list of change items) associated with a Team Workflow. The change items are
 * persisted on {@code CoreAttributeTypes.BranchDiffData} as the bare JSON list; this class is only the API type used by
 * {@code IAtsBranchService} read/write, not the stored format.
 *
 * @author Donald G. Dunne
 */
public class BranchDiffData {

   private List<ChangeItem> changeItems = new ArrayList<>();

   public BranchDiffData() {
      // Needed for jax-rs serialization
   }

   public BranchDiffData(List<ChangeItem> changeItems) {
      this.changeItems = changeItems;
   }

   public List<ChangeItem> getChangeItems() {
      return changeItems;
   }

   public void setChangeItems(List<ChangeItem> changeItems) {
      this.changeItems = changeItems;
   }

   public boolean isEmpty() {
      return changeItems == null || changeItems.isEmpty();
   }
}
