/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.types.impl;

import java.util.concurrent.FutureTask;
import org.eclipse.osee.framework.jdk.core.type.LazyObject;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.orcs.core.internal.types.OrcsTypesIndex;
import org.eclipse.osee.orcs.core.internal.types.OrcsTypesIndexProvider;
import org.eclipse.osee.orcs.core.internal.types.OrcsTypesLoader;

/**
 * @author Roberto E. Escobar
 */
public class OrcsTypesIndexProviderImpl extends LazyObject<OrcsTypesIndex>implements OrcsTypesIndexProvider {

   private OrcsTypesLoader loader;

   public OrcsTypesIndexProviderImpl(OrcsTypesLoader loader) {
      super();
      this.loader = loader;
   }

   @Override
   public ArtifactTypeIndex getArtifactTypeIndex()  {
      return get().getArtifactTypeIndex();
   }

   @Override
   public AttributeTypeIndex getAttributeTypeIndex()  {
      return get().getAttributeTypeIndex();
   }

   @Override
   public EnumTypeIndex getEnumTypeIndex()  {
      return get().getEnumTypeIndex();
   }

   @Override
   public RelationTypeIndex getRelationTypeIndex()  {
      return get().getRelationTypeIndex();
   }

   @Override
   public IResource getOrcsTypesResource()  {
      try {
         return get().getOrcsTypesResource();
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   @Override
   protected FutureTask<OrcsTypesIndex> createLoaderTask() {
      return new FutureTask<OrcsTypesIndex>(loader.createLoader());
   }

   @Override
   public void setLoader(OrcsTypesLoader loader) {
      synchronized (getLock()) {
         this.loader = loader;
         invalidate();
      }
   }
}