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

import java.util.Collection;
import org.eclipse.jface.action.IAction;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.osee.ats.ide.editor.tab.workflow.util.WfePromptChangeStatus;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class EditStatusAction extends AbstractAtsAction {

   private final ISelectedAtsArtifacts selectedAtsArtifacts;
   private final XViewer xViewer;
   private final IDirtiableEditor editor;

   public EditStatusAction(ISelectedAtsArtifacts selectedAtsArtifacts, IDirtiableEditor editor, XViewer xViewer) {
      super("Edit Status", IAction.AS_PUSH_BUTTON);
      this.selectedAtsArtifacts = selectedAtsArtifacts;
      this.editor = editor;
      this.xViewer = xViewer;
      setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.EDIT));
   }

   @Override
   public void runWithException() {
      Collection<AbstractWorkflowArtifact> smaArts =
         Collections.castMatching(AbstractWorkflowArtifact.class, selectedAtsArtifacts.getSelectedWorkflowArtifacts());
      if (WfePromptChangeStatus.promptChangeStatus(smaArts, true)) {
         if (xViewer != null) {
            xViewer.update(selectedAtsArtifacts.getSelectedWorkflowArtifacts().toArray(), null);
         }
         if (editor != null) {
            editor.onDirtied();
         }
      }

   }

}
