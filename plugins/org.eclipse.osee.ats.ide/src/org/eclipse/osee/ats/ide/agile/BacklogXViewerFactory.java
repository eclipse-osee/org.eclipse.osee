/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.ats.ide.agile;

import java.util.Arrays;
import java.util.List;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.column.AtsColumnTokensDefault;
import org.eclipse.osee.ats.api.column.AtsCoreColumnToken;
import org.eclipse.osee.ats.ide.column.AbstractMembersOrderColumnUI;
import org.eclipse.osee.ats.ide.column.BacklogOrderColumnUI;
import org.eclipse.osee.ats.ide.workflow.goal.GoalArtifact;
import org.eclipse.osee.ats.ide.world.WorldXViewerFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IOseeTreeReportProvider;

/**
 * @author Donald G. Dunne
 */
public class BacklogXViewerFactory extends WorldXViewerFactory {

   public final static String NAMESPACE = "BacklogXViewer";

   public BacklogXViewerFactory(GoalArtifact soleBacklogArtifact, IOseeTreeReportProvider reportProvider) {
      super(NAMESPACE, reportProvider);
   }

   @Override
   public List<AtsCoreColumnToken> getDefaultVisibleColumns() {
      return Arrays.asList( //
         AtsColumnTokensDefault.BacklogOrderColumn, //
         AtsColumnTokensDefault.TitleColumn, //
         AtsColumnTokensDefault.AgileTeamPointsColumn, //
         AtsColumnTokensDefault.StateColumn, //
         AtsColumnTokensDefault.PriorityColumn, //
         AtsColumnTokensDefault.ChangeTypeColumn, //
         AtsColumnTokensDefault.AssigneeColumn, //
         AtsColumnTokensDefault.TargetedVersionColumn, //
         AtsColumnTokensDefault.SprintColumn, //
         AtsColumnTokensDefault.UnPlannedWorkColumn, //
         AtsColumnTokensDefault.AgileFeatureGroupColumn, //
         AtsColumnTokensDefault.CreatedDateColumn, //
         AtsColumnTokensDefault.AtsIdColumn, //
         AtsColumnTokensDefault.NotesColumn //
      );
   }

   @Override
   public List<Integer> getDefaultColumnWidths() {
      return Arrays.asList(AbstractMembersOrderColumnUI.DEFAULT_WIDTH, 300,
         AtsColumnTokensDefault.BacklogOrderColumn.getWidth(), 116, 20, 20, 113, 50, 100, 20, 90, 80, 50, 200);
   }

   @Override
   public CustomizeData getDefaultTableCustomizeData() {
      CustomizeData customizeData = super.getDefaultTableCustomizeData();
      for (XViewerColumn xCol : customizeData.getColumnData().getColumns()) {
         if (xCol.getId().equals(BacklogOrderColumnUI.COLUMN_ID)) {
            xCol.setSortForward(true);
         }
      }
      customizeData.getSortingData().setSortingNames(BacklogOrderColumnUI.COLUMN_ID);
      return customizeData;
   }

}
