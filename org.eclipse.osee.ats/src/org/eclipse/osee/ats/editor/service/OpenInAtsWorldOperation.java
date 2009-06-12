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
package org.eclipse.osee.ats.editor.service;

import java.util.Arrays;
import org.eclipse.jface.action.Action;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.world.WorldEditor;
import org.eclipse.osee.ats.world.WorldEditorSimpleProvider;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class OpenInAtsWorldOperation extends WorkPageService {

   public OpenInAtsWorldOperation(SMAManager smaMgr) {
      super(smaMgr);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.service.IAtsEditorToolBarService#run()
    */
   public void performOpen() {
      try {
         if (smaMgr.getSma() instanceof TeamWorkFlowArtifact) {
            ActionArtifact actionArt = ((TeamWorkFlowArtifact) smaMgr.getSma()).getParentActionArtifact();
            WorldEditor.open(new WorldEditorSimpleProvider("Action " + actionArt.getHumanReadableId(),
                  Arrays.asList(actionArt)));
            return;
         } else if (smaMgr.getSma() instanceof StateMachineArtifact) {
            WorldEditor.open(new WorldEditorSimpleProvider(
                  smaMgr.getSma().getArtifactTypeName() + ": " + smaMgr.getSma().getHumanReadableId(),
                  Arrays.asList(smaMgr.getSma())));
            return;
         }
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP,
               "Unhandled artifact type " + smaMgr.getSma().getArtifactTypeName());
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.toolbar.IAtsEditorToolBarService#createToolbarService(org.eclipse.osee.ats.editor.SMAManager)
    */
   @Override
   public Action createToolbarService() {
      Action action = new Action(getName(), Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            performOpen();
         }
      };
      action.setToolTipText(getName());
      action.setImageDescriptor(ImageManager.getImageDescriptor(AtsImage.GLOBE));
      return action;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.service.WorkPageService#getName()
    */
   @Override
   public String getName() {
      return "Open in ATS World Editor";
   }

}
