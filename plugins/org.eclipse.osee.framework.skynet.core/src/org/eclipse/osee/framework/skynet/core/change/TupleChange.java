/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.framework.skynet.core.change;

import java.util.StringTokenizer;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.TupleTypeId;
import org.eclipse.osee.framework.core.enums.CoreTupleTypes;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.model.TransactionDelta;
import org.eclipse.osee.framework.core.model.change.ChangeType;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;

/**
 * @author Angel Avila
 */
public class TupleChange extends Change {

   private final String isValue;
   private final String wasValue;
   private final String itemKind;
   private final TupleTypeId itemTypeId;
   private String itemTypeDescription;
   private String detailedIsValue;
   private String detailedWasValue;

   public TupleChange(BranchId branch, GammaId sourceGamma, TransactionDelta txDelta, ModificationType modType, TupleTypeId itemTypeId, String isValue, String wasValue, String itemKind, boolean isHistorical) {
      super(branch, sourceGamma, ArtifactId.valueOf(0L), txDelta, modType, isHistorical, Artifact.SENTINEL, null);
      this.itemTypeId = itemTypeId;
      this.itemKind = itemKind;
      this.isValue = isValue;
      this.wasValue = wasValue;
      if (getItemTypeId().getId().equals(CoreTupleTypes.ViewApplicability.getId())) {
         String value = isValue.isEmpty() ? wasValue : isValue;
         StringTokenizer tok = new StringTokenizer(value, ",");

         String cfgId = "";
         String appId = "";

         if (tok.hasMoreTokens()) {
            cfgId = tok.nextToken();
         }
         if (tok.hasMoreElements()) {
            appId = tok.nextToken().trim();
         }
         String cfg = ServiceUtil.getOseeClient().getApplicabilityEndpoint(getBranch()).getView(cfgId).getName();
         if (cfg.isEmpty()) {
            cfg =
               ServiceUtil.getOseeClient().getApplicabilityEndpoint(getBranch()).getConfigurationGroup(cfgId).getName();
         }
         ApplicabilityToken tag =
            ServiceUtil.getOseeClient().getApplicabilityEndpoint(getBranch()).getApplicabilityTokenFromId(appId);
         itemTypeDescription = cfg;
         if (isValue.isEmpty()) {
            detailedWasValue = tag.getName();
            detailedIsValue = isValue;
         } else {
            detailedIsValue = tag.getName();
            detailedWasValue = wasValue;
         }

      }

   }

   @Override
   public TupleTypeId getItemTypeId() {
      return itemTypeId;
   }

   @Override
   public String getIsValue() {
      if (getItemTypeId().getId().equals(CoreTupleTypes.ViewApplicability.getId())) {
         return detailedIsValue;
      }
      return isValue;
   }

   @Override
   public String getWasValue() {
      if (getItemTypeId().getId().equals(CoreTupleTypes.ViewApplicability.getId())) {
         return detailedWasValue;
      }
      return wasValue;
   }

   @Override
   public String getItemTypeName() {
      if (getItemTypeId().getId().equals(CoreTupleTypes.ViewApplicability.getId())) {
         return itemTypeDescription;
      }
      return itemTypeId.getIdString();
   }

   @Override
   public String getName() {
      if (getItemTypeId().getId().equals(CoreTupleTypes.ViewApplicability.getId())) {
         return ("Product Line Configuration");
      }
      return "N/A";
   }

   @Override
   public String getNameOrToken() {
      return getName();
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
   public ChangeType getChangeType() {
      return ChangeType.Tuple;
   }

   public String getItemTypeDescription() {
      return itemTypeDescription;
   }

   public void setItemTypeDescription(String itemTypeDescription) {
      this.itemTypeDescription = itemTypeDescription;
   }

   public String getDetailedWasValue() {
      return detailedWasValue;
   }

   public void setDetailedWasValue(String detailedWasValue) {
      this.detailedWasValue = detailedWasValue;
   }

   public String getDetailedIsValue() {
      return detailedIsValue;
   }

   public void setDetailedIsValue(String detailedIsValue) {
      this.detailedIsValue = detailedIsValue;
   }
}
