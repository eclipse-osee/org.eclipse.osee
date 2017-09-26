/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.agile;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.util.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public class AgileSprintData {

   private String agileTeamName;
   private String sprintName;
   private Date startDate;
   private Date endDate;
   private List<Date> holidays = new LinkedList<>();
   private String pointsAttrTypeName;
   private Integer plannedPoints = 0;
   private Integer unPlannedPoints = null;
   private List<AgileSprintDateData> dates = new ArrayList<AgileSprintDateData>();
   private XResultData results = new XResultData();

   public XResultData validate() {
      results.validateNotNull(startDate, "Start Date");
      results.validateNotNull(endDate, "End Date");
      results.validateNotNullOrEmpty(pointsAttrTypeName, "Points Attribute Type");
      results.validateNotNull(plannedPoints, "Planned Points");
      results.validateTrue(unPlannedPoints == null || unPlannedPoints > 0,
         "Un-Planned Points must either be specified or > 0");
      return results;
   }

   public String getAgileTeamName() {
      return agileTeamName;
   }

   public void setAgileTeamName(String agileTeamName) {
      this.agileTeamName = agileTeamName;
   }

   public String getSprintName() {
      return sprintName;
   }

   public void setSprintName(String sprintName) {
      this.sprintName = sprintName;
   }

   public Date getStartDate() {
      return startDate;
   }

   public void setStartDate(Date startDate) {
      this.startDate = startDate;
   }

   public Date getEndDate() {
      return endDate;
   }

   public void setEndDate(Date endDate) {
      this.endDate = endDate;
   }

   public List<Date> getHolidays() {
      return holidays;
   }

   public void setHolidays(List<Date> holidays) {
      this.holidays = holidays;
   }

   public String getPointsAttrTypeName() {
      return pointsAttrTypeName;
   }

   public void setPointsAttrTypeName(String pointsAttrTypeName) {
      this.pointsAttrTypeName = pointsAttrTypeName;
   }

   public Integer getPlannedPoints() {
      return plannedPoints;
   }

   public void setPlannedPoints(Integer plannedPoints) {
      this.plannedPoints = plannedPoints;
   }

   public Integer getUnPlannedPoints() {
      return unPlannedPoints;
   }

   public void setUnPlannedPoints(Integer unPlannedPoints) {
      this.unPlannedPoints = unPlannedPoints;
   }

   public List<AgileSprintDateData> getDates() {
      return dates;
   }

   public void setDates(List<AgileSprintDateData> dates) {
      this.dates = dates;
   }

   public XResultData getResults() {
      return results;
   }

   public void setResults(XResultData results) {
      this.results = results;
   }

}
