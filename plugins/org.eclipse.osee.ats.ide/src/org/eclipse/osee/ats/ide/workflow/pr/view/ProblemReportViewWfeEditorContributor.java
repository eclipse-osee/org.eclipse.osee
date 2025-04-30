/*******************************************************************************
 * Copyright (c) 2025 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.ats.ide.workflow.pr.view;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.ide.editor.IWfeEditorContributor;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;

/**
 * @author Donald G. Dunne
 */
public class ProblemReportViewWfeEditorContributor implements IWfeEditorContributor {

   public ProblemReportViewWfeEditorContributor() {
      // for osgi
   }

   @Override
   public void addToolBarItems(IAtsWorkItem workItem, IToolBarManager toolBarMgr, WorkflowEditor editor) {
      if (workItem.isOfType(AtsArtifactTypes.ProblemReportTeamWorkflow)) {
         toolBarMgr.add(new Separator());
         toolBarMgr.add(new ProblemReportView(workItem));
      }
   }

}
