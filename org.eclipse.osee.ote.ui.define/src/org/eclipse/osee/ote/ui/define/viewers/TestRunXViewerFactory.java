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

import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn.SortDataType;
import org.eclipse.nebula.widgets.xviewer.customize.CustomizeData;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetXViewerFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.XViewerArtifactNameColumn;
import org.eclipse.osee.ote.ui.define.views.TestRunView;
import org.eclipse.swt.SWT;

/**
 * @author Roberto E. Escobar
 */
public class TestRunXViewerFactory extends SkynetXViewerFactory {

   private static XViewerArtifactNameColumn nameCol = new XViewerArtifactNameColumn("Name");
   public static final XViewerColumn DISPOSITION =
         new XViewerColumn("ote.test.run.view.Disposition", "Disposition", 300, SWT.LEFT, true, SortDataType.String,
               true, null);

   public TestRunXViewerFactory() {
      super(TestRunView.VIEW_ID);
      registerAllAttributeColumns();
      registerColumn(new XViewerColumn("ote.test.run.view.Name", "Name", 150, SWT.LEFT, true, SortDataType.String,
            false, null));
      registerColumn(new XViewerColumn("ote.test.run.view.Passed", "Passed", 50, SWT.LEFT, true, SortDataType.Integer,
            false, null));
      registerColumn(new XViewerColumn("ote.test.run.view.Failed", "Failed", 50, SWT.LEFT, true, SortDataType.Integer,
            false, null));
      registerColumn(new XViewerColumn("ote.test.run.view.Total Test Points", "Total Test Points", 50, SWT.LEFT, false,
            SortDataType.Integer, false, null));
      registerColumn(new XViewerColumn("ote.test.run.view.Status", "Status", 150, SWT.LEFT, true, SortDataType.String,
            false, null));
      registerColumn(new XViewerColumn("ote.test.run.view.Script Aborted", "Script Aborted", 150, SWT.LEFT, false,
            SortDataType.Boolean, false, null));

      registerColumn(new XViewerColumn("ote.test.run.view.Ran In Batch Mode", "Ran In Batch Mode", 150, SWT.LEFT,
            false, SortDataType.Boolean, false, null));
      registerColumn(new XViewerColumn("ote.test.run.view.Is Batch Mode Allowed", "Is Batch Mode Allowed", 150,
            SWT.LEFT, false, SortDataType.Boolean, false, null));

      registerColumn(new XViewerColumn("ote.test.run.view.Start Date", "Start Date", 150, SWT.LEFT, false,
            SortDataType.Date, false, null));
      registerColumn(new XViewerColumn("ote.test.run.view.End Date", "End Date", 150, SWT.LEFT, false,
            SortDataType.Date, false, null));
      registerColumn(new XViewerColumn("ote.test.run.view.Elapsed Date", "Elapsed Date", 150, SWT.LEFT, false,
            SortDataType.String, false, null));

      registerColumn(new XViewerColumn("ote.test.run.view.Processor ID", "Processor ID", 150, SWT.LEFT, false,
            SortDataType.String, false, null));
      registerColumn(new XViewerColumn("ote.test.run.view.Build Id", "Build Id", 150, SWT.LEFT, false,
            SortDataType.String, false, null));
      registerColumn(new XViewerColumn("ote.test.run.view.Qualification Level", "Qualification Level", 150, SWT.LEFT,
            true, SortDataType.String, false, null));
      registerColumn(new XViewerColumn("ote.test.run.view.Last Author", "Last Author", 150, SWT.LEFT, false,
            SortDataType.String, false, null));
      registerColumn(new XViewerColumn("ote.test.run.view.Last Modified Date", "Last Modified Date", 150, SWT.LEFT,
            false, SortDataType.Date, false, null));
      registerColumn(new XViewerColumn("ote.test.run.view.Revision", "Revision", 150, SWT.LEFT, false,
            SortDataType.String, false, null));

      registerColumn(new XViewerColumn("ote.test.run.view.OS Name", "OS Name", 150, SWT.LEFT, false,
            SortDataType.String, false, null));
      registerColumn(new XViewerColumn("ote.test.run.view.User ID", "User ID", 150, SWT.LEFT, false,
            SortDataType.String, false, null));
      registerColumn(DISPOSITION);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerFactory#getDefaultTableCustomizeData()
    */
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
