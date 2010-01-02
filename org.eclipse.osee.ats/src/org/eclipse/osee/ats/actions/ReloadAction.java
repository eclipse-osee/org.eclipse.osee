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

import java.util.HashSet;
import java.util.Set;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TaskableStateMachineArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.util.widgets.ReviewManager;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class ReloadAction extends Action {

   private final StateMachineArtifact sma;

   public ReloadAction(StateMachineArtifact sma) {
      String title = "Reload \"" + sma.getArtifactTypeName() + "\"";
      setText(title);
      setToolTipText(getText());
      this.sma = sma;
   }

   @Override
   public void run() {
      try {
         Set<Artifact> relatedArts = new HashSet<Artifact>();
         relatedArts.add(sma);
         if (sma.isTeamWorkflow()) {
            relatedArts.addAll(ReviewManager.getReviews((TeamWorkFlowArtifact) sma));
         }
         if (sma instanceof TaskableStateMachineArtifact) {
            relatedArts.addAll(((TaskableStateMachineArtifact) sma).getTaskArtifacts());
         }
         if (!MessageDialog.openConfirm(
               Display.getCurrent().getActiveShell(),
               "Reload Action (Experimental)",
               "Experimental Only...Use at own risk!\n\nThis operation will remove artifacts from cache and reload.\nUnsaved changes will be lost.\n\nReload [" + relatedArts.size() + "] artifacts?")) {
            return;
         }
         ArtifactQuery.reloadArtifacts(relatedArts);
         // Don't need to re-open editor cause event handler will do that
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.RELOAD);
   }

}
