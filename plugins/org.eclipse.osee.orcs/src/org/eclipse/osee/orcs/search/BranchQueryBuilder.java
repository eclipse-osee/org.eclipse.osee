/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.orcs.search;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchCategoryToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.jdk.core.type.Pair;

/**
 * @author Ryan D. Brooks
 * @author Roberto E. Escobar
 */
public interface BranchQueryBuilder<T> {

   T includeDeleted();

   T excludeDeleted();

   T includeDeleted(boolean enabled);

   boolean areDeletedIncluded();

   T includeArchived();

   T includeArchived(boolean enabled);

   T excludeArchived();

   boolean areArchivedIncluded();

   T andIds(Collection<? extends BranchId> ids);

   T andId(BranchId branchId);

   T andIsOfType(BranchType... branchType);

   T andStateIs(BranchState... branchState);

   T andNameEquals(String value);

   T andNamePattern(String pattern);

   T andNamePatternIgnoreCase(String pattern);

   T andIsChildOf(BranchId branch);

   T andIsAncestorOf(BranchId branch);

   T andIsMergeFor(BranchId source, BranchId destination);

   T andAssociatedArtId(ArtifactId artId);

   T andIsOfCategory(BranchCategoryToken category);

   T mapAssocArtIdToRelatedAttributes(String value, BranchId relatedBranch,
      List<Pair<ArtifactTypeToken, AttributeTypeToken>> artAttrPairs);

   T orderByName();

   T andNameLike(String pattern);

   T isOnPage(long page, long pageSize);

   T orderById();

}
