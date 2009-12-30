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

import java.util.logging.Level;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;

/**
 * @author Donald G. Dunne
 */
public class OpenVersionArtifactAction extends Action {

   Action action;
   private final StateMachineArtifact sma;

   public OpenVersionArtifactAction(StateMachineArtifact sma) {
      this.sma = sma;
      setText("Open Targeted for Version");
      setToolTipText(getText());
   }

   private void performOpen() {
      try {
         if (sma.getWorldViewTargetedVersion() != null) {
            ArtifactEditor.editArtifact(sma.getWorldViewTargetedVersion());
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   @Override
   public void run() {
      performOpen();
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.VERSION);
   }

}
