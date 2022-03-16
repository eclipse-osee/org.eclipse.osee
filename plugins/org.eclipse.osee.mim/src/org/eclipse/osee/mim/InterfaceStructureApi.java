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
import org.eclipse.osee.mim.types.InterfaceStructureToken;

/**
 * @author Luciano T. Vaglienti Api for accessing interface structures
 * @todo
 */
public interface InterfaceStructureApi {

   List<InterfaceStructureToken> getAll(BranchId branch);

   List<InterfaceStructureToken> getAllWithoutRelations(BranchId branch);

   List<InterfaceStructureToken> getFiltered(BranchId branch, String filter);

   List<InterfaceStructureToken> getFilteredWithoutRelations(BranchId branch, String filter);

   List<InterfaceStructureToken> getAllRelated(BranchId branch, ArtifactId subMessageId);

   List<InterfaceStructureToken> getAllRelatedAndFilter(BranchId branch, ArtifactId subMessageId, String filter);

   InterfaceStructureToken getRelated(BranchId branch, ArtifactId subMessageId, ArtifactId structureId);
}
