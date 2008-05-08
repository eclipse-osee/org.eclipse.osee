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

import org.eclipse.osee.framework.skynet.core.artifact.CacheArtifactModifiedEvent;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactModifiedEvent.ModType;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;

/**
 * @author Roberto E. Escobar
 */
public class AttributeStateManager {
   private static final SkynetEventManager eventManager = SkynetEventManager.getInstance();

   private Attribute parentAttribute;
   private DynamicAttributeManager attributeManager;
   private boolean deletable;
   private boolean deleted;
   protected boolean dirty;

   public AttributeStateManager() {
      this.parentAttribute = null;
      this.attributeManager = null;
   }

   protected void setAttribute(Attribute parentAttribute) {
      this.parentAttribute = parentAttribute;
   }

   protected Attribute getAttribute() {
      return this.parentAttribute;
   }

   public void setAttributeManager(DynamicAttributeManager attributeManager) {
      this.attributeManager = attributeManager;
   }

   public DynamicAttributeManager getAttributeManager() {
      return attributeManager;
   }

   public void setDirty() {
      checkDeleted();
      this.dirty = true;
      if (isAttributeManagerValid()) {
         getAttributeManager().getParentArtifact().setInTransaction(false);
         eventManager.kick(new CacheArtifactModifiedEvent(getAttributeManager().getParentArtifact(), ModType.Changed,
               this));
      }
   }

   private boolean isAttributeManagerValid() {
      return getAttributeManager() != null && getAttributeManager().getParentArtifact() != null;
   }

   /**
    * @return Returns the dirty.
    */
   public boolean isDirty() {
      return dirty;
   }

   /**
    * Set this attribute as not dirty. Should only be called my the persistence manager once it has persisted this
    * attribute.
    */
   public void setNotDirty() {
      checkDeleted();
      this.dirty = false;
   }

   /**
    * @return <b>true</b> if the item is deletable
    */
   public boolean isDeletable() {
      checkDeleted();
      return deletable;
   }

   protected void setDeleted(boolean isDeleted) {
      this.deleted = isDeleted;
   }

   protected void checkDeleted() {
      if (deleted) throw new IllegalStateException("This artifact has been deleted");
   }
}
