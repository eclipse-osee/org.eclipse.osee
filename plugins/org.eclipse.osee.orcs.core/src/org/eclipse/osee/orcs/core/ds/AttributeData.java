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

/**
 * @author Roberto E. Escobar
 */
public class AttributeData extends OrcsObject {

   private int stripeId = -1;
   private int artifactId = -1;
   private String uri;
   private String value;

   private DataProxy proxy;

   public AttributeData() {
      // do nothing
   }

   public String getUri() {
      return uri;
   }

   public void setUri(String uri) {
      this.uri = uri;
   }

   public void setValue(String value) {
      this.value = value;
   }

   public String getValue() {
      return value;
   }

   public int getArtifactId() {
      return artifactId;
   }

   public int getAttrId() {
      return getLocalId();
   }

   public void setAttrId(int attrId) {
      setLocalId(attrId);
   }

   public long getAttrTypeUuid() {
      return getTypeUuid();
   }

   public DataProxy getDataProxy() {
      return proxy;
   }

   public int getStripeId() {
      return stripeId;
   }

   public void setArtifactId(int artifactId) {
      this.artifactId = artifactId;
   }

   public void setAttrTypeUuid(long attrTypeUuid) {
      setTypeUuid(attrTypeUuid);
   }

   public void setStripeId(int stripeId) {
      this.stripeId = stripeId;
   }

   public void setDataProxy(DataProxy proxy) {
      this.proxy = proxy;
   }

   public boolean isSameArtifact(AttributeData other) {
      return getBranchId() == other.getBranchId() && this.artifactId == other.artifactId;
   }

   public boolean isSameAttribute(AttributeData other) {
      return getLocalId() == other.getLocalId() && isSameArtifact(other);
   }

   @Override
   public String toString() {
      return "AttributeRow [artifactId=" + artifactId + ", branchId=" + getBranchId() + ", attrId=" + getLocalId() + ", gammaId=" + getGammaId() + ", modType=" + getModType() + "]";
   }

}