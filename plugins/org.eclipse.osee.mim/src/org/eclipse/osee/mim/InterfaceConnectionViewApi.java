/*********************************************************************
 * Copyright (c) 2021 Boeing
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
package org.eclipse.osee.mim;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.mim.types.InterfaceConnection;
import org.eclipse.osee.mim.types.InterfaceStructureElementToken;
import org.eclipse.osee.orcs.core.ds.FollowRelation;

/**
 * @author Luciano T. Vaglienti
 */
public interface InterfaceConnectionViewApi extends QueryCapableMIMAPI<InterfaceConnection>, AffectedArtifactMIMAPI<InterfaceStructureElementToken> {

   ArtifactAccessor<InterfaceConnection> getAccessor();

   Collection<InterfaceConnection> getAll(BranchId branch);

   Collection<InterfaceConnection> getAll(BranchId branch, ArtifactId viewId);

   Collection<InterfaceConnection> getAll(BranchId branch, AttributeTypeId orderByAttribute);

   Collection<InterfaceConnection> getAll(BranchId branch, long pageNum, long pageSize);

   Collection<InterfaceConnection> getAll(BranchId branch, long pageNum, long pageSize,
      AttributeTypeId orderByAttribute);

   Collection<InterfaceConnection> getAll(BranchId branch, long pageNum, long pageSize,
      AttributeTypeId orderByAttribute, ArtifactId viewId);

   InterfaceConnection get(BranchId branch, ArtifactId connectionId);

   InterfaceConnection get(BranchId branch, ArtifactId viewId, ArtifactId connectionId,
      Collection<FollowRelation> followRelations);

   Collection<InterfaceConnection> get(BranchId branch, Collection<ArtifactId> connectionIds);

}
