/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.loader.data;

import org.eclipse.osee.framework.core.data.RelationalConstants;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.orcs.core.ds.RelationData;
import org.eclipse.osee.orcs.core.ds.VersionData;

/**
 * @author Andrew M. Finkbeiner
 */
public class RelationDataImpl extends OrcsVersionedObjectImpl implements RelationData {

   private int artIdA = RelationalConstants.ART_ID_SENTINEL;
   private int artIdB = RelationalConstants.ART_ID_SENTINEL;
   private String rationale = RelationalConstants.DEFAULT_RATIONALE;
   private boolean useBackingData = false;

   public RelationDataImpl(VersionData version) {
      super(version);
   }

   public void setRelationId(int relationId) {
      setLocalId(relationId);
   }

   @Override
   public void setArtIdA(int artIdA) {
      this.artIdA = artIdA;
   }

   @Override
   public void setArtIdB(int artIdB) {
      this.artIdB = artIdB;
   }

   @Override
   public void setRationale(String rationale) {
      this.rationale = rationale;
   }

   public int getRelationId() {
      return getLocalId();
   }

   @Override
   public int getArtIdA() {
      return artIdA;
   }

   @Override
   public int getArtIdB() {
      return artIdB;
   }

   @Override
   public String getRationale() {
      return rationale;
   }

   @Override
   public int getArtIdOn(RelationSide side) {
      return RelationSide.SIDE_A == side ? getArtIdA() : getArtIdB();
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + artIdA;
      result = prime * result + artIdB;
      result = prime * result + (rationale == null ? 0 : rationale.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (!super.equals(obj)) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      RelationDataImpl other = (RelationDataImpl) obj;
      if (artIdA != other.artIdA) {
         return false;
      }
      if (artIdB != other.artIdB) {
         return false;
      }
      if (rationale == null) {
         if (other.rationale != null) {
            return false;
         }
      } else if (!rationale.equals(other.rationale)) {
         return false;
      }
      return true;
   }

   @Override
   public String toString() {
      return "RelationData [artIdA=" + artIdA + ", artIdB=" + artIdB + ", rationale=" + rationale + " " + super.toString() + "]";
   }

   @Override
   public boolean isExistingVersionUsed() {
      return useBackingData;
   }

   @Override
   public void setUseBackingData(boolean useBackingData) {
      this.useBackingData = useBackingData;
   }

}
