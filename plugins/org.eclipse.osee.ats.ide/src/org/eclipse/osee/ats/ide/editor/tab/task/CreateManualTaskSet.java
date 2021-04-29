/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.ats.ide.editor.tab.task;

import org.eclipse.osee.ats.api.task.create.CreateTasksDefinitionBuilder;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.core.task.CreateTasksRuleRunner;
import org.eclipse.osee.ats.ide.actions.AbstractAtsAction;
import org.eclipse.osee.ats.ide.actions.ImportListener;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class CreateManualTaskSet extends AbstractAtsAction {

   private final TeamWorkFlowArtifact teamWf;
   private final ImportListener listener;
   private final CreateTasksDefinitionBuilder taskSet;

   public CreateManualTaskSet(String text, TeamWorkFlowArtifact teamWf, CreateTasksDefinitionBuilder taskSet, ImportListener listener) {
      super();
      this.teamWf = teamWf;
      this.taskSet = taskSet;
      this.listener = listener;
      setText(text);
      setImageDescriptor(ImageManager.getImageDescriptor(AtsImage.TASK));
   }

   @Override
   public void runWithException() {
      CreateTasksRuleRunner taskRunner =
         new CreateTasksRuleRunner(teamWf, taskSet.getCreateTasksDef(), AtsApiService.get());
      XResultData result = taskRunner.run();
      if (result.getIds().isEmpty()) {
         result.log("No new tasks created");
      }
      XResultDataUI.report(result, String.format("Create Tasks from Task Set [%s]", getText()));
      if (listener != null) {
         listener.importCompleted(result);
      }
   }
}
