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
package org.eclipse.osee.ats.operation;

import java.util.Collection;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact.DefaultTeamState;
import org.eclipse.osee.ats.editor.SMAManager.TransitionOption;
import org.eclipse.osee.ats.world.IAtsWorldEditorMenuItem;
import org.eclipse.osee.ats.world.WorldEditor;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;

/**
 * @author Donald G. Dunne
 */
public class CompleteMultipleWorkflows implements IAtsWorldEditorMenuItem {

   private final boolean enableEmail = false;

   public CompleteMultipleWorkflows() {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IAtsWorldEditorMenuItem#getMenuItemName()
    */
   @Override
   public String getMenuItemName() throws OseeCoreException {
      return "Complete Selected Workflows";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IAtsWorldEditorMenuItem#runMenuItem(org.eclipse.osee.ats.world.WorldEditor)
    */
   @Override
   public void runMenuItem(WorldEditor worldEditor) throws OseeCoreException {
      Collection<StateMachineArtifact> smas = worldEditor.getWorldComposite().getXViewer().getSelectedSMAArtifacts();
      if (smas.size() == 0) {
         AWorkbench.popup("ERROR", "Must select one or more workflows");
         return;
      }
      for (StateMachineArtifact sma : smas) {
         Result result =
               sma.getSmaMgr().isTransitionValid(DefaultTeamState.Completed.name(), null, TransitionOption.None);
         if (result.isFalse()) {
            result.popup();
            return;
         }
      }
      EntryDialog ed = new EntryDialog("Complete Workflows", "Enter Completion Reson");
      if (ed.open() == 0) {
         try {
            AtsPlugin.setEmailEnabled(enableEmail);
            SkynetTransaction transaction = new SkynetTransaction(AtsPlugin.getAtsBranch());
            for (StateMachineArtifact sma : smas) {
               Result result =
                     sma.getSmaMgr().transitionToCompleted(ed.getEntry(), transaction, TransitionOption.Persist);
               if (result.isFalse()) {
                  result.popup();
                  return;
               }
            }
            transaction.execute();
            AWorkbench.popup("Complete", "Workflows Completed");
         } finally {
            AtsPlugin.setEmailEnabled(true);
         }
      }
   }
}
