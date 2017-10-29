/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.agile.sprint;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.util.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public class SprintConfigurations {

   private ArtifactId id;
   // Date must be yyyy-mm-dd so angular datepicker can handle
   private String startDate;
   // Date must be yyyy-mm-dd so angular datepicker can handle
   private String endDate;
   private String plannedPoints;
   private String unPlannedPoints;
   private List<String> holidays = new LinkedList<>();
   private XResultData results = new XResultData();

   public SprintConfigurations() {
      // for jax-rs
   }

   public ArtifactId getId() {
      return id;
   }

   public void setId(ArtifactId id) {
      this.id = id;
   }

   public String getPlannedPoints() {
      return plannedPoints;
   }

   public void setPlannedPoints(String plannedPoints) {
      this.plannedPoints = plannedPoints;
   }

   public String getUnPlannedPoints() {
      return unPlannedPoints;
   }

   public void setUnPlannedPoints(String unPlannedPoints) {
      this.unPlannedPoints = unPlannedPoints;
   }

   public List<String> getHolidays() {
      return holidays;
   }

   public void setHolidays(List<String> holidays) {
      this.holidays = holidays;
   }

   public void addHoliday(String holidayStr) {
      this.holidays.add(holidayStr);
   }

   public String getStartDate() {
      return startDate;
   }

   /**
    * Date must be yyyy-mm-dd so angular datepicker can handle
    */
   public void setStartDate(String startDate) {
      this.startDate = startDate;
   }

   public String getEndDate() {
      return endDate;
   }

   /**
    * Date must be yyyy-mm-dd so angular datepicker can handle
    */
   public void setEndDate(String endDate) {
      this.endDate = endDate;
   }

   public XResultData getResults() {
      return results;
   }

   public void setResults(XResultData results) {
      this.results = results;
   }
}
