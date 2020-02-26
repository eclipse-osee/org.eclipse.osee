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

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;

/**
 * @author Donald G. Dunne
 */
public class AgileSprintData {

   private String agileTeamName;
   private String sprintName;
   private String startDate;
   private Date sDate;
   private String endDate;
   private Date eDate;
   private List<Date> holidays = new LinkedList<>();
   private String pointsAttrTypeName;
   private Integer plannedPoints = 0;
   private Integer unPlannedPoints = null;
   private List<AgileSprintDateData> dates = new ArrayList<>();
   private XResultData results = new XResultData();

   public XResultData validate() {
      results.validateNotNull(getStartDateAsDate(), "Start Date");
      results.validateNotNull(getEndDateAsDate(), "End Date");
      results.validateNotNullOrEmpty(pointsAttrTypeName, "Points Attribute Type");
      results.validateNotNull(plannedPoints, "Planned Points");
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

   @JsonIgnore
   public Date getStartDateAsDate() {
      if (sDate == null) {
         if (org.eclipse.osee.framework.jdk.core.util.Strings.isNumeric(startDate)) {
            sDate = new Date(Long.valueOf(startDate));
         } else {
            try {
               sDate = DateUtil.getDate(DateUtil.YYYYMMDD, startDate);
            } catch (Exception ex) {
               // do nothing
            }
         }
      }
      return sDate;
   }

   public void setStartDateAsDate(Date startDate) {
      this.sDate = startDate;
   }

   public void setEndDateAsDate(Date endDate) {
      this.eDate = endDate;
   }

   @JsonIgnore
   public Date getEndDateAsDate() {
      if (eDate == null) {
         if (org.eclipse.osee.framework.jdk.core.util.Strings.isNumeric(endDate)) {
            eDate = new Date(Long.valueOf(endDate));
         } else {
            try {
               eDate = DateUtil.getDate(DateUtil.YYYYMMDD, endDate);
            } catch (Exception ex) {
               // do nothing
            }
         }
      }
      return eDate;
   }

   public void setStartDate(String startDate) {
      this.startDate = startDate;
   }

   public String getStartDate() {
      return startDate;
   }

   public void setEndDate(String endDate) {
      this.endDate = endDate;
   }

   public String getEndDate() {
      return endDate;
   }

}
