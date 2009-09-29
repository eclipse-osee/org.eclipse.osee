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
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.operation.DuplicateWorkflowBlam;
import org.eclipse.osee.ats.world.WorldEditor;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.blam.BlamEditor;

/**
 * @author Donald G. Dunne
 */
public class DuplicateWorkflowViaWorldEditorAction extends Action {

   private final WorldEditor worldEditor;

   public DuplicateWorkflowViaWorldEditorAction(WorldEditor worldEditor) {
      this.worldEditor = worldEditor;
      setText("Duplicate Team Workflow");
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.DUPLICATE);
   }

   @Override
   public void run() {
      if (worldEditor.getWorldComposite().getXViewer().getSelectedTeamWorkflowArtifacts().size() == 0) {
         AWorkbench.popup("ERROR", "Must select one or more team workflows to duplicate");
         return;
      }
      try {
         DuplicateWorkflowBlam blamOperation = new DuplicateWorkflowBlam();
         blamOperation.setDefaultTeamWorkflows(worldEditor.getWorldComposite().getXViewer().getSelectedTeamWorkflowArtifacts());
         BlamEditor.edit(blamOperation);
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

}
