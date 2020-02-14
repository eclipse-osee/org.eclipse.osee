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
package org.eclipse.osee.ote.ui.define.viewers;

import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.OseeTreeReportAdapter;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetXViewerFactory;
import org.eclipse.osee.ote.ui.define.views.TestRunView;

/**
 * @author Roberto E. Escobar
 */
public class TestRunXViewerFactory extends SkynetXViewerFactory {

   private static String NAMESPACE = "Disposition";

   public static final XViewerColumn DISPOSITION =
      new XViewerColumn(NAMESPACE, "Disposition", 300, XViewerAlign.Left, true, SortDataType.String, true, null);

   public TestRunXViewerFactory() {
      super(TestRunView.VIEW_ID, new OseeTreeReportAdapter("Test Run View"));
      registerAllAttributeColumns();
      registerColumns(new XViewerColumn("ote.test.run.view.Name", "Name", 150, XViewerAlign.Left, true,
         SortDataType.String, false, null));
      registerColumns(new XViewerColumn("ote.test.run.view.Passed", "Passed", 50, XViewerAlign.Left, true,
         SortDataType.Integer, false, null));
      registerColumns(new XViewerColumn("ote.test.run.view.Failed", "Failed", 50, XViewerAlign.Left, true,
         SortDataType.Integer, false, null));
      registerColumns(new XViewerColumn("ote.test.run.view.Total Test Points", "Total Test Points", 50,
         XViewerAlign.Left, false, SortDataType.Integer, false, null));
      registerColumns(new XViewerColumn("ote.test.run.view.Status", "Status", 150, XViewerAlign.Left, true,
         SortDataType.String, false, null));
      registerColumns(new XViewerColumn("ote.test.run.view.Script Aborted", "Script Aborted", 150, XViewerAlign.Left,
         false, SortDataType.Boolean, false, null));

      registerColumns(new XViewerColumn("ote.test.run.view.Ran In Batch Mode", "Ran In Batch Mode", 150,
         XViewerAlign.Left, false, SortDataType.Boolean, false, null));
      registerColumns(new XViewerColumn("ote.test.run.view.Is Batch Mode Allowed", "Is Batch Mode Allowed", 150,
         XViewerAlign.Left, false, SortDataType.Boolean, false, null));

      registerColumns(new XViewerColumn("ote.test.run.view.Start Date", "Start Date", 150, XViewerAlign.Left, false,
         SortDataType.Date, false, null));
      registerColumns(new XViewerColumn("ote.test.run.view.End Date", "End Date", 150, XViewerAlign.Left, false,
         SortDataType.Date, false, null));
      registerColumns(new XViewerColumn("ote.test.run.view.Elapsed Date", "Elapsed Date", 150, XViewerAlign.Left, false,
         SortDataType.String, false, null));

      registerColumns(new XViewerColumn("ote.test.run.view.Processor ID", "Processor ID", 150, XViewerAlign.Left, false,
         SortDataType.String, false, null));
      registerColumns(new XViewerColumn("ote.test.run.view.Build Id", "Build Id", 150, XViewerAlign.Left, false,
         SortDataType.String, false, null));
      registerColumns(new XViewerColumn("ote.test.run.view.Qualification Level", "Qualification Level", 150,
         XViewerAlign.Left, true, SortDataType.String, false, null));
      registerColumns(new XViewerColumn("ote.test.run.view.Last Author", "Last Author", 150, XViewerAlign.Left, false,
         SortDataType.String, false, null));
      registerColumns(new XViewerColumn("ote.test.run.view.Last Modified Date", "Last Modified Date", 150,
         XViewerAlign.Left, false, SortDataType.Date, false, null));
      registerColumns(new XViewerColumn("ote.test.run.view.Revision", "Revision", 150, XViewerAlign.Left, false,
         SortDataType.String, false, null));

      registerColumns(new XViewerColumn("ote.test.run.view.OS Name", "OS Name", 150, XViewerAlign.Left, false,
         SortDataType.String, false, null));
      registerColumns(new XViewerColumn("ote.test.run.view.User Id", "User Id", 150, XViewerAlign.Left, false,
         SortDataType.String, false, null));
      registerColumns(DISPOSITION);
   }

   @Override
   public CustomizeData getDefaultTableCustomizeData() {
      CustomizeData custData = new CustomizeData();
      custData.getSortingData().setSortingNames("ote.test.run.view.Name");
      custData.getColumnData().setColumns(getColumns());
      custData.setNameSpace(getNamespace());
      custData.setName("Artifacts");
      return custData;
   }

}
