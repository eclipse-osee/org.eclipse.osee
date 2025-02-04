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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.eclipse.osee.accessor.ArtifactAccessor;
import org.eclipse.osee.accessor.internal.ArtifactAccessorImpl;
import org.eclipse.osee.accessor.types.ArtifactAccessorResultWithoutGammas;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionResult;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.rest.model.transaction.TransactionBuilderData;
import org.eclipse.osee.orcs.rest.model.transaction.TransactionBuilderDataFactory;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.eclipse.osee.testscript.DashboardApi;
import org.eclipse.osee.testscript.ScriptResultApi;
import org.eclipse.osee.testscript.ScriptSetApi;
import org.eclipse.osee.testscript.ScriptSetToken;
import org.eclipse.osee.testscript.ScriptTeamToken;
import org.eclipse.osee.testscript.TimelineDayToken;
import org.eclipse.osee.testscript.TimelineScriptToken;
import org.eclipse.osee.testscript.TimelineStatsToken;

/**
 * @author Stephen J. Molaro
 */
public class DashboardApiImpl implements DashboardApi {

   private final ScriptResultApi resultApi;
   private final ScriptSetApi setApi;
   private final OrcsApi orcsApi;

   public DashboardApiImpl(ScriptResultApi resultApi, ScriptSetApi setApi, OrcsApi orcsApi) {
      this.resultApi = resultApi;
      this.setApi = setApi;
      this.orcsApi = orcsApi;
   }

   @Override
   public TimelineStatsToken getTimelineStatsToken(BranchId branch, ArtifactId ciSet) {
      ArtifactReadable timelineArt = this.orcsApi.getQueryFactory().fromBranch(branch).andTypeEquals(
         CoreArtifactTypes.ScriptTimeline).andAttributeIs(CoreAttributeTypes.SetId,
            ciSet.getIdString()).asArtifactOrSentinel();
      return new TimelineStatsToken(timelineArt);
   }

   @Override
   public List<TimelineStatsToken> getTeamTimelineStats(BranchId branch, ArtifactId ciSet) {
      List<TimelineStatsToken> timelines = new LinkedList<>();
      TimelineStatsToken fullTimeline = getTimelineStatsToken(branch, ciSet);
      fullTimeline.setTeam("All");

      Collection<ScriptTeamToken> teams = this.getTeams(branch, "", 0L, 0L, AttributeTypeToken.SENTINEL);
      for (ScriptTeamToken team : teams) {
         TimelineStatsToken teamTimeline = new TimelineStatsToken(ArtifactReadable.SENTINEL);
         teamTimeline.setUpdatedAt(fullTimeline.getUpdatedAt());
         teamTimeline.setSetId(fullTimeline.getSetId());
         teamTimeline.setTeam(team.getName().getValue());
         teamTimeline = calculateTeamTimelineStats(fullTimeline, teamTimeline, team.getName().getValue());
         if (!teamTimeline.getDays().isEmpty()) {
            timelines.add(teamTimeline);
         }
      }

      Collections.sort(timelines, new Comparator<TimelineStatsToken>() {
         @Override
         public int compare(TimelineStatsToken o1, TimelineStatsToken o2) {
            return o1.getTeam().compareTo(o2.getTeam());
         }
      });

      timelines.add(0, fullTimeline);
      return timelines;
   }

   @Override
   public boolean updateAllActiveTimelineStats(BranchId branch) {
      Collection<ScriptSetToken> ciSets =
         this.setApi.getAll(branch, ArtifactId.SENTINEL, 0L, 0L, AttributeTypeId.SENTINEL, true);
      for (ScriptSetToken ciSet : ciSets) {
         if (ciSet.getActive().getValue()) {
            updateTimelineStats(branch, ciSet.getArtifactId());
         }
      }
      return true;
   }

