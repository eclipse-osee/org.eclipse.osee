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

package org.eclipse.osee.ats.ide.editor.tab.workflow.util;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.core.workflow.transition.TransitionStatusData;
import org.eclipse.osee.ats.ide.util.widgets.dialog.TransitionStatusDialog;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;

/**
 * @author Donald G. Dunne
 */
public class WfePromptChangeHoursSpent {

   private final Collection<? extends AbstractWorkflowArtifact> awas;

   public WfePromptChangeHoursSpent(AbstractWorkflowArtifact awa) {
      this(Arrays.asList(awa));
   }

   public WfePromptChangeHoursSpent(final Collection<? extends AbstractWorkflowArtifact> awas) {
      this.awas = awas;
   }

   public static boolean promptChangeStatus(Collection<? extends AbstractWorkflowArtifact> awas, boolean persist) {
      WfePromptChangeHoursSpent promptChangeStatus = new WfePromptChangeHoursSpent(awas);
      return promptChangeStatus.promptChangeStatus(persist).isTrue();
   }

   public Result promptChangeStatus(boolean persist) {
      Result result = WfePromptChangeStatus.isValidToChangeStatus(awas);
      if (result.isFalse()) {
         AWorkbench.popup(result);
         return result;
      }

      TransitionStatusData data = new TransitionStatusData(awas, false);
      TransitionStatusDialog dialog = new TransitionStatusDialog("Enter Hours Spent",
         "Enter percent complete and number of hours you spent since last status.", data);
      if (dialog.open() == Window.OK) {
         WfePromptChangeStatus.performChangeStatusAndPersist(awas, null, data.getAdditionalHours(), data.getPercent(),
            data.isSplitHoursBetweenItems());
         return Result.TrueResult;
      }
      return Result.FalseResult;
   }

}
