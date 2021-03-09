/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.orcs.core.ds;

import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.RelationalConstants;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactDataImpl extends OrcsVersionedObjectImpl<ArtifactTypeToken> implements ArtifactData {

   private String guid = RelationalConstants.DEFAULT_GUID;
   private boolean useBackingData = false;

   public ArtifactDataImpl(VersionData version) {
      super(version);
   }

   @Override
   public ArtifactTypeToken getArtifactType() {
      return getType();
   }

   @Override
   public String getGuid() {
      return guid;
   }

   @Override
   public void setGuid(String guid) {
      this.guid = guid;
   }

   @Override
   public boolean equals(Object obj) {
      if (!super.equals(obj)) {
         return false;
      }
      if (obj instanceof ArtifactDataImpl) {
         return guid.equals(((ArtifactDataImpl) obj).guid);
      }
      return true;
   }

   @Override
   public String toString() {
      return "ArtifactData [guid=" + guid + ", " + super.toString() + "]";
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
   public Long getId() {
      return getLocalId().longValue();
   }

   @Override
   public BranchToken getBranch() {
      return BranchToken.create(getVersion().getBranch(), "unknown");
   }
}