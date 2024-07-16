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

package org.eclipse.osee.framework.ui.skynet.artifact.massEditor;

import java.util.Collection;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.OseeTreeReportAdapter;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetXViewerFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.ArtifactNameColumnUI;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.ArtifactTokenColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.ArtifactTypeColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.HierarchyIndexColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.IdColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.LastModifiedByColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.LastModifiedDateColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.LastModifiedTransactionColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.LastModifiedTransactionCommentColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.UserGroupsColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.ViewApplicabilityColumn;

/**
 * @author Donald G. Dunne
 */
public class MassXViewerFactory extends SkynetXViewerFactory {

   private static String NAMESPACE = "ArtifactXViewer";
   private static ArtifactNameColumnUI nameCol = new ArtifactNameColumnUI(true, true);

   public MassXViewerFactory(Collection<? extends Artifact> artifacts) {
      super(NAMESPACE, new OseeTreeReportAdapter("Table Report - Mass Editor"));
      registerColumns(nameCol);
      registerColumns(ArtifactTypeColumn.getInstance());
      registerColumns(HierarchyIndexColumn.getInstance());
      registerColumns(new IdColumn(true));
      registerColumns(new ViewApplicabilityColumn(true));
      registerColumns(new LastModifiedDateColumn(true));
      registerColumns(new LastModifiedByColumn(true));
      registerColumns(new LastModifiedTransactionColumn(true));
      registerColumns(new LastModifiedTransactionCommentColumn(true));
      registerColumns(new ArtifactTokenColumn());
      registerColumns(new UserGroupsColumn(false));
      registerAllAttributeColumnsForArtifacts(artifacts, true, true);
   }

   @Override
   public CustomizeData getDefaultTableCustomizeData() {
      CustomizeData custData = new CustomizeData();
      custData.getSortingData().setSortingNames(nameCol.getId());
      custData.getColumnData().setColumns(getColumns());
      custData.setNameSpace(getNamespace());
      custData.setName("Artifacts");
      return custData;
   }

}
