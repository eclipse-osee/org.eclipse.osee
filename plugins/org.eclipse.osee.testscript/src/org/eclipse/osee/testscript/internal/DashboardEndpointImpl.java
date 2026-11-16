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
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.testscript.DashboardEndpoint;
import org.eclipse.osee.testscript.ScriptApi;
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
      if (ciSet == null || ciSet.isInvalid()) {
         return Collections.emptyList();
      }
      viewId = viewId == null ? ArtifactId.SENTINEL : viewId;
      Map<ArtifactId, CIStatsToken> stats = new HashMap<>();
      CIStatsToken allStats = new CIStatsToken("All");
      CIStatsToken unknownTeam = new CIStatsToken("Unassigned");
      boolean unknownHasData = false;

      Map<ArtifactId, ScriptResultToken> latestByDefForSet = buildLatestByDefForSet(branch, ciSet);

      List<ArtifactReadable> defs = getDefsInSet(branch, ciSet, viewId, true);

      boolean statsSet = false;
      for (ArtifactReadable def : defs) {
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

         ArtifactReadable teamArt =
            def.getRelated(CoreRelationTypes.TestScriptDefToTeam_ScriptTeam).getOneOrDefault(ArtifactReadable.SENTINEL);
         ScriptTeamToken team = teamArt.isValid() ? new ScriptTeamToken(teamArt) : ScriptTeamToken.SENTINEL;
         ArtifactId teamId = team.getArtifactId();
         String teamName = team.getName().getValue();

         if (teamId.isValid() && !teamName.equalsIgnoreCase("unassigned")) {
            CIStatsToken teamStats = stats.getOrDefault(teamId, new CIStatsToken(teamName));
            accumulate(teamStats, pointsPassed, pointsFailed, aborted, ran);
            stats.put(teamId, teamStats);
         } else {
            accumulate(unknownTeam, pointsPassed, pointsFailed, aborted, ran);
            unknownHasData = true;
         }
      }
      if (statsSet) {
         stats.put(ArtifactId.SENTINEL, allStats);
      }

      List<CIStatsToken> values = new LinkedList<>(stats.values());

      if (unknownHasData) {
         values.add(unknownTeam);
      }

      Collections.sort(values, new Comparator<CIStatsToken>() {
         @Override
         public int compare(CIStatsToken o1, CIStatsToken o2) {
            String n1 = o1.getName();
            String n2 = o2.getName();
            if ("All".equals(n1) && !"All".equals(n2)) {
               return -1;
            } else if (!"All".equals(n1) && "All".equals(n2)) {
               return 1;
            }
            boolean u1 = "Unassigned".equalsIgnoreCase(n1);
            boolean u2 = "Unassigned".equalsIgnoreCase(n2);
            if (u1 && !u2) {
               return 1; // o1 goes after
            } else if (!u1 && u2) {
               return -1; // o2 goes after
            }
            return n1.compareTo(n2);
         }
      });

      return values;
   }

   @Override
   public Collection<CIStatsToken> getSubsystemStats(BranchId branch, ArtifactId ciSet, ArtifactId viewId) {
      if (ciSet == null || ciSet.isInvalid()) {
         return Collections.emptyList();
      }
      viewId = viewId == null ? ArtifactId.SENTINEL : viewId;
      Map<String, CIStatsToken> stats = new HashMap<>();
      Map<ArtifactId, ScriptResultToken> latestByDefForSet = buildLatestByDefForSet(branch, ciSet);

      List<ArtifactReadable> defs = getDefsInSet(branch, ciSet, viewId, false);

      for (ArtifactReadable def : defs) {
         ScriptResultToken latestForSet =
            latestByDefForSet.getOrDefault(def.getArtifactId(), ScriptResultToken.SENTINEL);
         if (latestForSet.isInvalid()) {
            continue;
         }

         int pointsPassed = latestForSet.getPassedCount();
         int pointsFailed = latestForSet.getFailedCount();
         boolean aborted = latestForSet.getScriptAborted();
         boolean ran = true;

         String defSubsystem = def.getSoleAttributeAsString(CoreAttributeTypes.ScriptSubsystem, "");
         String subsystem = defSubsystem.isEmpty() ? "None" : defSubsystem;
         CIStatsToken subsystemStats = stats.getOrDefault(subsystem, new CIStatsToken(subsystem));

         accumulate(subsystemStats, pointsPassed, pointsFailed, aborted, ran);
         stats.put(subsystem, subsystemStats);
      }

      List<CIStatsToken> values = new LinkedList<>(stats.values());

      Collections.sort(values, new Comparator<CIStatsToken>() {
         @Override
         public int compare(CIStatsToken o1, CIStatsToken o2) {
            //Make sure None ends up at the end
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
   public List<TimelineStatsToken> getTimelineCompare(BranchId branch) {
      return this.testScriptApi.getDashboardApi().getTimelineCompare(branch);
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

   /**
    * Same query as ScriptResultApi.getAllForSet. The def is not followed because only its id is needed, and the run's
    * own relation rows already carry it; following would load every def's full result history.
    */
   private Map<ArtifactId, ScriptResultToken> buildLatestByDefForSet(BranchId branch, ArtifactId ciSet) {
      List<ArtifactReadable> setResults = this.testScriptApi.getOrcsApi().getQueryFactory().fromBranch(branch,
         ArtifactId.SENTINEL).includeApplicabilityTokens().andIsOfType(CoreArtifactTypes.TestScriptResults).and(
            Arrays.asList(CoreAttributeTypes.SetId), ciSet.getIdString(), QueryOption.TOKEN_DELIMITER__ANY,
            QueryOption.CASE__IGNORE, QueryOption.TOKEN_MATCH_ORDER__ANY).asArtifacts();

      Map<ArtifactId, ScriptResultToken> latestByDef = new HashMap<>(Math.max(16, setResults.size() / 2), 0.75f);
      for (ArtifactReadable resReadable : setResults) {
         if (resReadable.isInvalid()) {
            continue;
         }
         ScriptResultToken res = new ScriptResultToken(resReadable.getId(), resReadable.getName());
         res.setExecutionDate(resReadable.getSoleAttributeValue(CoreAttributeTypes.ExecutionDate, new Date()));
         res.setPassedCount(resReadable.getSoleAttributeValue(CoreAttributeTypes.PassedCount, 0));
         res.setFailedCount(resReadable.getSoleAttributeValue(CoreAttributeTypes.FailedCount, 0));
         res.setScriptAborted(resReadable.getSoleAttributeValue(CoreAttributeTypes.ScriptAborted, false));
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

   /**
    * Same criteria as ScriptDefApi.getAllByRelationThroughPartialFilter. Returns artifacts because only team and
    * subsystem are read, so building a full ScriptDefToken per def is wasted work.
    */
   private List<ArtifactReadable> getDefsInSet(BranchId branch, ArtifactId ciSet, ArtifactId viewId,
      boolean withTeam) {
      LinkedList<RelationTypeSide> rels = new LinkedList<>();
      rels.add(CoreRelationTypes.TestScriptDefToTestScriptResults_TestScriptDef);
      rels.add(CoreRelationTypes.TestScriptSetToTestScriptResults_TestScriptResults);

      QueryBuilder query = this.testScriptApi.getOrcsApi().getQueryFactory().fromBranch(branch,
         viewId).includeApplicabilityTokens().andIsOfType(CoreArtifactTypes.TestScriptDef).andRelatedToThroughRels(
            rels, ciSet);
      if (withTeam) {
         query = query.followFork(CoreRelationTypes.TestScriptDefToTeam_ScriptTeam);
      }
      return query.asArtifacts();
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