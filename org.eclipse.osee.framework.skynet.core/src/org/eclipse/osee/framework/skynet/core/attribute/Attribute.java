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
package org.eclipse.osee.framework.skynet.core.attribute;

import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.AttributeMemo;
import org.eclipse.osee.framework.skynet.core.artifact.CacheArtifactModifiedEvent;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactModifiedEvent.ModType;
import org.eclipse.osee.framework.skynet.core.attribute.providers.IAttributeDataProvider;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;

/**
 * @author Ryan D. Brooks
 */
public abstract class Attribute<T> {
   private final AttributeType attributeType;
   private AttributeMemo memo;
   private Artifact artifact;
   private IAttributeDataProvider attributeDataProvider;

   protected Attribute(AttributeType attributeType, Artifact artifact) {
      this.attributeType = attributeType;
      this.artifact = artifact;
      this.memo = new AttributeMemo();
   }

   /**
    * @return the attribute name/value description
    */
   public String getNameValueDescription() {
      return attributeType.getName() + ": " + toString();
   }

   /**
    * @return attributeType Attribute Type Information
    */
   public AttributeType getAttributeType() {
      return attributeType;
   }

   public AttributeMemo getPersistenceMemo() {
      return memo;
   }

   /**
    * @return <b>true</b> if this attribute is dirty
    */
   public boolean isDirty() {
      return memo.isDirty();
   }

   public void setDirty() {
      memo.setDirty(true);
      artifact.setInTransaction(false);
      SkynetEventManager.getInstance().kick(new CacheArtifactModifiedEvent(artifact, ModType.Changed, this));
   }

   public Artifact getArtifact() {
      return artifact;
   }

   /**
    * Deletes the attribute
    */
   public void delete() {
      memo.setDeleted(true);
   }

   public boolean canDelete() {
      return artifact.getAttributeCount(attributeType.getName()) > attributeType.getMinOccurrences();
   }

   /**
    * Purges the attribute from the database.
    */
   public void purge() throws Exception {
      getAttributeDataProvider().purge();
      memo.setDeleted(true);
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      return getDisplayableString();
   }

   public abstract void setValue(T value);

   public abstract T getValue();

   public abstract String getDisplayableString();

   /**
    * @param attributeDataProvider the attributeDataProvider to set
    */
   public void setAttributeDataProvider(IAttributeDataProvider attributeDataProvider) {
      this.attributeDataProvider = attributeDataProvider;
   }

   protected IAttributeDataProvider getAttributeDataProvider() {
      return attributeDataProvider;
   }

   /**
    * @return
    */
   public boolean isInDatastore() {
      return memo.getGammaId() > 0;
   }

   public void initializeDefaultValue() {

   }
}