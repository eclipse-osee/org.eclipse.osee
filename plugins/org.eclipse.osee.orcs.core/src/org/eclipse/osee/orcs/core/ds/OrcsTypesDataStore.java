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

package org.eclipse.osee.orcs.core.ds;

import java.util.Collection;
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.orcs.OrcsSession;

/**
 * @author Roberto E. Escobar
 */
public interface OrcsTypesDataStore {

   Callable<Void> purgeArtifactsByArtifactType(OrcsSession session,
      Collection<? extends ArtifactTypeToken> artifactTypes);

   Callable<Void> purgeAttributesByAttributeType(OrcsSession session,
      Collection<? extends AttributeTypeId> attributeTypes);

   Callable<Void> purgeRelationsByRelationType(OrcsSession session,
      Collection<? extends RelationTypeToken> relationTypes);

}