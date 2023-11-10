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

package org.eclipse.osee.ats.ide.workflow.goal;

import java.util.Arrays;
import java.util.List;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.column.AtsColumnTokensDefault;
import org.eclipse.osee.ats.api.column.AtsCoreColumnToken;
import org.eclipse.osee.ats.ide.column.AbstractMembersOrderColumnUI;
import org.eclipse.osee.ats.ide.column.GoalOrderColumnUI;
import org.eclipse.osee.ats.ide.column.GoalOrderVoteColumnUI;
import org.eclipse.osee.ats.ide.world.WorldXViewerFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IOseeTreeReportProvider;

/**
 * @author Donald G. Dunne
 */
public class GoalXViewerFactory extends WorldXViewerFactory {

   private final GoalArtifact soleGoalArtifact;
   private final static String NAMESPACE = "GoalXViewer";

   public GoalXViewerFactory(GoalArtifact soleGoalArtifact, IOseeTreeReportProvider reportProvider) {
      super(NAMESPACE, reportProvider);
      this.soleGoalArtifact = soleGoalArtifact;
   }

   @Override
   public List<AtsCoreColumnToken> getDefaultVisibleColumns() {
      return Arrays.asList( //
         AtsColumnTokensDefault.GoalOrderColumn, //
         AtsColumnTokensDefault.TitleColumn, //
         AtsColumnTokensDefault.TypeColumn, //
         AtsColumnTokensDefault.StateColumn, //
         AtsColumnTokensDefault.PriorityColumn, //
         AtsColumnTokensDefault.ChangeTypeColumn, //
         AtsColumnTokensDefault.AssigneeColumn, //
         AtsColumnTokensDefault.AtsIdColumn, //
         AtsColumnTokensDefault.CreatedDateColumn, //
         AtsColumnTokensDefault.TargetedVersionColumn, //
         AtsColumnTokensDefault.NotesColumn //
      );
   }

   @Override
   public List<Integer> getDefaultColumnWidths() {
      return Arrays.asList(AbstractMembersOrderColumnUI.DEFAULT_WIDTH, GoalOrderVoteColumnUI.DEFAULT_WIDTH, 250, 60, 60, 20,
         20, 100, 50, 50, 50, 80);
   }

   @Override
   public CustomizeData getDefaultTableCustomizeData() {
      CustomizeData customizeData = super.getDefaultTableCustomizeData();
      for (XViewerColumn xCol : customizeData.getColumnData().getColumns()) {
         if (xCol.getId().equals(GoalOrderColumnUI.COLUMN_ID)) {
            xCol.setSortForward(true);
         }
      }
      customizeData.getSortingData().setSortingNames(GoalOrderColumnUI.COLUMN_ID);
      return customizeData;
   }

   public GoalArtifact getSoleGoalArtifact() {
      return soleGoalArtifact;
   }

}
