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
package org.eclipse.osee.ats.core.agile;

import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.agile.AgileSprintData;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;

/**
 * @author Donald G. Dunne
 */
public class SprintUtil {

   public static String RGB_GREEN = "3, 127, 24";
   public static String RGB_BLACK = "0, 0, 0";
   public static String RGB_BLUE = "0, 4, 140";
   public static String RGB_YELLOW = "252, 252, 2";
   public static final String POINTS = "Points";
   public static final String DATES = "Dates";
   public static final String COMPLETED_UN_PLANNED = "Completed Un-Planned";
   public static final String COMPLETED_PLANNED = "Completed Planned";
   public static final String TOTAL_COMPLETED = "Total Completed";
   public static final String TOTAL_POINTS = "Total Points";
   public static final String TOTAL_REALIZED_POINTS = "Total Realized Points";
   public static final String REMAINING_WORK = "Remaining Work";
   public static final String TOTAL_WORK = "Total Work";

   private SprintUtil() {
      // utility class
   }

   public static AgileSprintData getAgileSprintData(IAtsServices services, long teamUuid, long sprintUuid) {
      if (teamUuid <= 0) {
         throw new OseeArgumentException("teamUuid %s is not valid", teamUuid);
      }
      if (sprintUuid <= 0) {
         throw new OseeArgumentException("sprintUuid %s is not valid", sprintUuid);
      }
      ArtifactToken sprintArt = services.getArtifact(sprintUuid);
      IAgileTeam agileTeam = services.getAgileService().getAgileTeam(teamUuid);
      IAgileSprint sprint = services.getAgileService().getAgileSprint(sprintArt);

      SprintDataBuilder builder = new SprintDataBuilder(agileTeam, sprint, services);
      AgileSprintData burndown = builder.get();
      return burndown;
   }

}
