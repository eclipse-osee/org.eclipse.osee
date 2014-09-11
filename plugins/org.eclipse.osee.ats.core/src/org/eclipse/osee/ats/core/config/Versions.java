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
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.version.IAtsVersionService;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public class Versions {

   public static Collection<String> getNames(Collection<? extends IAtsVersion> versions) {
      ArrayList<String> names = new ArrayList<String>();
      for (IAtsVersion version : versions) {
         names.add(version.getName());
      }
      return names;
   }

   public static String getTargetedVersionStr(Object object, IAtsVersionService versionService) throws OseeCoreException {
      if (object instanceof IAtsWorkItem) {
         IAtsTeamWorkflow teamWf = ((IAtsWorkItem) object).getParentTeamWorkflow();
         if (teamWf != null) {
            IAtsVersion version = versionService.getTargetedVersion(object);
            if (version != null) {
               if (!teamWf.getStateMgr().getStateType().isCompletedOrCancelled() && versionService.isReleased(teamWf)) {
                  String errStr =
                     "Workflow " + teamWf.getAtsId() + " targeted for released version, but not completed: " + version;
                  return "!Error " + errStr;
               }
               return version.getName();
            }
         }
      }
      return "";
   }

}
