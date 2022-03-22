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

import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.mim.types.InterfaceStructureElementToken;
import org.eclipse.osee.mim.types.PlatformTypeToken;

/**
 * @author Luciano T. V@todo
 */
public interface InterfaceElementApi extends QueryCapableMIMAPI<InterfaceStructureElementToken> {

   List<InterfaceStructureElementToken> getAll(BranchId branch);

   List<InterfaceStructureElementToken> getAllRelated(BranchId branch, ArtifactId structureId);

   InterfaceStructureElementToken getWithAllParentRelations(BranchId branch, ArtifactId elementId);

   List<InterfaceStructureElementToken> getAllRelatedAndFilter(BranchId branch, ArtifactId structureId, String filter);

   List<InterfaceStructureElementToken> getFiltered(BranchId branch, String filter);

   List<InterfaceStructureElementToken> getElementsByType(BranchId branch, ArtifactId platformTypeId);

   InterfaceStructureElementToken get(BranchId branch, ArtifactId elementId);

   InterfaceStructureElementToken getRelated(BranchId branch, ArtifactId structureId, ArtifactId elementId);

   List<RelationTypeSide> getFollowRelationDetails();

   List<InterfaceStructureElementToken> getAllFromPlatformType(PlatformTypeToken pType);
}
