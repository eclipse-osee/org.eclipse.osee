/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.testscript;

import java.util.Collection;
import org.eclipse.osee.accessor.types.ArtifactAccessorResult;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.testscript.internal.CITimelineStatsToken;

/**
 * @author Stephen J. Molaro
 */
public interface DashboardApi {

   Collection<CITimelineStatsToken> getTimelineStats(BranchId branch, ArtifactId ciSet, ArtifactId viewId);

   Collection<ArtifactAccessorResult> getSubsystems(BranchId branch, String filter, long pageNum, long pageSize,
      AttributeTypeToken orderByAttributeType);

   Integer getSubsystemsCount(BranchId branch, String filter);

   Collection<ArtifactAccessorResult> getTeams(BranchId branch, String filter, long pageNum, long pageSize,
      AttributeTypeToken orderByAttributeType);

   Integer getTeamsCount(BranchId branch, String filter);

}
