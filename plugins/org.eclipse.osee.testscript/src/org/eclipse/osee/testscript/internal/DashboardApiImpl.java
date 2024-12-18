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

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.accessor.ArtifactAccessor;
import org.eclipse.osee.accessor.internal.ArtifactAccessorImpl;
import org.eclipse.osee.accessor.types.ArtifactAccessorResult;
import org.eclipse.osee.accessor.types.ArtifactAccessorResultWithoutGammas;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.core.ds.FollowRelation;
import org.eclipse.osee.testscript.DashboardApi;
import org.eclipse.osee.testscript.ScriptDefApi;

/**
 * @author Stephen J. Molaro
 */
public class DashboardApiImpl implements DashboardApi {

   private final ScriptDefApi scriptDefApi;
   private final OrcsApi orcsApi;

   public DashboardApiImpl(ScriptDefApi scriptDefApi, OrcsApi orcsApi) {
      this.scriptDefApi = scriptDefApi;
      this.orcsApi = orcsApi;
   }

   @Override
   public Collection<CITimelineStatsToken> getTimelineStats(BranchId branch, ArtifactId ciSet, ArtifactId viewId) {
      viewId = viewId == null ? ArtifactId.SENTINEL : viewId;
      Table<String, Date, CIStatsToken> stats = TreeBasedTable.create();

      Collection<ScriptDefToken> defs = this.scriptDefApi.getAllByFilter(branch, ciSet.getIdString(),
         FollowRelation.followList(CoreRelationTypes.TestScriptDefToTestScriptResults_TestScriptResults), 0L, 0L, null,
         Arrays.asList(CoreAttributeTypes.SetId));

      SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

      //Pre-record all relevant dates so we can ensure all are populated with data from previously run scripts not run that day.
      for (ScriptDefToken def : defs) {
         for (ScriptResultToken res : def.getScriptResults()) {
            Date executionDate = res.getExecutionDate();
            try {
               executionDate = formatter.parse(formatter.format(executionDate));
            } catch (ParseException ex) {
               //Do Nothing
            }

            if (!stats.contains("All", executionDate)) {
               stats.put("All", executionDate, new CIStatsToken("All", executionDate));
            }

            if (!def.getTeam().isEmpty() && !stats.contains(def.getTeam(), executionDate)) {
               stats.put(def.getTeam(), executionDate, new CIStatsToken(def.getTeam(), executionDate));
            }
         }
      }

      for (ScriptDefToken def : defs) {

         List<ScriptResultToken> scriptResults = def.getScriptResults();
         Collections.sort(scriptResults, Comparator.comparing(ScriptResultToken::getExecutionDate).reversed());

         int prevPointsPassed = 0;
         int prevPointsFailed = 0;
         boolean prevAborted = false;
         boolean prevPassed = false;
         boolean prevScriptRun = false;

         Date prevExecutionDate = new Date();

         for (ScriptResultToken res : scriptResults) {
            Date executionDate = res.getExecutionDate();
            try {
               executionDate = formatter.parse(formatter.format(executionDate));
            } catch (ParseException ex) {
               //Do Nothing
            }

            if (executionDate.equals(prevExecutionDate)) {
               continue;
            }

            int pointsPassed = res.getPassedCount();
            int pointsFailed = res.getFailedCount();
            boolean aborted = res.getScriptAborted();
            boolean passed = aborted ? false : pointsFailed == 0;
            boolean scriptRun = true;

            //Updates the script values on the current Test Result's execution date
            CIStatsToken allStats = stats.get("All", executionDate);
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
            stats.put("All", executionDate, allStats);

            //Updates the script values for each team on the current Test Result's execution date
            if (!def.getTeam().isEmpty()) {
               CIStatsToken teamStats = stats.get(def.getTeam(), executionDate);
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
               stats.put(def.getTeam(), executionDate, teamStats);
            }

            //Maintains this Test Result's data for relevant days that this script was not run.
            for (Date specifiedDate : stats.row("All").keySet()) {
               if (specifiedDate.after(executionDate) && specifiedDate.before(prevExecutionDate)) {
                  allStats = stats.get("All", specifiedDate);
                  if (prevAborted) {
                     allStats.addScriptsAbort(1);
                  } else if (prevPassed) {
                     allStats.addScriptsPass(1);
                  } else {
                     allStats.addScriptsFail(1);
                  }
                  if (prevScriptRun) {
                     allStats.addScriptsRan(1);
                  } else {
                     allStats.addScriptsNotRan(1);
                  }
                  allStats.addTestPointsPass(prevPointsPassed);
                  allStats.addTestPointsFail(prevPointsFailed);
                  stats.put("All", specifiedDate, allStats);
               }
            }

            //For each team, maintains this Test Result's data for relevant days that this script was not run.
            for (Date specifiedDate : stats.row(def.getTeam()).keySet()) {
               if (specifiedDate.after(executionDate) && specifiedDate.before(prevExecutionDate)) {
                  if (!def.getTeam().isEmpty()) {
                     CIStatsToken teamStats = stats.get(def.getTeam(), executionDate);
                     teamStats = stats.get(def.getTeam(), specifiedDate);
                     if (prevAborted) {
                        teamStats.addScriptsAbort(1);
                     } else if (prevPassed) {
                        teamStats.addScriptsPass(1);
                     } else {
                        teamStats.addScriptsFail(1);
                     }
                     if (prevScriptRun) {
                        teamStats.addScriptsRan(1);
                     } else {
                        teamStats.addScriptsNotRan(1);
                     }
                     teamStats.addTestPointsPass(prevPointsPassed);
                     teamStats.addTestPointsFail(prevPointsFailed);
                     stats.put(def.getTeam(), specifiedDate, teamStats);
                  }
               } else if (specifiedDate.equals(prevExecutionDate)) {
                  break;
               }
            }

            prevPointsPassed = pointsPassed;
            prevPointsFailed = pointsFailed;
            prevAborted = aborted;
            prevPassed = passed;
            prevScriptRun = scriptRun;
            prevExecutionDate = executionDate;
         }
      }

      List<CITimelineStatsToken> values = new LinkedList<>();

      for (String teamName : stats.rowKeySet()) {
         CITimelineStatsToken timelineStats = new CITimelineStatsToken(teamName);
         timelineStats.setCiStats(stats.row(teamName).values());
         values.add(timelineStats);
      }

      return values;

   }

