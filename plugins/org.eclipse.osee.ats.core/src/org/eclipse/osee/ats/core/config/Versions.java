/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.internal.Activator;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class Versions {

   private static Set<String> targetErrorLoggedForId = new HashSet<String>(10);

   public static Collection<String> getNames(Collection<? extends IAtsVersion> versions) {
      ArrayList<String> names = new ArrayList<String>();
      for (IAtsVersion version : versions) {
         names.add(version.getName());
      }
      return names;
   }

   public static String getTargetedVersionStr(Object object) throws OseeCoreException {
      if (object instanceof IAtsWorkItem) {
         IAtsTeamWorkflow teamWf = ((IAtsWorkItem) object).getParentTeamWorkflow();
         if (teamWf != null) {
            IAtsVersion version = AtsVersionService.get().getTargetedVersion(object);
            if (version != null) {
               if (!teamWf.getStateMgr().getStateType().isCompletedOrCancelled() && AtsVersionService.get().isReleased(
                  teamWf)) {
                  String errStr =
                     "Workflow " + teamWf.getAtsId() + " targeted for released version, but not completed: " + version;
                  // only log error once
                  if (!targetErrorLoggedForId.contains(teamWf.getGuid())) {
                     OseeLog.log(Activator.class, Level.SEVERE, errStr, null);
                     targetErrorLoggedForId.add(teamWf.getGuid());
                  }
                  return "!Error " + errStr;
               }
               return version.getName();
            }
         }
      }
      return "";
   }

}
