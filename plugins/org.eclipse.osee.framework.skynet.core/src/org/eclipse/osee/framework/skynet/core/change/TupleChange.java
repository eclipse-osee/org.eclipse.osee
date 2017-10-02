/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.change;

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.TupleTypeId;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.skynet.core.revision.LoadChangeType;

/**
 * @author Angel Avila
 */
public class TupleChange extends Change {

   private final String isValue;
   private final String wasValue;
   private final String itemKind;
   private final TupleTypeId itemTypeId;

   public TupleChange(BranchId branch, GammaId sourceGamma, ModificationType modType, TupleTypeId itemTypeId, String isValue, String wasValue, String itemKind, boolean isHistorical) {
      super(branch, sourceGamma, ArtifactId.valueOf(0L), null, modType, isHistorical, null, null);
      this.itemTypeId = itemTypeId;
      this.isValue = isValue;
      this.wasValue = wasValue;
      this.itemKind = itemKind;
   }

   @Override
   public TupleTypeId getItemTypeId() {
      return itemTypeId;
   }

   @Override
   public String getIsValue() {
      return isValue;
   }

   @Override
   public String getWasValue() {
      return wasValue;
   }

   @Override
   public String getItemTypeName() {
      return itemTypeId.getIdString();
   }

   @Override
   public String getName() {
      return "N/A";
   }

   @Override
   public String getItemKind() {
      return itemKind;
   }

   @Override
   public Id getItemId() {
      return getGamma();
   }

   @Override
   public LoadChangeType getChangeType() {
      return LoadChangeType.tuple;
   }
}
