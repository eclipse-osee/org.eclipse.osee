/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.ats.api.agile;

import java.util.Date;

/**
 * @author Donald G. Dunne
 */
public class AgileSprintDateData {

   private Date date;
   private Double completedPoints = Double.valueOf(0);
   private Double completedPlannedPoints = Double.valueOf(0);
   private Double completedUnPlannedPoints = Double.valueOf(0);
   private Double inCompletedUnPlannedPoints = Double.valueOf(0);
   private Double goalPoints = Double.valueOf(0);
   // Planned points plus current walkup
   private Double totalRealizedPoints = Double.valueOf(0);

   public Date getDate() {
      return date;
   }

   public void setDate(Date date) {
      this.date = date;
   }

   public Double getCompletedPoints() {
      return completedPoints;
   }

   public void setCompletedPoints(Double completedPoints) {
      if (completedPoints == null) {
         this.completedPoints = Double.valueOf(0);
      } else {
         this.completedPoints = completedPoints;
      }
   }

   public Double getCompletedPlannedPoints() {
      return completedPlannedPoints;
   }

   public void setCompletedPlannedPoints(Double completedPlannedPoints) {
      if (completedPlannedPoints == null) {
         this.completedPlannedPoints = Double.valueOf(0);
      } else {
         this.completedPlannedPoints = completedPlannedPoints;
      }
   }

   public Double getCompletedUnPlannedPoints() {
      return completedUnPlannedPoints;
   }

   public void setCompletedUnPlannedPoints(Double completedUnPlannedPoints) {
      if (completedUnPlannedPoints == null) {
         this.completedUnPlannedPoints = Double.valueOf(0);
      } else {
         this.completedUnPlannedPoints = completedUnPlannedPoints;
      }
   }

   public Double getGoalPoints() {
      return goalPoints;
   }

   public void setGoalPoints(Double goalPoints) {
      if (goalPoints == null) {
         this.goalPoints = Double.valueOf(0);
      } else {
         this.goalPoints = goalPoints;
      }
   }

   public Double getInCompletedUnPlannedPoints() {
      return inCompletedUnPlannedPoints;
   }

   public void setInCompletedUnPlannedPoints(Double inCompletedUnPlannedPoints) {
      this.inCompletedUnPlannedPoints = inCompletedUnPlannedPoints;
   }

   /**
    * @return Total Planned and added unplanned work as it comes in. This is the real total work by date, regardless of
    * planned or planned-unplanned.
    */
   public Double getTotalRealizedPoints() {
      return totalRealizedPoints;
   }

   public void setTotalRealizedPoints(Double totalRealizedPoints) {
      this.totalRealizedPoints = totalRealizedPoints;
   }

}
