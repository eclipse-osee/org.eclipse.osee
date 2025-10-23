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

package org.eclipse.osee.testscript.internal;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.Response;
import org.eclipse.osee.accessor.types.ArtifactAccessorResultWithoutGammas;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.TransactionResult;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.core.ds.FollowRelation;
import org.eclipse.osee.testscript.DashboardEndpoint;
import org.eclipse.osee.testscript.ScriptApi;
import org.eclipse.osee.testscript.ScriptDefToken;
import org.eclipse.osee.testscript.ScriptResultToken;
import org.eclipse.osee.testscript.ScriptTeamToken;
import org.eclipse.osee.testscript.TimelineStatsToken;

/**
 * @author Ryan T. Baldwin
 */

public class DashboardEndpointImpl implements DashboardEndpoint {
   private final ScriptApi testScriptApi;

   public DashboardEndpointImpl(ScriptApi testScriptApi) {
      this.testScriptApi = testScriptApi;
   }

   @Override
   public Collection<CIStatsToken> getTeamStats(BranchId branch, ArtifactId ciSet, ArtifactId viewId) {
      viewId = viewId == null ? ArtifactId.SENTINEL : viewId;
      Map<ArtifactId, CIStatsToken> stats = new HashMap<>();
      CIStatsToken allStats = new CIStatsToken("All");

      Map<ArtifactId, ScriptResultToken> latestByDefForSet = buildLatestByDefForSet(branch, ciSet);

      LinkedList<RelationTypeSide> rels = new LinkedList<>();
      rels.add(CoreRelationTypes.TestScriptDefToTestScriptResults_TestScriptDef);
      rels.add(CoreRelationTypes.TestScriptSetToTestScriptResults_TestScriptSet);

      Collection<ScriptDefToken> defs = this.testScriptApi.getScriptDefApi().getAllByRelationThrough(branch, rels,
         ciSet, Strings.EMPTY_STRING, Arrays.asList(CoreAttributeTypes.Name),
         Arrays.asList(FollowRelation.fork(CoreRelationTypes.TestScriptDefToTeam_ScriptTeam)), 0L, 0L, null,
         new LinkedList<>(), viewId);

      boolean statsSet = false;
      for (ScriptDefToken def : defs) {
         ScriptResultToken latestForSet =
            latestByDefForSet.getOrDefault(def.getArtifactId(), ScriptResultToken.SENTINEL);
         if (latestForSet.isInvalid()) {
            continue;
         }

         int pointsPassed = latestForSet.getPassedCount();
         int pointsFailed = latestForSet.getFailedCount();
         boolean aborted = latestForSet.getScriptAborted();
         boolean ran = true;

         accumulate(allStats, pointsPassed, pointsFailed, aborted, ran);
         statsSet = true;

         if (def.getTeam().getArtifactId().isInvalid()) {
            continue;
         }
         CIStatsToken teamStats =
            stats.getOrDefault(def.getTeam().getArtifactId(), new CIStatsToken(def.getTeam().getName().getValue()));
         accumulate(teamStats, pointsPassed, pointsFailed, aborted, ran);
         stats.put(def.getTeam().getArtifactId(), teamStats);
      }
      if (statsSet) {
         stats.put(ArtifactId.SENTINEL, allStats);
      }

      List<CIStatsToken> values = new LinkedList<>(stats.values());

      Collections.sort(values, new Comparator<CIStatsToken>() {
         @Override
         public int compare(CIStatsToken o1, CIStatsToken o2) {
            // Make sure All ends up at the beginning
            if (o1.getName().equals("All")) {
               return -1;
            } else if (o2.getName().equals("All")) {
               return 1;
            }
            return o1.getName().compareTo(o2.getName());
         }

      });

      return values;

   }

   @Override
   public Collection<CIStatsToken> getSubsystemStats(BranchId branch, ArtifactId ciSet, ArtifactId viewId) {
      viewId = viewId == null ? ArtifactId.SENTINEL : viewId;
      Map<String, CIStatsToken> stats = new HashMap<>();
      Map<ArtifactId, ScriptResultToken> latestByDefForSet = buildLatestByDefForSet(branch, ciSet);

      LinkedList<RelationTypeSide> rels = new LinkedList<>();
      rels.add(CoreRelationTypes.TestScriptDefToTestScriptResults_TestScriptDef);
      rels.add(CoreRelationTypes.TestScriptSetToTestScriptResults_TestScriptSet);

      Collection<ScriptDefToken> defs = this.testScriptApi.getScriptDefApi().getAllByRelationThrough(branch, rels,
         ciSet, Strings.EMPTY_STRING, Arrays.asList(CoreAttributeTypes.Name), Collections.emptyList(), // remove excessive follow calls
         0L, 0L, null, new LinkedList<>(), viewId);

      for (ScriptDefToken def : defs) {
         ScriptResultToken latestForSet =
            latestByDefForSet.getOrDefault(def.getArtifactId(), ScriptResultToken.SENTINEL);
         if (latestForSet.isInvalid()) {
            continue;
         }

         int pointsPassed = latestForSet.getPassedCount();
         int pointsFailed = latestForSet.getFailedCount();
         boolean aborted = latestForSet.getScriptAborted();
         boolean ran = true;

         String subsystem = def.getSubsystem().isEmpty() ? "None" : def.getSubsystem();
         CIStatsToken subsystemStats = stats.getOrDefault(subsystem, new CIStatsToken(subsystem));

         accumulate(subsystemStats, pointsPassed, pointsFailed, aborted, ran);
         stats.put(subsystem, subsystemStats);
      }

      List<CIStatsToken> values = new LinkedList<>(stats.values());

      Collections.sort(values, new Comparator<CIStatsToken>() {
         @Override
         public int compare(CIStatsToken o1, CIStatsToken o2) {
            // Make sure None ends up at the end
            if (o1.getName().equals("None")) {
               return 1;
            } else if (o2.getName().equals("None")) {
               return -1;
            }
            return o1.getName().compareTo(o2.getName());
         }

      });

      return values;

   }

