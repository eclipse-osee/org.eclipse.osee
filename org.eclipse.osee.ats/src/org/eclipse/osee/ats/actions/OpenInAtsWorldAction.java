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
package org.eclipse.osee.ats.actions;

import java.util.Arrays;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.world.WorldEditor;
import org.eclipse.osee.ats.world.WorldEditorSimpleProvider;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class OpenInAtsWorldAction extends Action {

   private final SMAManager smaMgr;

   public OpenInAtsWorldAction(SMAManager smaMgr) {
      this.smaMgr = smaMgr;
      setText("Open in ATS World Editor");
   }

   public void performOpen() {
      try {
         if (smaMgr.getSma() instanceof TeamWorkFlowArtifact) {
            ActionArtifact actionArt = ((TeamWorkFlowArtifact) smaMgr.getSma()).getParentActionArtifact();
            WorldEditor.open(new WorldEditorSimpleProvider("Action " + actionArt.getHumanReadableId(),
                  Arrays.asList(actionArt)));
            return;
         } else {
            WorldEditor.open(new WorldEditorSimpleProvider(
                  smaMgr.getSma().getArtifactTypeName() + ": " + smaMgr.getSma().getHumanReadableId(),
                  Arrays.asList(smaMgr.getSma())));
            return;
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public void run() {
      performOpen();
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.action.Action#getImageDescriptor()
    */
   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(AtsImage.GLOBE);
   }

}
