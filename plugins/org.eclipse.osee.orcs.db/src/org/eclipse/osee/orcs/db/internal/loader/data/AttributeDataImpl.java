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
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.DataProxy;
import org.eclipse.osee.orcs.core.ds.VersionData;

/**
 * @author Roberto E. Escobar
 */
public class AttributeDataImpl<T> extends OrcsVersionedObjectImpl implements AttributeData<T> {

   private int artifactId = RelationalConstants.ART_ID_SENTINEL;
   private boolean useBackingData = false;

   private DataProxy<T> proxy;

   public AttributeDataImpl(VersionData version) {
      super(version);
   }

   @Override
   public int getArtifactId() {
      return artifactId;
   }

   @Override
   public DataProxy<T> getDataProxy() {
      return proxy;
   }

   @Override
   public void setArtifactId(int artifactId) {
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
         return Integer.valueOf(other.artifactId).equals(artifactId) && proxy.equals(other.proxy);
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