   @Override
   public TimelineStatsToken getTimeline(BranchId branch, ArtifactId ciSet) {
      return this.testScriptApi.getDashboardApi().getTimelineStatsToken(branch, ciSet);
   }

   @Override
   public List<TimelineStatsToken> getTeamTimelines(BranchId branch, ArtifactId ciSet) {
      return this.testScriptApi.getDashboardApi().getTeamTimelineStats(branch, ciSet);
   }

   @Override
   public TransactionResult updateTimelines(BranchId branch, ArtifactId ciSet) {
      return this.testScriptApi.getDashboardApi().updateTimelineStats(branch, ciSet);
   }

   @Override
   public boolean updateAllActiveTimelines(BranchId branch) {
      return this.testScriptApi.getDashboardApi().updateAllActiveTimelineStats(branch);
   }

   @Override
   public Collection<ArtifactAccessorResultWithoutGammas> getSubsystems(BranchId branch, String filter, long pageNum,
      long pageSize, AttributeTypeToken orderByAttributeType) {
      return this.testScriptApi.getDashboardApi().getSubsystems(branch, filter, pageNum, pageSize,
         orderByAttributeType);
   }

   @Override
   public Integer getSubsystemsCount(BranchId branch, String filter) {
      return this.testScriptApi.getDashboardApi().getSubsystemsCount(branch, filter);
   }

   @Override
   public Collection<ScriptTeamToken> getTeams(BranchId branch, String filter, long pageNum, long pageSize,
      AttributeTypeToken orderByAttributeType) {
      return this.testScriptApi.getDashboardApi().getTeams(branch, filter, pageNum, pageSize, orderByAttributeType);
   }

   @Override
   public Integer getTeamsCount(BranchId branch, String filter) {
      return this.testScriptApi.getDashboardApi().getTeamsCount(branch, filter);
   }

   @Override
   public Response exportDashboardBranchData(BranchId branch, ArtifactId viewId) {
      viewId = viewId == null ? ArtifactId.SENTINEL : viewId;
      return this.testScriptApi.getDashboardApi().exportDashboardBranchData(branch, viewId);
   }

   @Override
   public Response exportDashboardSetData(BranchId branch, ArtifactId ciSet, ArtifactId viewId) {
      viewId = viewId == null ? ArtifactId.SENTINEL : viewId;
      return this.testScriptApi.getDashboardApi().exportDashboardSetData(branch, ciSet, viewId);
   }

   private Map<ArtifactId, ScriptResultToken> buildLatestByDefForSet(BranchId branch, ArtifactId ciSet) {
      Collection<ScriptResultToken> setResults =
         this.testScriptApi.getScriptResultApi().getAllForSetWithScripts(branch, ArtifactId.SENTINEL, ciSet);

      Map<ArtifactId, ScriptResultToken> latestByDef = new HashMap<>(Math.max(16, setResults.size() / 2), 0.75f);
      for (ScriptResultToken res : setResults) {
         ArtifactReadable resReadable = res.getArtifactReadable();
         ArtifactReadable defReadable = resReadable.getRelated(
            CoreRelationTypes.TestScriptDefToTestScriptResults_TestScriptDef).getAtMostOneOrDefault(
               ArtifactReadable.SENTINEL);

         ArtifactId defId = defReadable.getArtifactId();
         if (defId.isInvalid()) {
            continue;
         }

         ScriptResultToken cur = latestByDef.get(defId);
         if (cur == null) {
            latestByDef.put(defId, res);
         } else {
            Date curDate = cur.getExecutionDate();
            Date newDate = res.getExecutionDate();
            long curMs = curDate != null ? curDate.getTime() : Long.MIN_VALUE;
            long newMs = newDate != null ? newDate.getTime() : Long.MIN_VALUE;
            if (newMs > curMs) {
               latestByDef.put(defId, res);
            }
         }
      }
      return latestByDef;
   }

   private void accumulate(CIStatsToken token, int pointsPassed, int pointsFailed, boolean aborted, boolean ran) {
      if (aborted) {
         token.addScriptsAbort(1);
      } else if (!aborted && pointsFailed == 0) {
         token.addScriptsPass(1);
      } else {
         token.addScriptsFail(1);
      }
      if (ran) {
         token.addScriptsRan(1);
      } else {
         token.addScriptsNotRan(1);
      }
      token.addTestPointsPass(pointsPassed);
      token.addTestPointsFail(pointsFailed);
   }

}