   @Override
   public Collection<ArtifactAccessorResultWithoutGammas> getSubsystems(BranchId branch, String filter, long pageNum,
      long pageSize, AttributeTypeToken orderByAttributeType) {
      ArtifactAccessor<ArtifactAccessorResultWithoutGammas> accessor =
         new ArtifactAccessorResultAccessor(CoreArtifactTypes.ScriptSubsystem, orcsApi);
      try {
         return accessor.getAllByFilter(branch, filter, Arrays.asList(CoreAttributeTypes.Name), pageNum, pageSize,
            orderByAttributeType);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
         return Collections.emptyList();
      }
   }

   @Override
   public Integer getSubsystemsCount(BranchId branch, String filter) {
      ArtifactAccessor<ArtifactAccessorResultWithoutGammas> accessor =
         new ArtifactAccessorImpl<>(CoreArtifactTypes.ScriptSubsystem, orcsApi);
      return accessor.getAllByFilterAndCount(branch, filter, Arrays.asList(CoreAttributeTypes.Name));
   }

   @Override
   public Collection<ArtifactAccessorResultWithoutGammas> getTeams(BranchId branch, String filter, long pageNum,
      long pageSize, AttributeTypeToken orderByAttributeType) {
      ArtifactAccessor<ArtifactAccessorResultWithoutGammas> accessor =
         new ArtifactAccessorResultAccessor(CoreArtifactTypes.ScriptTeam, orcsApi);
      try {
         return accessor.getAllByFilter(branch, filter, Arrays.asList(CoreAttributeTypes.Name), pageNum, pageSize,
            orderByAttributeType);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
         return Collections.emptyList();
      }
   }

   @Override
   public Integer getTeamsCount(BranchId branch, String filter) {
      ArtifactAccessor<ArtifactAccessorResult> accessor =
         new ArtifactAccessorImpl<>(CoreArtifactTypes.ScriptTeam, orcsApi);
      return accessor.getAllByFilterAndCount(branch, filter, Arrays.asList(CoreAttributeTypes.Name));
   }

   private class ArtifactAccessorResultAccessor extends ArtifactAccessorImpl<ArtifactAccessorResultWithoutGammas> {
      public ArtifactAccessorResultAccessor(ArtifactTypeToken type, OrcsApi orcsApi) {
         super(type, orcsApi);
      }
   }

}
