/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.workflow.duplicate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.workflow.util.DuplicateWorkflowAsIsOperation;
import org.eclipse.osee.ats.core.workflow.util.IDuplicateWorkflowListener;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.util.widgets.XAssigneesHyperlinkWidget;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class CloneWorkflowAction extends Action {

   private final IAtsTeamWorkflow teamWf;
   private final IDuplicateWorkflowListener duplicateListener;
   private CloneDialog dialog;

   public CloneWorkflowAction(IAtsTeamWorkflow teamWf, IDuplicateWorkflowListener duplicateListener) {
      super("Clone Workflow");
      this.teamWf = teamWf;
      this.duplicateListener = duplicateListener;
   }

   @Override
   public void run() {
      String title = teamWf.getName() + " (cloned)";

      dialog = new CloneDialog(getText(), "Enter details for new cloned Team Workflow");
      dialog.setXTextString("title", title);
      dialog.setXTextString("desc", teamWf.getDescription());

      if (dialog.open() == Window.OK) {
         List<IDuplicateWorkflowListener> listeners = new ArrayList<IDuplicateWorkflowListener>();
         if (duplicateListener != null) {
            listeners.add(duplicateListener);
         }
         boolean newAction = dialog.getXCheckBoxChecked("newAction");
         String newTitle = dialog.getXtextString("title");
         DuplicateWorkflowAsIsOperation op = new DuplicateWorkflowAsIsOperation(Arrays.asList(teamWf), false, newTitle,
            AtsClientService.get().getUserService().getCurrentUser(), AtsClientService.get(),
            "Clone from " + teamWf.toStringWithId(), newAction, listeners);

         AtsUser orig = null;
         XAssigneesHyperlinkWidget origWidget = (XAssigneesHyperlinkWidget) dialog.getXWidget("orig");
         Collection<AtsUser> selected = origWidget.getSelected();
         if (selected.size() > 0) {
            orig = selected.iterator().next();
            op.setOriginator(orig);
         }
         String desc = dialog.getXtextString("desc");
         if (Strings.isInValid(desc)) {
            desc = teamWf.getDescription();
         }
         op.setDescription(desc);

         XResultData results = op.run();
         if (!results.isErrors()) {
            for (IAtsTeamWorkflow newTeamArt : op.getResults().values()) {
               WorkflowEditor.edit(newTeamArt);
            }
         } else {
            XResultDataUI.report(results, "Clone Workflow");
         }
      }
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.DUPLICATE);
   }

}
