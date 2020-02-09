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
package org.eclipse.osee.ats.ide.workflow.teamwf;

import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
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
         if (AtsClientService.get().getVersionService().hasTargetedVersion(teamWf)) {
            return "[" + teamWf.getTeamName() + "][" + AtsClientService.get().getVersionService().getTargetedVersionStr(
               teamWf, AtsClientService.get().getVersionService()) + "] - " + teamWf.getName();
         } else {
            return "[" + teamWf.getTeamName() + "] - " + teamWf.getName();
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         return "Exception: " + ex.getLocalizedMessage();
      }
   }

}
