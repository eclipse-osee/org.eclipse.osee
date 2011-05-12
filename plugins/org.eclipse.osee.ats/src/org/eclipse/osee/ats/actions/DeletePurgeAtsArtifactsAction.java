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

import org.eclipse.jface.action.Action;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsDeleteManager;
import org.eclipse.osee.ats.util.AtsDeleteManager.DeleteOption;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class DeletePurgeAtsArtifactsAction extends Action {

   private final ISelectedAtsArtifacts selectedAtsArtifacts;

   public DeletePurgeAtsArtifactsAction(ISelectedAtsArtifacts selectedAtsArtifacts) {
      super("Delete/Purge Ats Artifact(s)", ImageManager.getImageDescriptor(FrameworkImage.ARTIFACT_EDITOR));
      this.selectedAtsArtifacts = selectedAtsArtifacts;
      setToolTipText(getText());
   }

   @Override
   public void run() {
      try {
         AtsDeleteManager.handleDeletePurgeAtsObject(selectedAtsArtifacts.getSelectedSMAArtifacts(), false,
            DeleteOption.Prompt);
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public void updateEnablement() {
      try {
         setEnabled(!selectedAtsArtifacts.getSelectedSMAArtifacts().isEmpty());
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         setEnabled(false);
      }
   }
}
