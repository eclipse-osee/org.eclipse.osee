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

package org.eclipse.osee.ats.ide.world;

import java.util.Arrays;
import java.util.List;
import org.eclipse.jface.action.Action;
import org.eclipse.osee.ats.ide.export.AtsExportAction;
import org.eclipse.osee.ats.ide.search.AtsSearchWorkflowSearchItem;
import org.eclipse.osee.ats.ide.workflow.duplicate.DuplicateWorkflowViaWorldEditorAction;
import org.eclipse.osee.ats.ide.world.search.AtsSearchGoalSearchItem;
import org.eclipse.osee.ats.ide.world.search.AtsSearchReviewSearchItem;
import org.eclipse.osee.ats.ide.world.search.AtsSearchTaskSearchItem;
import org.eclipse.osee.ats.ide.world.search.AtsSearchTeamWorkflowSearchItem;
import org.eclipse.osee.ats.ide.world.search.AtsSearchWorkPackageSearchItem;

/**
 * @author Donald G. Dunne
 */
public class AtsWorldEditorItem implements IAtsWorldEditorItem {

   @Override
   public List<? extends Action> getWorldEditorMenuActions(IWorldEditorProvider worldEditorProvider, WorldEditor worldEditor) {
      return Arrays.asList(new AtsExportAction(worldEditor.getWorldComposite().getWorldXViewer()),
         new DuplicateWorkflowViaWorldEditorAction(worldEditor.getWorldComposite().getWorldXViewer()));
   }

   @Override
   public List<AtsSearchWorkflowSearchItem> getSearchWorkflowSearchItems() {
      return Arrays.asList(new AtsSearchWorkflowSearchItem(), new AtsSearchTeamWorkflowSearchItem(),
         new AtsSearchTaskSearchItem(), new AtsSearchReviewSearchItem(), new AtsSearchGoalSearchItem(),
         new AtsSearchWorkPackageSearchItem(), new AtsSearchWorkPackageSearchItem());
   }

}
