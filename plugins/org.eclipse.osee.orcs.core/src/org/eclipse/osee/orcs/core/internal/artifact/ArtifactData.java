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
package org.eclipse.osee.orcs.core.internal.artifact;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.orcs.core.ds.AttributeContainer;
import org.eclipse.osee.orcs.core.internal.attribute.Attribute;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactData implements AttributeContainer<Artifact> {

   @SuppressWarnings("unused")
   private final HashCollection<IAttributeType, Attribute<?>> attributes =
      new HashCollection<IAttributeType, Attribute<?>>(false, LinkedList.class, 12);

   @SuppressWarnings("unused")
   private boolean isLoaded;

   @Override
   public Artifact getContainer() {
      return null;
   }

   @Override
   public boolean isLoaded() {
      return false;
   }

   @Override
   public void setLoaded(boolean value) {
      this.isLoaded = value;
      if (value == true) {
         onLoaded();
      }
   }

   @Override
   public int getCount(IAttributeType type) {
      return 0;
   }

   @Override
   public void add(IAttributeType type, Attribute<?> attribute) {
      //
   }

   @SuppressWarnings("unused")
   @Override
   public Collection<IAttributeType> getAttributeTypes() throws OseeCoreException {
      return null;
   }

   @SuppressWarnings("unused")
   @Override
   public <T> List<Attribute<T>> getAttributes(IAttributeType attributeType) throws OseeCoreException {
      return null;
   }

   private void onLoaded() {
      //      computeLastDateModified();
      //    artifact.meetMinimumAttributeCounts(false);
   }

}
