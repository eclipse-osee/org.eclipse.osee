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
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;

/**
 * @author Donald G. Dunne
 */
public class OpenInArtifactEditorOperation extends WorkPageService {

   public OpenInArtifactEditorOperation(SMAManager smaMgr) {
      super(smaMgr);
   }

   private void performOpen() {
      ArtifactEditor.editArtifact(smaMgr.getSma());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.service.WorkPageService#createToolbarService()
    */
   @Override
   public Action createToolbarService() {
      if (!AtsPlugin.isAtsAdmin()) return null;
      Action action = new Action(getName(), Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            performOpen();
         }
      };
      action.setToolTipText(getName());
      action.setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.ARTIFACT_EDITOR));
      return action;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.service.WorkPageService#getName()
    */
   @Override
   public String getName() {
      return "Open Artifact Editor";
   }

}
