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

package org.eclipse.osee.ats.ide.workflow.task;

import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.ats.api.column.AtsColumnTokensDefault;
import org.eclipse.osee.ats.api.column.AtsCoreColumnToken;
import org.eclipse.osee.ats.ide.world.WorldXViewerFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IOseeTreeReportProvider;

/**
 * @author Donald G. Dunne
 */
public class TaskXViewerFactory extends WorldXViewerFactory {

   public final static String NAMESPACE = "TaskXViewer";

   public TaskXViewerFactory(IOseeTreeReportProvider reportProvider) {
      super(NAMESPACE, reportProvider);
   }

   @Override
   public List<AtsCoreColumnToken> getDefaultVisibleColumns() {
      return Arrays.asList( //
         AtsColumnTokensDefault.TitleColumn, //
         AtsColumnTokensDefault.StateColumn, //
         AtsColumnTokensDefault.PriorityColumn, //
         AtsColumnTokensDefault.AssigneeColumn, //
         AtsColumnTokensDefault.PercentCompleteWorkflowColumn, //
         AtsColumnTokensDefault.HoursSpent, //
         AtsColumnTokensDefault.ResolutionColumn, //
         AtsColumnTokensDefault.EstimatedHoursColumn, //
         AtsColumnTokensDefault.RelatedToStateColumn, //
         AtsColumnTokensDefault.NotesColumn //
      );
   }

   // Return default visible column widths.  Empty list or missing will use default token width.
   @Override
   public List<Integer> getDefaultColumnWidths() {
      return Arrays.asList(450, 60, 150, 40, 40, 100, 50, 50, 50, 80);
   }

}