   @Override
   public TransactionResult updateTimelineStats(BranchId branch, ArtifactId ciSet) {
      TimelineStatsToken currentTimeline = this.getTimelineStatsToken(branch, ciSet);

      if (currentTimeline.getArtifactReadable().isInvalid()) {
         currentTimeline.setSetId(ciSet);
      }

      Collection<ScriptResultToken> results = resultApi.getAllForSetWithScripts(branch, ArtifactId.SENTINEL, ciSet);
      TimelineStatsToken updatedTimeline = this.getUpdatedTimelineStats(branch, currentTimeline, results);

      TransactionBuilderDataFactory txBdf = new TransactionBuilderDataFactory(orcsApi);

      TransactionBuilderData txData = new TransactionBuilderData();
      txData.setBranch(branch.getIdString());
      txData.setTxComment("Update Zenith Timelines for CI Set " + ciSet);
      txData.setCreateArtifacts(new LinkedList<>());
      txData.setModifyArtifacts(new LinkedList<>());
      txData.setAddRelations(new LinkedList<>());

      if (updatedTimeline.getArtifactReadable().isValid()) {
         txData.getModifyArtifacts().add(updatedTimeline.modifyArtifact());
      } else {
         txData.getCreateArtifacts().add(updatedTimeline.createArtifact("timeline", ApplicabilityId.SENTINEL));
      }

      TransactionResult txResult = new TransactionResult();
      try {
         ObjectMapper mapper = new ObjectMapper();
         TransactionBuilder tx = txBdf.loadFromJson(mapper.writeValueAsString(txData));
         TransactionToken token = tx.commit();
         txResult.setTx(token);
         txResult.getResults().setIds(
            tx.getTxDataReadables().stream().map(readable -> readable.getIdString()).collect(Collectors.toList()));
      } catch (JsonProcessingException ex) {
         txResult.getResults().error("Error processing tx json");
      }

      return txResult;
   }

   @Override
   public TimelineStatsToken getUpdatedTimelineStats(BranchId branch, TimelineStatsToken timeline,
      Collection<ScriptResultToken> results) {
      Date newTime = new Date();
      Map<ArtifactId, String> teamNames = new HashMap<>();

      SortedMap<String, TimelineDayToken> timelineDays = new TreeMap<>();
      for (TimelineDayToken day : timeline.getDays()) {
         String key = TimelineStatsToken.dateFormat.format(day.getExecutionDate());
         timelineDays.put(key, day);
      }

      for (ScriptResultToken result : results) {
         String dayKey = TimelineStatsToken.dateFormat.format(result.getExecutionDate());
         TimelineDayToken timelineDay;
         try {
            timelineDay =
               timelineDays.getOrDefault(dayKey, new TimelineDayToken(TimelineStatsToken.dateFormat.parse(dayKey)));
         } catch (ParseException ex) {
            throw new OseeCoreException("Unable to parse date from format", ex);
         }

         TimelineScriptToken dayScript = timelineDay.getScripts().get(result.getName());
         if (dayScript == null) {
            dayScript = new TimelineScriptToken(result.getExecutionDate(), result.getPassedCount(),
               result.getFailedCount(), result.getScriptAborted());
         }

         ArtifactReadable script = result.getArtifactReadable().getRelated(
            CoreRelationTypes.TestScriptDefToTestScriptResults_TestScriptDef).getExactlyOne();
         ArtifactId teamId = script.getRelated(CoreRelationTypes.TestScriptDefToTeam_ScriptTeam).getAtMostOneOrDefault(
            ArtifactReadable.SENTINEL).getArtifactId();
         if (teamId.isInvalid()) {
            dayScript.setTeamName("");
         } else if (teamNames.containsKey(teamId)) {
            dayScript.setTeamName(teamNames.get(teamId));
         } else {
            ScriptTeamToken team = this.getTeam(branch, teamId);
            if (team.getArtifactId().isValid()) {
               teamNames.put(teamId, team.getName().getValue());
               dayScript.setTeamName(team.getName().getValue());
            }
         }

         // If a more recent script has already been included for the day, skip this one.
         if (dayScript.getExecutedAt().after(result.getExecutionDate())) {
            continue;
         }

         timelineDay.getScripts().put(result.getName(), dayScript);
         timelineDays.put(dayKey, timelineDay);
      }

      timeline.setDays(new LinkedList<>(timelineDays.values()));

      Map<String, TimelineScriptToken> currentScripts = new HashMap<>();

      for (TimelineDayToken day : timeline.getDays()) {
         for (String scriptName : day.getScripts().keySet()) {
            TimelineScriptToken script = day.getScripts().get(scriptName);
            currentScripts.put(scriptName, script);
         }

         int scriptsPass = 0;
         int scriptsFail = 0;
         int pointsPass = 0;
         int pointsFail = 0;
         int abort = 0;
         for (TimelineScriptToken script : currentScripts.values()) {
            pointsPass += script.getPass();
            pointsFail += script.getFail();
            abort += script.getAbort() ? 1 : 0;
            if (!script.getAbort() && script.getFail() == 0) {
               scriptsPass++;
            } else if (!script.getAbort()) {
               scriptsFail++;
            }
         }
         day.setScriptsPass(scriptsPass);
         day.setScriptsFail(scriptsFail);
         day.setPointsPass(pointsPass);
         day.setPointsFail(pointsFail);
         day.setAbort(abort);
      }

      timeline.setUpdatedAt(newTime);

      return timeline;
   }

