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

import java.util.Date;

/**
 * @author Donald G. Dunne
 */
public class AgileSprintDateData {

   private Date date;
   private Double completedPoints = new Double(0);
   private Double completedPlannedPoints = new Double(0);
   private Double completedUnPlannedPoints = new Double(0);
   private Double inCompletedUnPlannedPoints = new Double(0);
   private Double goalPoints = new Double(0);
   // Planned points plus current walkup
   private Double totalRealizedPoints = new Double(0);

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
         this.completedPoints = new Double(0);
      } else {
         this.completedPoints = completedPoints;
      }
   }

   public Double getCompletedPlannedPoints() {
      return completedPlannedPoints;
   }

   public void setCompletedPlannedPoints(Double completedPlannedPoints) {
      if (completedPlannedPoints == null) {
         this.completedPlannedPoints = new Double(0);
      } else {
         this.completedPlannedPoints = completedPlannedPoints;
      }
   }

   public Double getCompletedUnPlannedPoints() {
      return completedUnPlannedPoints;
   }

   public void setCompletedUnPlannedPoints(Double completedUnPlannedPoints) {
      if (completedUnPlannedPoints == null) {
         this.completedUnPlannedPoints = new Double(0);
      } else {
         this.completedUnPlannedPoints = completedUnPlannedPoints;
      }
   }

   public Double getGoalPoints() {
      return goalPoints;
   }

   public void setGoalPoints(Double goalPoints) {
      if (goalPoints == null) {
         this.goalPoints = new Double(0);
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
