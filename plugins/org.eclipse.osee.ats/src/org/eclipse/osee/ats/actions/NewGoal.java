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

import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.editor.WorkflowEditor;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.workflow.goal.GoalArtifact;
import org.eclipse.osee.ats.workflow.goal.GoalManager;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class NewGoal extends AbstractAtsAction {

   public String titleOverride = null;

   public NewGoal() {
      super("Create New Goal");
      setImageDescriptor(ImageManager.getImageDescriptor(AtsImage.GOAL_NEW));
      setToolTipText("Create New Goal");
   }

   @Override
   public void runWithException() {
      String title = titleOverride;
      if (!Strings.isValid(title)) {
         EntryDialog dialog = new EntryDialog("New Goal", "Enter Title");
         if (dialog.open() == 0) {
            title = dialog.getEntry();
         }
      }
      if (Strings.isValid(title)) {
         IAtsChangeSet changes = AtsClientService.get().createChangeSet(getClass().getSimpleName());
         GoalArtifact goalArt = GoalManager.createGoal(title, changes);
         changes.execute();
         WorkflowEditor.editArtifact(goalArt);
      }
   }

   public void setTitleOverride(String titleOverride) {
      this.titleOverride = titleOverride;
   }

}