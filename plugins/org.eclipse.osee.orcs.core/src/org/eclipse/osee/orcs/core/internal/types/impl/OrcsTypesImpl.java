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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.concurrent.Callable;
import org.eclipse.osee.executor.admin.CancellableCallable;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsTypes;
import org.eclipse.osee.orcs.core.ds.OrcsTypesDataStore;
import org.eclipse.osee.orcs.core.internal.SessionContext;
import org.eclipse.osee.orcs.core.internal.types.OrcsTypesIndexProvider;
import org.eclipse.osee.orcs.core.internal.types.OrcsTypesLoaderFactory;
import org.eclipse.osee.orcs.core.internal.types.OrcsTypesResourceProvider;
import org.eclipse.osee.orcs.data.ArtifactTypes;
import org.eclipse.osee.orcs.data.AttributeTypes;
import org.eclipse.osee.orcs.data.RelationTypes;

/**
 * @author Roberto E. Escobar
 */
public class OrcsTypesImpl implements OrcsTypes {

   private final OrcsTypesIndexProvider indexProvider;

   private final Log logger;
   private final SessionContext session;
   private final OrcsTypesDataStore dataStore;
   private final OrcsTypesLoaderFactory loaderFactory;

   private final ArtifactTypes artifactTypes;
   private final AttributeTypes attributeTypes;
   private final RelationTypes relationTypes;

   public OrcsTypesImpl(Log logger, SessionContext session, OrcsTypesDataStore dataStore, OrcsTypesLoaderFactory loaderFactory, OrcsTypesIndexProvider indexProvider) {
      this.logger = logger;
      this.session = session;
      this.dataStore = dataStore;
      this.loaderFactory = loaderFactory;

      this.indexProvider = indexProvider;

      this.artifactTypes = new ArtifactTypesImpl(indexProvider);
      this.attributeTypes = new AttributeTypesImpl(indexProvider, indexProvider);
      this.relationTypes = new RelationTypesImpl(indexProvider);
   }

   @Override
   public ArtifactTypes getArtifactTypes() {
      return artifactTypes;
   }

   @Override
   public AttributeTypes getAttributeTypes() {
      return attributeTypes;
   }

   @Override
   public RelationTypes getRelationTypes() {
      return relationTypes;
   }

   @Override
   public void invalidateAll() {
      indexProvider.invalidate();
   }

   @Override
   public Callable<?> loadTypes(final IResource resource, final boolean isInitializing) {
      return new CancellableCallable<Void>() {
         @Override
         public Void call() throws Exception {
            indexProvider.setLoader(loaderFactory.createTypesLoader(session, new OrcsTypesResourceProvider() {

               @Override
               public IResource getOrcsTypesResource() {
                  return resource;
               }
            }));
            return null;
         }
      };
   }

   @Override
   public Callable<?> writeTypes(final OutputStream outputStream) {
      return new CancellableCallable<Void>() {
         @Override
         public Void call() throws Exception {
            logger.trace("Writing OrcsTypes for session [%s]", session);
            IResource resource = indexProvider.getOrcsTypesResource();
            InputStream inputStream = null;
            try {
               inputStream = resource.getContent();
               checkForCancelled();
               Lib.inputStreamToOutputStream(inputStream, outputStream);
            } catch (Exception ex) {
               OseeExceptions.wrapAndThrow(ex);
            } finally {
               Lib.close(inputStream);
               Lib.close(outputStream);
            }
            return null;
         }
      };
   }

   @Override
   public Callable<?> purgeArtifactsByArtifactType(Collection<? extends IArtifactType> artifactTypes) {
      return dataStore.purgeArtifactsByArtifactType(session.getSessionId(), artifactTypes);
   }

   @Override
   public Callable<?> purgeAttributesByAttributeType(Collection<? extends IAttributeType> attributeTypes) {
      return dataStore.purgeAttributesByAttributeType(session.getSessionId(), attributeTypes);
   }

   @Override
   public Callable<?> purgeRelationsByRelationType(Collection<? extends IRelationType> relationTypes) {
      return dataStore.purgeRelationsByRelationType(session.getSessionId(), relationTypes);
   }

}
