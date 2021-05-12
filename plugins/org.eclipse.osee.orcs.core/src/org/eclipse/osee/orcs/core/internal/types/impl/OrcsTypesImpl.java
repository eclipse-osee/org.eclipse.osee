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

import java.util.Collection;
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
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