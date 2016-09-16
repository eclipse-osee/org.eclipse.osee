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
package org.eclipse.osee.ats.rest.internal.agile;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.agile.AgileBurndown;
import org.eclipse.osee.ats.api.agile.AgileBurndownDate;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;

/**
 * @author Donald G. Dunne
 */
public class SprintBurndownPageBuilder {

   private static final String N_A = "0";
   private final AgileBurndown burn;

   public SprintBurndownPageBuilder(AgileBurndown burn) {
      this.burn = burn;
   }

   public String getHtml() {

      Date today = new Date();

      StringBuilder sb = new StringBuilder();
      sb.append(AHTML.beginMultiColumnTable(95, 1));

      // Add Sprint row
      sb.append(AHTML.addRowMultiColumnTable("Sprint", burn.getSprintName()));

      // Add Start row
      sb.append(AHTML.addRowMultiColumnTable("Start", DateUtil.get(burn.getStartDate(), DateUtil.MMDDYY), "End",
         DateUtil.get(burn.getEndDate(), DateUtil.MMDDYY), "", "Days", String.valueOf(burn.getDates().size()),
         "Planned", String.valueOf(burn.getPlannedPoints().toString()), "Un-Planned",
         String.valueOf(burn.getUnPlannedPoints().toString())));

      // Add Holidays
      List<String> holidays = new LinkedList<>();
      holidays.add("Holidays");
      for (Date holiday : burn.getHolidays()) {
         holidays.add(DateUtil.get(holiday, DateUtil.MMDDYY));
      }
      sb.append(AHTML.addRowMultiColumnTable(holidays.toArray(new String[holidays.size()])));

      // Add day number row
      List<String> strs = new LinkedList<>();
      strs.add("");
      for (int count = 1; count <= burn.getDates().size(); count++) {
         strs.add(String.valueOf(count));
      }
      sb.append(AHTML.addRowMultiColumnTable(strs.toArray(new String[strs.size()])));

      // Add dates row
      strs = new LinkedList<>();
      strs.add("");
      for (AgileBurndownDate date : burn.getDates()) {
         strs.add(DateUtil.get(date.getDate(), DateUtil.MMDDYY));
      }
      sb.append(AHTML.addRowMultiColumnTable(strs.toArray(new String[strs.size()])));

      // Add Total Points
      strs = new LinkedList<>();
      strs.add("Total Points");
      for (int count = 1; count <= burn.getDates().size(); count++) {
         strs.add(String.valueOf(burn.getPlannedPoints() + burn.getUnPlannedPoints()));
      }
      sb.append(AHTML.addRowMultiColumnTable(strs.toArray(new String[strs.size()])));

      // Add Goal
      strs = new LinkedList<>();
      strs.add("Goal");
      for (AgileBurndownDate date : burn.getDates()) {
         strs.add(String.valueOf(date.getGoalPoints()));
      }
      sb.append(AHTML.addRowMultiColumnTable(strs.toArray(new String[strs.size()])));

      // Add Planned Complete
      strs = new LinkedList<>();
      strs.add("Planned Complete");
      for (AgileBurndownDate date : burn.getDates()) {
         if (date.getDate().after(today)) {
            strs.add(N_A);
         } else {
            strs.add(String.valueOf(date.getCompletedPlannedPoints()));
         }
      }
      sb.append(AHTML.addRowMultiColumnTable(strs.toArray(new String[strs.size()])));

      // Add Planned Remaining
      strs = new LinkedList<>();
      strs.add("Planned Remaining");
      for (AgileBurndownDate date : burn.getDates()) {
         if (date.getCompletedPlannedPoints() == null) {
            strs.add(N_A);
         } else {
            strs.add(String.valueOf(burn.getPlannedPoints() - date.getCompletedPlannedPoints()));
         }
      }
      sb.append(AHTML.addRowMultiColumnTable(strs.toArray(new String[strs.size()])));

      // Add UnPlanned Complete
      strs = new LinkedList<>();
      strs.add("UnPlanned Complete");
      for (AgileBurndownDate date : burn.getDates()) {
         if (date.getCompletedUnPlannedPoints() == null) {
            strs.add(N_A);
         } else {
            strs.add(String.valueOf(date.getCompletedUnPlannedPoints()));
         }
      }
      sb.append(AHTML.addRowMultiColumnTable(strs.toArray(new String[strs.size()])));

      // Add UnPlanned Incomplete
      strs = new LinkedList<>();
      strs.add("UnPlanned Incomplete");
      for (AgileBurndownDate date : burn.getDates()) {
         if (date.getCompletedUnPlannedPoints() == null) {
            strs.add(N_A);
         } else {
            strs.add(String.valueOf(date.getInCompletedUnPlannedPoints()));
         }
      }
      sb.append(AHTML.addRowMultiColumnTable(strs.toArray(new String[strs.size()])));

      // Add UnPlanned Unrealized
      strs = new LinkedList<>();
      strs.add("UnPlanned Unrealized");
      for (AgileBurndownDate date : burn.getDates()) {
         if (date.getCompletedUnPlannedPoints() == null) {
            strs.add(N_A);
         } else {
            strs.add(String.valueOf(date.getUnRealizedWalkup())); // TBD
         }
      }
      sb.append(AHTML.addRowMultiColumnTable(strs.toArray(new String[strs.size()])));

      // Add Total Completed
      strs = new LinkedList<>();
      strs.add("Total Completed");
      for (AgileBurndownDate date : burn.getDates()) {
         if (date.getCompletedPoints() == null) {
            strs.add(N_A);
         } else {
            strs.add(String.valueOf(date.getCompletedPoints()));
         }
      }
      sb.append(AHTML.addRowMultiColumnTable(strs.toArray(new String[strs.size()])));

      // Add Total Remaining
      strs = new LinkedList<>();
      strs.add("Total Remaining");
      for (AgileBurndownDate date : burn.getDates()) {
         if (date.getDate().after(today)) {
            strs.add(N_A);
         } else {
            strs.add(String.valueOf(
               ((burn.getPlannedPoints() - date.getCompletedPlannedPoints()) + date.getUnRealizedWalkup() + date.getInCompletedUnPlannedPoints())));
         }
      }
      sb.append(AHTML.addRowMultiColumnTable(strs.toArray(new String[strs.size()])));

      sb.append(AHTML.endMultiColumnTable());
      return AHTML.simplePage(sb.toString());
   }

}
