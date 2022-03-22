/*********************************************************************
 * Copyright (c) 2022 Boeing
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
package org.eclipse.osee.mim.types;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.model.change.ChangeItem;

/**
 * @author Ryan T. Baldwin
 */
public class MimDifferenceReportItem {
   private final PLGenericDBObject item;
   private final List<ChangeItem> changes;
   private final List<ArtifactId> parents;

   public MimDifferenceReportItem(PLGenericDBObject item, List<ChangeItem> changes) {
      this.item = item;
      this.changes = changes;
      this.parents = new LinkedList<>();
   }

   public PLGenericDBObject getItem() {
      return item;
   }

   public List<ChangeItem> getChanges() {
      return changes;
   }

   public List<ArtifactId> getParents() {
      return parents;
   }

   public void addParent(ArtifactId artId) {
      parents.add(artId);
   }

   @Override
   public String toString() {
      return this.item.toString();
   }

}