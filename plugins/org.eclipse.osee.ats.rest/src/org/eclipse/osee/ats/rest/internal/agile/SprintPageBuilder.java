/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.agile;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map.Entry;
import java.util.TreeMap;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResourceToken;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.template.engine.PageCreator;

/**
 * @author David.W.Miller
 */
public class SprintPageBuilder {
   private final ArtifactReadable sprint;
   private Date startDate;
   private Date endDate;
   private final SimpleDateFormat df = new SimpleDateFormat("EEE, MMM d, yyyy");
   private int numActionsCompleted = 0;
   private double workCompleted = 0;
   private double walkupCompleted = 0;
   private int numActionsStarted = 0;
   private int numActionsBacklog = 0;
   TreeMap<String, FeatureGroupSum> featureSums = new TreeMap<>();
   private final ArtifactReadable team;
   private final AtsApi atsApi;

   public SprintPageBuilder(ArtifactReadable team, ArtifactReadable sprint, AtsApi atsApi) {
      this.team = team;
      this.sprint = sprint;
      this.atsApi = atsApi;
   }

   public String generatePage(PageCreator page, ResourceToken token) {
      calculateData();
      page.addKeyValuePairs("sprintName", sprint.getName());
      page.addKeyValuePairs("teamName", team.getName());
      page.addKeyValuePairs("summaryDate", DateUtil.getDateNow(DateUtil.MMDDYYHHMM));
      page.addKeyValuePairs("beginDate", getStartDate());
      page.addKeyValuePairs("endDate", getEndDate());
      page.addKeyValuePairs("workCompleted", getWorkCompleted());
      page.addKeyValuePairs("walkupCompleted", getWalkupCompleted());
      page.addKeyValuePairs("numActionsCompleted", getNumActionsCompleted());
      page.addKeyValuePairs("numActionsStarted", getNumActionsStarted());
      page.addKeyValuePairs("numActionsBacklog", getNumActionsBacklog());
      page.addKeyValuePairs("tableContent", getTable());
      String html = page.realizePage(token);
      html = AtsUtil.resolveAjaxToBaseApplicationServer(html, atsApi);
      return html;
   }

   private void calculateData() {
      initCalculations();
      calculateBacklogCount();
      calculateActionsCreated();
      featureSums.clear();
      ResultSet<ArtifactReadable> actions = sprint.getRelated(AtsRelationTypes.AgileSprintToItem_AtsItem);

      for (ArtifactReadable item : actions) {
         if (includeInCount(item)) {
            ++numActionsCompleted;
            double points = getPointsFromAction(item);
            if (item.getSoleAttributeValue(AtsAttributeTypes.UnplannedWork, false)) {
               walkupCompleted += points;
            } else {
               workCompleted += points;
            }
            updateFeatureGroupSum(item, featureSums, points);
         }
      }
   }

   private double getPointsFromAction(ArtifactReadable item) {
      double points = 0;
      ArtifactReadable agileTeam =
         sprint.getRelated(AtsRelationTypes.AgileTeamToSprint_AgileTeam).getOneOrDefault(ArtifactReadable.SENTINEL);
      if (agileTeam.isValid()) {
         String pointsAttrType = agileTeam.getSoleAttributeAsString(AtsAttributeTypes.PointsAttributeType, "");
         if (Strings.isValid(pointsAttrType) && pointsAttrType.equals(AtsAttributeTypes.PointsNumeric.getName())) {
            points = item.getSoleAttributeValue(AtsAttributeTypes.PointsNumeric, 0.0);
         } else {
            String value = item.getSoleAttributeAsString(AtsAttributeTypes.Points, "0");
            if (Strings.isNumeric(value)) {
               points = Double.parseDouble(value);
            }
         }
      }
      return points;
   }

   private ArtifactReadable calculateActionsCreated() {
      ArtifactReadable agileTeam =
         sprint.getRelated(AtsRelationTypes.AgileTeamToSprint_AgileTeam).getOneOrDefault(ArtifactReadable.SENTINEL);
      if (agileTeam.isValid()) {
         // get items created and closed during sprint
         ResultSet<ArtifactReadable> agileItems = sprint.getRelated(AtsRelationTypes.AgileSprintToItem_AtsItem);
         numActionsStarted += countIfInTimeFrame(agileItems);
         // get items created and still open (in backlog) during the sprint time frame
         ArtifactReadable backlog = agileTeam.getRelated(AtsRelationTypes.AgileTeamToBacklog_Backlog).getOneOrDefault(
            ArtifactReadable.SENTINEL);
         if (backlog.isValid()) {
            ResultSet<ArtifactReadable> backlogItems = backlog.getRelated(AtsRelationTypes.Goal_Member);
            numActionsStarted += countIfInTimeFrame(backlogItems);
         }
      }
      return agileTeam;
   }

