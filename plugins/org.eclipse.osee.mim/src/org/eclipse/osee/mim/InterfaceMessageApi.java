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
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.mim.types.InterfaceMessageToken;
import org.eclipse.osee.mim.types.InterfaceStructureElementToken;
import org.eclipse.osee.mim.types.InterfaceSubMessageToken;

/**
 * @author Luciano T. Vaglienti Api for accessing interface messages
 */
public interface InterfaceMessageApi extends QueryCapableMIMAPI<InterfaceMessageToken>, AffectedArtifactMIMAPI<InterfaceStructureElementToken> {

   ArtifactAccessor<InterfaceMessageToken> getAccessor();

   InterfaceMessageToken get(BranchId branch, ArtifactId messageId);

   Collection<InterfaceMessageToken> getAll(BranchId branch);

   Collection<InterfaceMessageToken> getAllForConnection(BranchId branch, ArtifactId connectionId);

   Collection<InterfaceMessageToken> getAll(BranchId branch, AttributeTypeId orderByAttribute);

   Collection<InterfaceMessageToken> getAllForConnection(BranchId branch, ArtifactId connectionId, AttributeTypeId orderByAttribute);

   Collection<InterfaceMessageToken> getAll(BranchId branch, long pageNum, long pageSize);

   Collection<InterfaceMessageToken> getAllForConnection(BranchId branch, ArtifactId connectionId, long pageNum, long pageSize);

   Collection<InterfaceMessageToken> getAll(BranchId branch, long pageNum, long pageSize, AttributeTypeId orderByAttribute);

   Collection<InterfaceMessageToken> getAllForConnection(BranchId branch, ArtifactId connectionId, long pageNum, long pageSize, AttributeTypeId orderByAttribute);

   InterfaceMessageToken getRelatedToConnection(BranchId branch, ArtifactId connectionId, ArtifactId messageId);

   List<RelationTypeSide> getFollowRelationDetails();

   List<InterfaceMessageToken> getAllRelatedFromSubMessage(InterfaceSubMessageToken subMessage);

   InterfaceMessageToken getWithAllParentRelations(BranchId branch, ArtifactId messageId);

   InterfaceSubMessageToken getMessageHeader(InterfaceMessageToken message);

   Collection<InterfaceMessageToken> getAllForConnectionAndFilter(BranchId branch, ArtifactId connectionId, String filter);
}
