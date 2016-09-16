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
public class AgileBurndownDate {

   private Date date;
   private Double completedPoints = new Double(0);
   private Double completedPlannedPoints = new Double(0);
   private Double completedUnPlannedPoints = new Double(0);
   private Double inCompletedUnPlannedPoints = new Double(0);
   private Double goalPoints = new Double(0);
   private Double unRealizedWalkup = new Double(0);

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
      this.completedPoints = completedPoints;
   }

   public Double getCompletedPlannedPoints() {
      return completedPlannedPoints;
   }

   public void setCompletedPlannedPoints(Double completedPlannedPoints) {
      this.completedPlannedPoints = completedPlannedPoints;
   }

   public Double getCompletedUnPlannedPoints() {
      return completedUnPlannedPoints;
   }

   public void setCompletedUnPlannedPoints(Double completedUnPlannedPoints) {
      this.completedUnPlannedPoints = completedUnPlannedPoints;
   }

   public Double getGoalPoints() {
      return goalPoints;
   }

   public void setGoalPoints(Double goalPoints) {
      this.goalPoints = goalPoints;
   }

   public Double getUnRealizedWalkup() {
      return unRealizedWalkup;
   }

   public void setUnRealizedWalkup(Double unRealizedWalkup) {
      this.unRealizedWalkup = unRealizedWalkup;
   }

   public Double getInCompletedUnPlannedPoints() {
      return inCompletedUnPlannedPoints;
   }

   public void setInCompletedUnPlannedPoints(Double inCompletedUnPlannedPoints) {
      this.inCompletedUnPlannedPoints = inCompletedUnPlannedPoints;
   }

}
