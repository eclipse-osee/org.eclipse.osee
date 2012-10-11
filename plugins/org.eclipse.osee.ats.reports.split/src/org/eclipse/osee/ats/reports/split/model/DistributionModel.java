/*
 * Copyright (c) 2012 Robert Bosch Engineering and Business Solutions Ltd India. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.osee.ats.reports.split.model;

/**
 * Model class to store the data
 * 
 * @author Chandan Bandemutt
 */
public class DistributionModel {

   private static AIDistributionEntry aiSplitEntry;
   private static TeamDistributionEntry teamSplitEntry;
   private static StateDistributionEntry stateSplitEntry;

   /**
    * @return the Actionable Item DistributionEntry
    */
   public static AIDistributionEntry getAiSplitEntry() {
      return aiSplitEntry;
   }

   /**
    * @param aiSpltEntry : sets the Actionable Item split entry
    */
   public static void setAiSplitEntry(final AIDistributionEntry aiSpltEntry) {
      DistributionModel.aiSplitEntry = aiSpltEntry;
   }

   /**
    * @return the Actionable Item split entry
    */
   public static TeamDistributionEntry getTeamSplitEntry() {
      return teamSplitEntry;
   }

   /**
    * @param teamSplitEntry : sets the teamSplitEntry
    */
   public static void setTeamSplitEntry(final TeamDistributionEntry teamSplitEntry) {
      DistributionModel.teamSplitEntry = teamSplitEntry;
   }

   /**
    * @return the StateDistributionEntry
    */
   public static StateDistributionEntry getStateSplitEntry() {
      return stateSplitEntry;
   }

   /**
    * @param stateSplitEntry : sets the StateDistributionEntry
    */
   public static void setStateSplitEntry(final StateDistributionEntry stateSplitEntry) {
      DistributionModel.stateSplitEntry = stateSplitEntry;
   }
}
