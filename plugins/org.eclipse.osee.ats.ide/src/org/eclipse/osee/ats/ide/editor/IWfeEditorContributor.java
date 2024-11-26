/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.ats.ide.editor;

import java.util.Collection;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public interface IWfeEditorContributor {

   default public void editorDisposing() {
      // do nothing
   }

   default public void addToolBarItems(IAtsWorkItem workItem, IToolBarManager toolBarMgr, WorkflowEditor editor) {
      // do nothing
   }

   default public void createStoryLink(Collection<IAtsTeamWorkflow> teamWfs, XResultData rd) {
      // do nothing
   }

}
