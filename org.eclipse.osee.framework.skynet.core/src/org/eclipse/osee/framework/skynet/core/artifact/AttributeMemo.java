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
package org.eclipse.osee.framework.skynet.core.artifact;

/**
 * @author Robert A. Fisher
 */
public class AttributeMemo {
   private int attrId;
   private int gammaId;
   private boolean deleted;
   private boolean dirty;

   public AttributeMemo() {
   }

   /**
    * @param attrId
    * @param attrTypeId
    */
   public AttributeMemo(int attrId, int gammaId) {
      this.attrId = attrId;
      this.gammaId = gammaId;
   }

   /**
    * @return Returns the attrId.
    */
   public int getAttrId() {
      return attrId;
   }

   public int getGammaId() {
      return gammaId;
   }

   public void setDeleted(boolean isDeleted) {
      this.deleted = isDeleted;
   }

   /**
    * Set this attribute as not dirty. Should only be called my the persistence manager once it has persisted this
    * attribute.
    */
   public void setDirty(boolean dirty) {
      this.dirty = dirty;
   }

   /**
    * @return Returns the dirty.
    */
   public boolean isDirty() {
      return dirty;
   }

   /**
    * @return the deleted
    */
   public boolean isDeleted() {
      return deleted;
   }

   /**
    * @param attrId the attrId to set
    */
   public void setAttrId(int attrId) {
      this.attrId = attrId;
   }

   /**
    * @param gammaId the gammaId to set
    */
   public void setGammaId(int gammaId) {
      this.gammaId = gammaId;
   }

   public void setIds(int attrId, int gammaId) {
      this.attrId = attrId;
      this.gammaId = gammaId;
   }

}
