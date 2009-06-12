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

import org.eclipse.jface.action.Action;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.util.AtsLib;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.ats.AtsOpenOption;

/**
 * @author Donald G. Dunne
 */
public class OpenTeamDefinition extends WorkPageService {

   public OpenTeamDefinition(SMAManager smaMgr) {
      super(smaMgr);
   }

   private void performOpen() {
      try {
         AtsLib.openAtsAction(((TeamWorkFlowArtifact) smaMgr.getSma()).getTeamDefinition(),
               AtsOpenOption.OpenOneOrPopupSelect);
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.service.WorkPageService#createToolbarService()
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
      action.setImageDescriptor(ImageManager.getImageDescriptor(AtsImage.TEAM_DEFINITION));
      return action;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.service.WorkPageService#getName()
    */
   @Override
   public String getName() {
      return "Open Team Definition";
   }

}
