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
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.mim.types.InterfaceMessageToken;

/**
 * @author Luciano T. Vaglienti Api for accessing interface messages
 * @todo
 */
public interface InterfaceMessageApi extends QueryCapableMIMAPI<InterfaceMessageToken> {
   ArtifactAccessor<InterfaceMessageToken> getAccessor();

   Collection<InterfaceMessageToken> getAll(BranchId branch);

   Collection<InterfaceMessageToken> getAllForConnection(BranchId branch, ArtifactId connectionId);

   InterfaceMessageToken getRelatedToConnection(BranchId branch, ArtifactId connectionId, ArtifactId messageId);

   List<RelationTypeSide> getFollowRelationDetails();
}
