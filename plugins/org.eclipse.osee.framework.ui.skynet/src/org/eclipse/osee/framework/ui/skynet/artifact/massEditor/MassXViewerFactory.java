/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.artifact.massEditor;

import java.util.Collection;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.OseeTreeReportAdapter;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetXViewerFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.ArtifactNameColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.ArtifactTokenColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.ArtifactTypeColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.IdColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.LastModifiedByColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.LastModifiedDateColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.LastModifiedTransactionColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.LastModifiedTransactionCommentColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.ViewApplicabilityColumn;

/**
 * @author Donald G. Dunne
 */
public class MassXViewerFactory extends SkynetXViewerFactory {

   private static String NAMESPACE = "org.eclipse.osee.framework.ui.skynet.massEditor.ArtifactXViewer";
   private static ArtifactNameColumn nameCol = new ArtifactNameColumn(true, true);

   public MassXViewerFactory(Collection<? extends Artifact> artifacts) {
      super(NAMESPACE, new OseeTreeReportAdapter("Table Report - Mass Editor"));
      registerColumns(nameCol);
      registerColumns(ArtifactTypeColumn.getInstance());
      registerColumns(new IdColumn(true));
      registerColumns(new ViewApplicabilityColumn(true));
      registerColumns(new LastModifiedDateColumn(true));
      registerColumns(new LastModifiedByColumn(true));
      registerColumns(new LastModifiedTransactionColumn(true));
      registerColumns(new LastModifiedTransactionCommentColumn(true));
      registerColumns(new ArtifactTokenColumn());
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
