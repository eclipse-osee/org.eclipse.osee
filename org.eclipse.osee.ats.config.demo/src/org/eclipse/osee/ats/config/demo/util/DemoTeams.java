/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.config.demo.util;

import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.config.demo.OseeAtsConfigDemoPlugin;
import org.eclipse.osee.framework.database.DatabaseActivator;
import org.eclipse.osee.framework.db.connection.OseeDb;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * Convenience method for demo plugin to retrieve configured teams for use mostly in DemoNavigateViewItem.
 * 
 * @author Donald G. Dunne
 */
public class DemoTeams {

   public static enum Team {
      Process_Team, Tools_Team, SAW_HW, SAW_SW, CIS_SW, Facilities_Team
   };
   private static DemoTeams instance = new DemoTeams();

   private DemoTeams() {
   }

   public static DemoTeams getInstance() {
      return instance;
   }

   public TeamDefinitionArtifact getTeamDef(Team team) throws Exception {
      // Add check to keep exception from occurring for OSEE developers running against production
      OseeDb.getDefaultDatabaseService();
      if (DatabaseActivator.getInstance().isProductionDb()) return null;
      try {
         return (TeamDefinitionArtifact) ArtifactQuery.getArtifactFromTypeAndName(TeamDefinitionArtifact.ARTIFACT_NAME,
               team.name().replaceAll("_", " "), AtsPlugin.getAtsBranch());
      } catch (Exception ex) {
         OSEELog.logException(OseeAtsConfigDemoPlugin.class, ex, false);
      }
      return null;
   }
}
