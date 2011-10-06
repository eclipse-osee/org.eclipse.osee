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
package org.eclipse.osee.orcs.core.internal.attribute;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.NamedIdentity;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.core.ds.AttributeContainer;
import org.eclipse.osee.orcs.data.ReadableAttribute;

/**
 * @author Roberto E. Escobar
 */
public class AttributeContainerImpl implements AttributeContainer {

   private final AttributeCollection collection = new AttributeCollection();

   private final NamedIdentity<String> parent;
   private boolean isLoaded;

   public AttributeContainerImpl(NamedIdentity<String> parent) {
      this.parent = parent;
   }

   @Override
   public boolean isLoaded() {
      return isLoaded;
   }

   @Override
   public void setLoaded(boolean value) {
      this.isLoaded = value;
      if (value == true) {
         onLoaded();
      }
   }

   private void onLoaded() {
      //      computeLastDateModified();
      //    artifact.meetMinimumAttributeCounts(false);
   }

   @Override
   public int getCount(IAttributeType type) throws OseeCoreException {
      return getAttributes(type).size();
   }

   @Override
   public void add(IAttributeType type, Attribute<?> attribute) {
      collection.add(type, attribute);
   }

   @Override
   public Collection<IAttributeType> getAttributeTypes() {
      return collection.keySet();
   }

   @Override
   public <T> List<ReadableAttribute<T>> getAttributes(IAttributeType type) throws OseeCoreException {
      ensureAttributesLoaded();
      return collection.getCurrentAttributesFor(type);
   }

   private void ensureAttributesLoaded() throws OseeCoreException {
      //      if (!isLoaded() && isInDb()) {
      //         ArtifactLoader.loadArtifactData(this, LoadLevel.ATTRIBUTE);
      //      }
   }

   @Override
   public <T> ReadableAttribute<T> getSoleAttribute(IAttributeType attributeType) throws OseeCoreException {
      return null;
   }

   @Override
   public NamedIdentity<String> getParent() {
      return parent;
   }
}
