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
package org.eclipse.osee.orcs.core.ds;

import org.eclipse.osee.framework.core.enums.ModificationType;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactRow {
   private int artifactId = -1;
   private int branchId = -1;

   private int transactionId = -1;
   private int stripeId = -1;

   private long artTypeUuid = -1;

   private String guid = null;
   private String humanReadableId = null;

   private int gammaId = -1;
   private ModificationType modType = null;
   private boolean isHistorical = false;

   public ArtifactRow() {
      // do nothing
   }

   public int getArtifactId() {
      return artifactId;
   }

   public int getBranchId() {
      return branchId;
   }

   public int getTransactionId() {
      return transactionId;
   }

   public int getStripeId() {
      return stripeId;
   }

   public long getArtTypeUuid() {
      return artTypeUuid;
   }

   public String getGuid() {
      return guid;
   }

   public String getHumanReadableId() {
      return humanReadableId;
   }

   public int getGammaId() {
      return gammaId;
   }

   public ModificationType getModType() {
      return modType;
   }

   public boolean isHistorical() {
      return isHistorical;
   }

   public void setArtifactId(int artifactId) {
      this.artifactId = artifactId;
   }

   public void setBranchId(int branchId) {
      this.branchId = branchId;
   }

   public void setTransactionId(int transactionId) {
      this.transactionId = transactionId;
   }

   public void setStripeId(int stripeId) {
      this.stripeId = stripeId;
   }

   public void setArtTypeUuid(long artTypeUuid) {
      this.artTypeUuid = artTypeUuid;
   }

   public void setGuid(String guid) {
      this.guid = guid;
   }

   public void setHumanReadableId(String humanReadableId) {
      this.humanReadableId = humanReadableId;
   }

   public void setGammaId(int gammaId) {
      this.gammaId = gammaId;
   }

   public void setModType(ModificationType modType) {
      this.modType = modType;
   }

   public void setHistorical(boolean isHistorical) {
      this.isHistorical = isHistorical;
   }

   @Override
   public String toString() {
      return "ArtifactRow [artifactId=" + artifactId + ", branchId=" + branchId + ", transactionId=" + transactionId + ", artTypeUuid=" + artTypeUuid + ", guid=" + guid + ", humanReadableId=" + humanReadableId + ", gammaId=" + gammaId + ", modType=" + modType + "]";
   }

}
