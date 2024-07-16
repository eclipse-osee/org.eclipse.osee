/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.actions;

import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.goal.GoalArtifact;
import org.eclipse.osee.ats.ide.workflow.goal.GoalManager;
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
         if (dialog.open() == Window.OK) {
            title = dialog.getEntry();
         }
      }
      if (Strings.isValid(title)) {
         IAtsChangeSet changes = AtsApiService.get().createChangeSet(getClass().getSimpleName());
         GoalArtifact goalArt = GoalManager.createGoal(title, changes);
         changes.execute();
         WorkflowEditor.editArtifact(goalArt);
      }
   }

   public void setTitleOverride(String titleOverride) {
      this.titleOverride = titleOverride;
   }

}