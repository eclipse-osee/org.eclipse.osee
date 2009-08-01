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
package org.eclipse.osee.ats.artifact;

import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.ArtifactLabelProvider;

/**
 * @author Donald G. Dunne
 */
public class TeamWorkflowLabelProvider extends ArtifactLabelProvider {

   public TeamWorkflowLabelProvider() {
      super();
   }

   @Override
   public String getText(Object element) {
      TeamWorkFlowArtifact teamWf = (TeamWorkFlowArtifact) element;
      try {
         if (teamWf.getWorldViewTargetedVersion() != null) {
            return "[" + teamWf.getTeamName() + "][" + teamWf.getWorldViewTargetedVersionStr() + "] - " + teamWf.getName();
         } else {
            return "[" + teamWf.getTeamName() + "] - " + teamWf.getName();
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         return "Exception: " + ex.getLocalizedMessage();
      }
   }

}
