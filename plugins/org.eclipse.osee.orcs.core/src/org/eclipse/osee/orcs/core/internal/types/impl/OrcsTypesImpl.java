/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.orcs.core.internal.types.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.executor.CancellableCallable;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.OrcsTypes;
import org.eclipse.osee.orcs.core.ds.OrcsTypesDataStore;

/**
 * @author Roberto E. Escobar
 */
public class OrcsTypesImpl implements OrcsTypes {

   private final OrcsSession session;
   private final OrcsTypesDataStore dataStore;

   public OrcsTypesImpl(OrcsSession session, OrcsTypesDataStore dataStore) {
      this.session = session;
      this.dataStore = dataStore;
   }

   @Override
   public Callable<Void> writeTypes(final OutputStream outputStream) {
      return new CancellableCallable<Void>() {
         @Override
         public Void call() throws Exception {
            try (InputStream inputStream = dataStore.getAccessInputStream()) {
               checkForCancelled();
               Lib.inputStreamToOutputStream(inputStream, outputStream);
            } catch (Exception ex) {
               OseeCoreException.wrapAndThrow(ex);
            } finally {
               Lib.close(outputStream);
            }
            return null;
         }
      };
   }

   @Override
   public Callable<Void> purgeArtifactsByArtifactType(Collection<? extends ArtifactTypeToken> artifactTypes) {
      return dataStore.purgeArtifactsByArtifactType(session, artifactTypes);
   }

   @Override
   public Callable<Void> purgeAttributesByAttributeType(Collection<? extends AttributeTypeId> attributeTypes) {
      return dataStore.purgeAttributesByAttributeType(session, attributeTypes);
   }

   @Override
   public Callable<Void> purgeRelationsByRelationType(Collection<? extends RelationTypeToken> relationTypes) {
      return dataStore.purgeRelationsByRelationType(session, relationTypes);
   }
}