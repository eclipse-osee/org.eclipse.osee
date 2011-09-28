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
public class AttributeRow {
   private int artifactId = -1;
   private int branchId = -1;
   private int attrId = -1;
   private int gammaId = -1;
   private ModificationType modType = null;
   private int transactionId = -1;
   private long attrTypeUuid = -1;
   private String value = "";
   private int stripeId = -1;
   private String uri = "";
   private boolean isHistorical = false;

   public AttributeRow() {
      // do nothing
   }

   public int getArtifactId() {
      return artifactId;
   }

   public int getBranchId() {
      return branchId;
   }

   public int getAttrId() {
      return attrId;
   }

   public int getGammaId() {
      return gammaId;
   }

   public ModificationType getModType() {
      return modType;
   }

   public int getTransactionId() {
      return transactionId;
   }

   public long getAttrTypeUuid() {
      return attrTypeUuid;
   }

   public String getValue() {
      return value;
   }

   public int getStripeId() {
      return stripeId;
   }

   public String getUri() {
      return uri;
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

   public void setAttrId(int attrId) {
      this.attrId = attrId;
   }

   public void setGammaId(int gammaId) {
      this.gammaId = gammaId;
   }

   public void setModType(ModificationType modType) {
      this.modType = modType;
   }

   public void setTransactionId(int transactionId) {
      this.transactionId = transactionId;
   }

   public void setAttrTypeUuid(long attrTypeUuid) {
      this.attrTypeUuid = attrTypeUuid;
   }

   public void setValue(String value) {
      this.value = value;
   }

   public void setStripeId(int stripeId) {
      this.stripeId = stripeId;
   }

   public void setUri(String uri) {
      this.uri = uri;
   }

   public void setHistorical(boolean isHistorical) {
      this.isHistorical = isHistorical;
   }

   public boolean isSameArtifact(AttributeRow other) {
      return this.branchId == other.branchId && this.artifactId == other.artifactId;
   }

   public boolean isSameAttribute(AttributeRow other) {
      return this.attrId == other.attrId && isSameArtifact(other);
   }

   @Override
   public String toString() {
      return "AttributeRow [artifactId=" + artifactId + ", branchId=" + branchId + ", attrId=" + attrId + ", gammaId=" + gammaId + ", modType=" + modType + "]";
   }

}