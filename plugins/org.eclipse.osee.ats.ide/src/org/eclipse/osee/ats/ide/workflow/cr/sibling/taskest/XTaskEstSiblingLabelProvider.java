/*******************************************************************************
 * Copyright (c) 2021 Boeing.
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
package org.eclipse.osee.ats.ide.workflow.cr.sibling.taskest;

import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.column.AtsColumnTokens;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.cr.TaskEstUtil;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.world.WorldLabelProvider;
import org.eclipse.osee.ats.ide.world.WorldXViewer;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class XTaskEstSiblingLabelProvider extends WorldLabelProvider {

   private final AtsApi atsApi;

   public XTaskEstSiblingLabelProvider(WorldXViewer worldXViewer) {
      super(worldXViewer);
      atsApi = AtsApiService.get();
   }

   @Override
   public String getColumnText(Object element, int columnIndex) {
      XViewerColumn xViewerColumn = getTreeColumnOffIndex(columnIndex);
      if (element instanceof IAtsTeamWorkflow) {
         IAtsTeamWorkflow teamWf = (IAtsTeamWorkflow) element;
         if (xViewerColumn.getName().equals(XTaskEstSiblingXViewerFactory.Related_Task_Col.getName())) {
            IAtsTask relatedTask = TaskEstUtil.getTask(teamWf, atsApi);
            if (relatedTask != null) {
               return relatedTask.toStringWithId();
            } else {
               return "";
            }
         }
      }
      return super.getColumnText(element, columnIndex);
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn xCol, int columnIndex) {
      XViewerColumn xViewerColumn = getTreeColumnOffIndex(columnIndex);
      if (element instanceof IAtsTeamWorkflow) {
         IAtsTeamWorkflow teamWf = (IAtsTeamWorkflow) element;
         if (xViewerColumn.getName().equals(XTaskEstSiblingXViewerFactory.Related_Task_Col.getName())) {
            IAtsTask relatedTask = TaskEstUtil.getTask(teamWf, atsApi);
            if (relatedTask != null) {
               return ImageManager.getImage(AtsImage.TASK);
            } else {
               return null;
            }
         }
         if (xViewerColumn.getName().equals(AtsColumnTokens.TypeColumn.getId())) {
            return ImageManager.getImage(AtsImage.WORKFLOW);
         }
      }
      return null;
   }
}
