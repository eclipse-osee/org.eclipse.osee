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

package org.eclipse.osee.orcs.db.internal.loader.data;

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.DataProxy;
import org.eclipse.osee.orcs.core.ds.OrcsVersionedObjectImpl;
import org.eclipse.osee.orcs.core.ds.VersionData;

/**
 * @author Roberto E. Escobar
 */
public class AttributeDataImpl<T> extends OrcsVersionedObjectImpl<AttributeTypeToken> implements AttributeData<T> {

   private ArtifactId artifactId = ArtifactId.SENTINEL;
   private boolean useBackingData = false;

   private DataProxy<T> proxy;

   public AttributeDataImpl(VersionData version) {
      super(version);
   }

   @Override
   public ArtifactId getArtifactId() {
      return artifactId;
   }

   @Override
   public DataProxy<T> getDataProxy() {
      return proxy;
   }

   @Override
   public void setArtifactId(ArtifactId artifactId) {
      this.artifactId = artifactId;
   }

   @Override
   public void setDataProxy(DataProxy<T> proxy) {
      this.proxy = proxy;
   }

   @Override
   public boolean equals(Object obj) {
      if (!super.equals(obj)) {
         return false;
      }
      if (obj instanceof AttributeDataImpl) {
         AttributeDataImpl<?> other = (AttributeDataImpl<?>) obj;
         return other.artifactId.equals(artifactId) && proxy.equals(other.proxy);
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