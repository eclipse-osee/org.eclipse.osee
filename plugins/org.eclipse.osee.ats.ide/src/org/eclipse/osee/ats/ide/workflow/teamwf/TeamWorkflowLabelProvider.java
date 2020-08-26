/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.workflow.teamwf;

import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.ArtifactLabelProvider;

/**
 * @author Donald G. Dunne
 */
public class TeamWorkflowLabelProvider extends ArtifactLabelProvider {

   @Override
   public String getText(Object element) {
      TeamWorkFlowArtifact teamWf = (TeamWorkFlowArtifact) element;
      try {
         if (AtsApiService.get().getVersionService().hasTargetedVersion(teamWf)) {
            return "[" + teamWf.getTeamName() + "][" + AtsApiService.get().getVersionService().getTargetedVersionStr(
               teamWf, AtsApiService.get().getVersionService()) + "] - " + teamWf.getName();
         } else {
            return "[" + teamWf.getTeamName() + "] - " + teamWf.getName();
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         return "Exception: " + ex.getLocalizedMessage();
      }
   }

}