   private int countIfInTimeFrame(ResultSet<ArtifactReadable> agileItems) {
      int numFound = 0;
      for (ArtifactReadable item : agileItems) {
         Date artCreated = item.getSoleAttributeValue(AtsAttributeTypes.CreatedDate, null);
         if (artCreated != null && artCreated.after(startDate) && artCreated.before(endDate)) {
            numFound++;
         }
      }
      return numFound;
   }

   private void updateFeatureGroupSum(ArtifactReadable item, TreeMap<String, FeatureGroupSum> featureSums, double points) {
      String featureGroupName = null;
      ArtifactReadable featureGroup =
         item.getRelated(AtsRelationTypes.AgileFeatureToItem_FeatureGroup).getAtMostOneOrDefault(
            ArtifactReadable.SENTINEL);
      if (featureGroup.isInvalid()) {
         featureGroupName = "UnSet";
      } else {
         featureGroupName = featureGroup.getName();
      }
      FeatureGroupSum feature = featureSums.get(featureGroupName);
      if (feature == null) {
         feature = new FeatureGroupSum(featureGroupName,
            featureGroup == null ? "" : featureGroup.getSoleAttributeAsString(AtsAttributeTypes.Description, ""));
      }
      feature.addToSum(points);
      featureSums.put(featureGroupName, feature);
   }

   private boolean includeInCount(ArtifactReadable item) {
      if ("Completed".equals(item.getSoleAttributeAsString(AtsAttributeTypes.CurrentStateType)) || "Cancelled".equals(
         item.getSoleAttributeAsString(AtsAttributeTypes.CurrentStateType))) {
         return true;
      }
      return false;
   }

   private void calculateBacklogCount() {
      ArtifactReadable team =
         sprint.getRelated(AtsRelationTypes.AgileTeamToSprint_AgileTeam).getOneOrDefault(ArtifactReadable.SENTINEL);
      if (team.isValid()) {
         ArtifactReadable goal =
            team.getRelated(AtsRelationTypes.AgileTeamToBacklog_Backlog).getOneOrDefault(ArtifactReadable.SENTINEL);
         if (goal.isValid()) {
            ResultSet<ArtifactReadable> members = goal.getRelated(AtsRelationTypes.Goal_Member);
            numActionsBacklog = members.size();
         }
      }
   }

   private void initCalculations() {
      try {
         // since the sprint start is set to whatever time the sprint date was chosen,
         // and the sprint end is similar, there is a possiblity of a gap between sprint dates
         // to close the gap, the start date is set to hour, min, sec = 0, and the end date is 23:59:59
         startDate = adjustDate((Date) sprint.getSoleAttributeValue(AtsAttributeTypes.StartDate), false);
         endDate = adjustDate((Date) sprint.getSoleAttributeValue(AtsAttributeTypes.EndDate), true);
      } catch (Exception e) {
         throw new OseeCoreException("Start Date and End Date must be set in Sprint [%s]", sprint.getName());
      }
      numActionsCompleted = 0;
      workCompleted = 0;
      walkupCompleted = 0;
      numActionsCompleted = 0;
      numActionsStarted = 0;
      numActionsBacklog = 0;
   }

   private Date adjustDate(Date date, boolean isEnd) {
      int hour = 0;
      int min = 0;
      int sec = 0;
      if (isEnd) {
         hour = 23;
         min = 59;
         sec = 59;
      }
      Calendar cal = Calendar.getInstance();
      cal.setTime(date);
      cal.set(Calendar.HOUR_OF_DAY, hour);
      cal.set(Calendar.MINUTE, min);
      cal.set(Calendar.SECOND, sec);
      cal.set(Calendar.MILLISECOND, 0);
      return cal.getTime();
   }

   private String getStartDate() {
      return df.format(startDate);
   }

   private String getEndDate() {
      return df.format(endDate);
   }

   private String getWorkCompleted() {
      return Integer.valueOf(Double.valueOf(workCompleted).intValue()).toString();
   }

   private String getWalkupCompleted() {
      return Integer.valueOf(Double.valueOf(walkupCompleted).intValue()).toString();
   }

   private String getNumActionsCompleted() {
      return Integer.toString(numActionsCompleted);
   }

   private String getNumActionsStarted() {
      return Integer.toString(numActionsStarted);
   }

   private String getNumActionsBacklog() {
      return Integer.toString(numActionsBacklog);
   }

   private String getTable() {
      StringBuilder sb = new StringBuilder();
      for (Entry<String, FeatureGroupSum> feature : featureSums.entrySet()) {
         sb.append(feature.getValue().getHTML());
      }
      return sb.toString();
   }
}