   private TimelineStatsToken calculateTeamTimelineStats(TimelineStatsToken fullTimeline,
      TimelineStatsToken teamTimeline, String team) {
      Map<String, TimelineScriptToken> currentScripts = new HashMap<>();

      for (int i = 0; i < fullTimeline.getDays().size(); i++) {
         TimelineDayToken fullDay = fullTimeline.getDays().get(i);
         boolean dayChanged = false;
         for (String scriptName : fullDay.getScripts().keySet()) {
            TimelineScriptToken script = fullDay.getScripts().get(scriptName);
            if (team.isEmpty() || team.equals(script.getTeamName())) {
               currentScripts.put(scriptName, script);
               dayChanged = true;
            }
         }

         if (!dayChanged) {
            continue;
         }

         TimelineDayToken teamDay = new TimelineDayToken(fullDay.getExecutionDate());
         teamTimeline.getDays().add(teamDay);

         int scriptsPass = 0;
         int scriptsFail = 0;
         int pointsPass = 0;
         int pointsFail = 0;
         int abort = 0;
         for (TimelineScriptToken script : currentScripts.values()) {
            pointsPass += script.getPass();
            pointsFail += script.getFail();
            abort += script.getAbort() ? 1 : 0;
            if (!script.getAbort() && script.getFail() == 0) {
               scriptsPass++;
            } else if (!script.getAbort()) {
               scriptsFail++;
            }
         }
         teamDay.setScriptsPass(scriptsPass);
         teamDay.setScriptsFail(scriptsFail);
         teamDay.setPointsPass(pointsPass);
         teamDay.setPointsFail(pointsFail);
         teamDay.setAbort(abort);
      }

      return teamTimeline;
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
   public ScriptTeamToken getTeam(BranchId branch, ArtifactId id) {
      ArtifactAccessor<ScriptTeamToken> accessor = new ScriptTeamAccessor(CoreArtifactTypes.ScriptTeam, orcsApi);
      try {
         return accessor.get(branch, id);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         return ScriptTeamToken.SENTINEL;
      }
   }

   @Override
   public Collection<ScriptTeamToken> getTeams(BranchId branch, String filter, long pageNum, long pageSize,
      AttributeTypeToken orderByAttributeType) {
      ArtifactAccessor<ScriptTeamToken> accessor = new ScriptTeamAccessor(CoreArtifactTypes.ScriptTeam, orcsApi);
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
      ArtifactAccessor<ScriptTeamToken> accessor = new ScriptTeamAccessor(CoreArtifactTypes.ScriptTeam, orcsApi);
      return accessor.getAllByFilterAndCount(branch, filter, Arrays.asList(CoreAttributeTypes.Name));
   }

   private class ArtifactAccessorResultAccessor extends ArtifactAccessorImpl<ArtifactAccessorResultWithoutGammas> {
      public ArtifactAccessorResultAccessor(ArtifactTypeToken type, OrcsApi orcsApi) {
         super(type, orcsApi);
      }
   }

   private class ScriptTeamAccessor extends ArtifactAccessorImpl<ScriptTeamToken> {
      public ScriptTeamAccessor(ArtifactTypeToken type, OrcsApi orcsApi) {
         super(type, orcsApi);
      }
   }

}
