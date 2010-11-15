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

import java.util.Collection;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.editor.SMAPromptChangeStatus;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class EditStatusAction extends Action {

   private final ISelectedAtsArtifacts selectedAtsArtifacts;
   private final XViewer xViewer;

   public EditStatusAction(ISelectedAtsArtifacts selectedAtsArtifacts, XViewer xViewer) {
      super("Edit Status", IAction.AS_PUSH_BUTTON);
      this.selectedAtsArtifacts = selectedAtsArtifacts;
      this.xViewer = xViewer;
      setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.EDIT));
   }

   @Override
   public void run() {
      try {
         Collection<AbstractWorkflowArtifact> smaArts =
            Collections.castMatching(AbstractWorkflowArtifact.class, selectedAtsArtifacts.getSelectedSMAArtifacts());
         if (SMAPromptChangeStatus.promptChangeStatus(smaArts, true)) {
            if (xViewer != null) {
               xViewer.update(selectedAtsArtifacts.getSelectedSMAArtifacts().toArray(), null);
            }
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

}
