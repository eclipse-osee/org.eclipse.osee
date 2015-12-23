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
import org.eclipse.osee.framework.core.exception.OseeExceptions;
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
   public ArtifactTypeIndex getArtifactTypeIndex() throws OseeCoreException {
      return get().getArtifactTypeIndex();
   }

   @Override
   public AttributeTypeIndex getAttributeTypeIndex() throws OseeCoreException {
      return get().getAttributeTypeIndex();
   }

   @Override
   public EnumTypeIndex getEnumTypeIndex() throws OseeCoreException {
      return get().getEnumTypeIndex();
   }

   @Override
   public RelationTypeIndex getRelationTypeIndex() throws OseeCoreException {
      return get().getRelationTypeIndex();
   }

   @Override
   public IResource getOrcsTypesResource() throws OseeCoreException {
      IResource resource = null;
      try {
         resource = get().getOrcsTypesResource();
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
      return resource;
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