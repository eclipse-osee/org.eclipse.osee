/*
 * Copyright (c) 2012 Robert Bosch Engineering and Business Solutions Ltd India. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.osee.ats.reports.efficiency.team;

import java.util.List;

/**
 * Model Class to store the version efficiency list
 * 
 * @author Praveen Joseph
 */
public class TeamEfficiencyModel {

   private static List<VersionEfficiency> versionEfficiency;

   /**
    * @param versionEfficiency sets the version efficiency list
    */
   public static void setVersionEfficiency(final List<VersionEfficiency> versionEfficiency) {
      TeamEfficiencyModel.versionEfficiency = versionEfficiency;
   }

   /**
    * @return the version efficiency list
    */
   public static List<VersionEfficiency> getVersionEfficiency() {
      return versionEfficiency;
   }
}
