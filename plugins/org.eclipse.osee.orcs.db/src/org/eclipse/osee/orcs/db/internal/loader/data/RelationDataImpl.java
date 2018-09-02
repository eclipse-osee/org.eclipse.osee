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

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.RelationalConstants;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.orcs.core.ds.OrcsVersionedObjectImpl;
import org.eclipse.osee.orcs.core.ds.RelationData;
import org.eclipse.osee.orcs.core.ds.VersionData;

/**
 * @author Andrew M. Finkbeiner
 */
public class RelationDataImpl extends OrcsVersionedObjectImpl implements RelationData {

   private ArtifactId artIdA = ArtifactId.SENTINEL;
   private ArtifactId artIdB = ArtifactId.SENTINEL;
   private String rationale = RelationalConstants.DEFAULT_RATIONALE;
   private boolean useBackingData = false;

   public RelationDataImpl(VersionData version) {
      super(version);
   }

   public void setRelationId(int relationId) {
      setLocalId(relationId);
   }

   @Override
   public void setArtIdA(ArtifactId artIdA) {
      this.artIdA = artIdA;
   }

   @Override
   public void setArtIdB(ArtifactId artIdB) {
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
      return artIdA.getId().intValue();
   }

   @Override
   public int getArtIdB() {
      return artIdB.getId().intValue();
   }

   @Override
   public String getRationale() {
      return rationale;
   }

   @Override
   public ArtifactId getArtIdOn(RelationSide side) {
      return RelationSide.SIDE_A == side ? getArtifactIdA() : getArtifactIdB();
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

   @Override
   public ArtifactId getArtifactIdA() {
      return artIdA;
   }

   @Override
   public ArtifactId getArtifactIdB() {
      return artIdB;
   }

   @Override
   public Long getId() {
      return getLocalId().longValue();
   }
}