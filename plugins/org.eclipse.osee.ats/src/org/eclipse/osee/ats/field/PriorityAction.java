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

package org.eclipse.osee.ats.field;

import org.eclipse.jface.action.Action;
import org.eclipse.osee.ats.actions.ISelectedTeamWorkflowArtifacts;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.world.WorldXViewer;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class PriorityAction extends Action {

   private final ISelectedTeamWorkflowArtifacts selectedTeamWorkflowArtifacts;
   private final WorldXViewer worldXViewer;

   public PriorityAction(WorldXViewer worldXViewer, ISelectedTeamWorkflowArtifacts selectedTeamWorkflowArtifacts) {
      super();
      this.worldXViewer = worldXViewer;
      this.selectedTeamWorkflowArtifacts = selectedTeamWorkflowArtifacts;
      setText("Edit Priority");
      setToolTipText(getText());
   }

   @Override
   public void run() {
      try {
         if (PriorityColumn.promptChangePriority(selectedTeamWorkflowArtifacts.getSelectedTeamWorkflowArtifacts(), true)) {
            worldXViewer.update(worldXViewer.getSelectedArtifactItems().toArray(), null);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

}
