/*********************************************************************
 * Copyright (c) 2021 Robert Bosch Engineering and Business Solutions Ltd India
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Robert Bosch Engineering and Business Solutions Ltd India - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.icteam.web.rest.layer.util;

import java.util.Collection;
import java.util.List;

/**
 * Model class to hold burn down data
 *
 * @author Ajay Chandrahasan
 */
public class BurnDownData {
   private List<String> sprintDates;
   private Collection<Float> remainingTime;
   private float totalTime;
   private int totalStoryPoints;
   private int noOfDays;
   private Collection<Float> remainingStoryPoints;
   private Collection<Float> practicalStoryPoints;

   /**
    * @return
    */
   public List<String> getDatesSet() {
      return sprintDates;
   }

   public void setDatesSet(List<String> datesSet) {
      this.sprintDates = datesSet;
   }

   public Collection<Float> getRemainingTimeSet() {
      return remainingTime;
   }

   public void setRemainingTimeSet(Collection<Float> remainingTimeSet) {
      this.remainingTime = remainingTimeSet;
   }

   public float getTotalTime() {
      return totalTime;
   }

   public void setTotalTime(float totalTime) {
      this.totalTime = totalTime;
   }

   public int getNoOfDays() {
      return noOfDays;
   }

   public void setNoOfDays(int noOfDays) {
      this.noOfDays = noOfDays;
   }

   public Collection<Float> getRemainingStoryPoints() {
      return remainingStoryPoints;
   }

   public void setRemainingStoryPoints(Collection<Float> remainingStoryPoints) {
      this.remainingStoryPoints = remainingStoryPoints;
   }

   public int getTotalStoryPoints() {
      return totalStoryPoints;
   }

   public void setTotalStoryPoints(int totalStoryPoints) {
      this.totalStoryPoints = totalStoryPoints;
   }

   public Collection<Float> getPracticalStoryPoints() {
      return practicalStoryPoints;
   }

   public void setPracticalStoryPoints(Collection<Float> practicalStoryPoints) {
      this.practicalStoryPoints = practicalStoryPoints;
   }
}
