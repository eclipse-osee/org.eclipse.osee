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
import java.util.List;
import org.eclipse.osee.accessor.types.ArtifactAccessorResultWithoutGammas;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionResult;
import org.eclipse.osee.testscript.internal.ScriptResultToken;

/**
 * @author Stephen J. Molaro
 */
public interface DashboardApi {

   List<TimelineStatsToken> getTeamTimelineStats(BranchId branch, ArtifactId ciSet);

   TimelineStatsToken getTimelineStatsToken(BranchId branch, ArtifactId ciSet);

   TimelineStatsToken getUpdatedTimelineStats(BranchId branch, TimelineStatsToken timelineStats,
      Collection<ScriptResultToken> results);

   boolean updateAllActiveTimelineStats(BranchId branch);

   TransactionResult updateTimelineStats(BranchId branch, ArtifactId ciSet);

   Collection<ArtifactAccessorResultWithoutGammas> getSubsystems(BranchId branch, String filter, long pageNum,
      long pageSize, AttributeTypeToken orderByAttributeType);

   Integer getSubsystemsCount(BranchId branch, String filter);

   ScriptTeamToken getTeam(BranchId branch, ArtifactId id);

   Collection<ScriptTeamToken> getTeams(BranchId branch, String filter, long pageNum, long pageSize,
      AttributeTypeToken orderByAttributeType);

   Integer getTeamsCount(BranchId branch, String filter);

}
