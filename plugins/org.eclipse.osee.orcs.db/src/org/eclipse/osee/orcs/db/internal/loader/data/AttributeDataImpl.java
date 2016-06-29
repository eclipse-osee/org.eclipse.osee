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

import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.DataProxy;
import org.eclipse.osee.orcs.core.ds.VersionData;
import org.eclipse.osee.orcs.db.internal.sql.RelationalConstants;

/**
 * @author Roberto E. Escobar
 */
public class AttributeDataImpl extends OrcsVersionedObjectImpl implements AttributeData {

   private int artifactId = RelationalConstants.ART_ID_SENTINEL;
   private boolean useBackingData = false;

   private DataProxy proxy;

   public AttributeDataImpl(VersionData version) {
      super(version);
   }

   @Override
   public int getArtifactId() {
      return artifactId;
   }

   @Override
   public DataProxy getDataProxy() {
      return proxy;
   }

   @Override
   public void setArtifactId(int artifactId) {
      this.artifactId = artifactId;
   }

   @Override
   public void setDataProxy(DataProxy proxy) {
      this.proxy = proxy;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + artifactId;
      result = prime * result + (proxy == null ? 0 : proxy.hashCode());
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
      AttributeDataImpl other = (AttributeDataImpl) obj;
      if (artifactId != other.artifactId) {
         return false;
      }
      if (proxy == null) {
         if (other.proxy != null) {
            return false;
         }
      } else if (!proxy.equals(other.proxy)) {
         return false;
      }
      return true;
   }

   @Override
   public String toString() {
      return "AttributeData [artifactId=" + artifactId + " " + super.toString() + ", proxy=" + proxy + "]";
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
}