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
package org.eclipse.osee.orcs.core.ds;

import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.RelationalConstants;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactDataImpl extends OrcsVersionedObjectImpl<IArtifactType> implements ArtifactData {

   private String guid = RelationalConstants.DEFAULT_GUID;
   private boolean useBackingData = false;

   public ArtifactDataImpl(VersionData version) {
      super(version);
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
   public BranchId getBranch() {
      return getVersion().getBranch();
   }
}