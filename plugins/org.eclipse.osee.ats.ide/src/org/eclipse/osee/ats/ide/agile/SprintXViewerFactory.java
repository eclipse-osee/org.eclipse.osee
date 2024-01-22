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

import java.util.List;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.column.AtsCoreColumnToken;
import org.eclipse.osee.ats.ide.workflow.sprint.SprintArtifact;
import org.eclipse.osee.ats.ide.world.WorldXViewerFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IOseeTreeReportProvider;

/**
 * @author Donald G. Dunne
 */
public class SprintXViewerFactory extends WorldXViewerFactory {

   private final SprintArtifact soleSprintArtifact;
   private BacklogXViewerFactory backlogFactory;
   private static String NAMESPACE = "SprintXViewer";

   public SprintXViewerFactory(SprintArtifact soleSprintArtifact, IOseeTreeReportProvider reportProvider) {
      super(NAMESPACE, reportProvider);
      this.soleSprintArtifact = soleSprintArtifact;
   }

   private BacklogXViewerFactory getBacklogFactory() {
      if (this.backlogFactory == null) {
         backlogFactory = new BacklogXViewerFactory(null, reportProvider);
      }
      return backlogFactory;
   }

   @Override
   public List<AtsCoreColumnToken> getDefaultVisibleColumns() {
      return getBacklogFactory().getDefaultVisibleColumns();
   }

   @Override
   public List<Integer> getDefaultColumnWidths() {
      return getBacklogFactory().getDefaultColumnWidths();
   }

   @Override
   public CustomizeData getDefaultTableCustomizeData() {
      CustomizeData customizeData = super.getDefaultTableCustomizeData();
      for (XViewerColumn xCol : customizeData.getColumnData().getColumns()) {
         if (xCol.getId().equals(SprintOrderColumnUI.getInstance().getId())) {
            xCol.setSortForward(true);
         }
      }
      customizeData.getSortingData().setSortingNames(SprintOrderColumnUI.getInstance().getId());
      return customizeData;
   }

   public SprintArtifact getSoleSprintArtifact() {
      return soleSprintArtifact;
   }

}
