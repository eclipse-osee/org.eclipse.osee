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

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.orcs.core.ds.FollowRelation;
import org.eclipse.osee.testscript.DashboardEndpoint;
import org.eclipse.osee.testscript.ScriptApi;

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
      Map<String, CIStatsToken> stats = new HashMap<>();
      CIStatsToken allStats = new CIStatsToken("All");
      Collection<ScriptDefToken> defs = this.testScriptApi.getScriptDefApi().getAll(branch, viewId,
         FollowRelation.followList(CoreRelationTypes.TestScriptDefToTestScriptResults_TestScriptResults));
      boolean statsSet = false;
      for (ScriptDefToken def : defs) {
         int pointsPassed = 0;
         int pointsFailed = 0;
         boolean aborted = false;
         boolean passed = false;
         boolean scriptRun = false;

         if (!def.getScriptResults().isEmpty()) {
            ScriptResultToken results = def.getScriptResults().get(0);
            pointsPassed = results.getPassedCount();
            pointsFailed = results.getFailedCount();
            aborted = results.getScriptAborted();
            passed = aborted ? false : pointsFailed == 0;
            scriptRun = true;
         }

         if (aborted) {
            allStats.addScriptsAbort(1);
         } else if (passed) {
            allStats.addScriptsPass(1);
         } else {
            allStats.addScriptsFail(1);
         }
         if (scriptRun) {
            allStats.addScriptsRan(1);
         } else {
            allStats.addScriptsNotRan(1);
         }
         allStats.addTestPointsPass(pointsPassed);
         allStats.addTestPointsFail(pointsFailed);
         statsSet = true;

         if (def.getTeam().isEmpty()) {
            continue;
         }
         CIStatsToken teamStats = stats.getOrDefault(def.getTeam(), new CIStatsToken(def.getTeam()));
         if (aborted) {
            teamStats.addScriptsAbort(1);
         } else if (passed) {
            teamStats.addScriptsPass(1);
         } else {
            teamStats.addScriptsFail(1);
         }
         if (scriptRun) {
            teamStats.addScriptsRan(1);
         } else {
            teamStats.addScriptsNotRan(1);
         }
         teamStats.addTestPointsPass(pointsPassed);
         teamStats.addTestPointsFail(pointsFailed);
         stats.put(def.getTeam(), teamStats);
      }
      if (statsSet) {
         stats.put("All", allStats);
      }

      List<CIStatsToken> values = new LinkedList<>(stats.values());

      Collections.sort(values, new Comparator<CIStatsToken>() {
         @Override
         public int compare(CIStatsToken o1, CIStatsToken o2) {
            // Make sure All ends up at the beginning
            String name1 = o1.getName().equals("All") ? "AAAAAAAAAA" : o1.getName();
            String name2 = o2.getName().equals("All") ? "AAAAAAAAAA" : o2.getName();
            return name1.compareTo(name2);
         }

      });

      return values;

   }

   @Override
   public Collection<CIStatsToken> getSubsystemStats(BranchId branch, ArtifactId ciSet, ArtifactId viewId) {
      viewId = viewId == null ? ArtifactId.SENTINEL : viewId;
      Map<String, CIStatsToken> stats = new HashMap<>();
      Collection<ScriptDefToken> defs = this.testScriptApi.getScriptDefApi().getAll(branch, viewId,
         FollowRelation.followList(CoreRelationTypes.TestScriptDefToTestScriptResults_TestScriptResults));

      for (ScriptDefToken def : defs) {
         int pointsPassed = 0;
         int pointsFailed = 0;
         boolean aborted = false;
         boolean passed = false;
         boolean scriptRun = false;

         if (!def.getScriptResults().isEmpty()) {
            ScriptResultToken results = def.getScriptResults().get(0);
            pointsPassed = results.getPassedCount();
            pointsFailed = results.getFailedCount();
            aborted = results.getScriptAborted();
            passed = aborted ? false : pointsFailed == 0;
            scriptRun = true;
         }

         if (def.getTeam().isEmpty()) {
            continue;
         }
         String subsystem = def.getSubsystem().isEmpty() ? "None" : def.getSubsystem();
         CIStatsToken subsystemStats = stats.getOrDefault(subsystem, new CIStatsToken(subsystem));
         if (aborted) {
            subsystemStats.addScriptsAbort(1);
         } else if (passed) {
            subsystemStats.addScriptsPass(1);
         } else {
            subsystemStats.addScriptsFail(1);
         }
         if (scriptRun) {
            subsystemStats.addScriptsRan(1);
         } else {
            subsystemStats.addScriptsNotRan(1);
         }
         subsystemStats.addTestPointsPass(pointsPassed);
         subsystemStats.addTestPointsFail(pointsFailed);
         stats.put(subsystem, subsystemStats);
      }

      List<CIStatsToken> values = new LinkedList<>(stats.values());

      Collections.sort(values, new Comparator<CIStatsToken>() {
         @Override
         public int compare(CIStatsToken o1, CIStatsToken o2) {
            // Make sure None ends up at the end
            String name1 = o1.getName().equals("None") ? "ZZZZZZZZZZ" : o1.getName();
            String name2 = o2.getName().equals("None") ? "ZZZZZZZZZZ" : o2.getName();
            return name1.compareTo(name2);
         }

      });

      return values;

   }

}