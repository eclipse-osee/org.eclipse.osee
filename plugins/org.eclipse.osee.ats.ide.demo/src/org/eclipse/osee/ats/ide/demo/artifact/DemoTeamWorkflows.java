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

package org.eclipse.osee.ats.ide.demo.artifact;

import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.demo.AtsDemoOseeTypes;
import org.eclipse.osee.ats.api.team.ITeamWorkflowProvider;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.demo.internal.Activator;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class DemoTeamWorkflows implements ITeamWorkflowProvider {

   @Override
   public boolean isResponsibleFor(IAtsWorkItem workItem) {
      try {
         TeamWorkFlowArtifact teamWf = (TeamWorkFlowArtifact) workItem.getParentTeamWorkflow();
         if (teamWf != null) {
            return teamWf.isTypeEqual(AtsDemoOseeTypes.DemoCodeTeamWorkflow) || teamWf.isTypeEqual(
               AtsDemoOseeTypes.DemoReqTeamWorkflow) || teamWf.isTypeEqual(AtsDemoOseeTypes.DemoTestTeamWorkflow);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return false;
   }

   @Override
   public String getBranchName(IAtsTeamWorkflow teamWf, String defaultBranchName) {
      try {
         if (teamWf.getTeamDefinition().getName().contains("SAW Test")) {
            return "SAW Test - " + defaultBranchName;
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return null;
   }

}
