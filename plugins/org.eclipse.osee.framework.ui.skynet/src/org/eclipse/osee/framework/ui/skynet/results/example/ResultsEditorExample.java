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

package org.eclipse.osee.framework.ui.skynet.results.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.result.table.ExampleTableData;
import org.eclipse.osee.framework.jdk.core.result.table.ExampleTableData.Columns;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorProvider;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorTab;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.skynet.results.html.ResultsEditorHtmlTab;
import org.eclipse.osee.framework.ui.skynet.results.table.IResultsXViewerRow;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsEditorTableTab;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsXViewerRow;

/**
 * @author Donald G. Dunne
 */
public class ResultsEditorExample extends XNavigateItemAction {

   public static final String TITLE = "Results Editor Example";

   public ResultsEditorExample() {
      super(TITLE, FrameworkImage.EXAMPLE, XNavigateItem.UTILITY_EXAMPLES);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      ResultsEditor.open(new IResultsEditorProvider() {

         private List<IResultsEditorTab> tabs;

         @Override
         public String getEditorName() {
            return TITLE;
         }

         @Override
         public List<IResultsEditorTab> getResultsEditorTabs() {
            if (tabs == null) {
               tabs = new LinkedList<>();
               tabs.add(createDataTab());
               tabs.add(createHtmlTab());
               tabs.add(createArtifactTab());
            }
            return tabs;
         }
      });
   }

   private IResultsEditorTab createDataTab() {
      List<IResultsXViewerRow> rows = new ArrayList<>();
      for (int x = 0; x < ExampleTableData.chartDateStrs.size(); x++) {
         rows.add(new ResultsXViewerRow(new String[] {
            ExampleTableData.chartDateStrs.get(x),
            String.valueOf(ExampleTableData.chartValueStrs.get(x)),
            String.valueOf(ExampleTableData.chartValueStrsGoal.get(x))}));
      }
      List<XViewerColumn> columns = Arrays.asList(
         new XViewerColumn(Columns.Date.name(), Columns.Date.name(), 80, XViewerAlign.Left, true, SortDataType.Date,
            false, ""),
         new XViewerColumn(Columns.Priority_123_Open_Bugs.name(), Columns.Priority_123_Open_Bugs.name(), 80,
            XViewerAlign.Left, true, SortDataType.Integer, false, ""),
         new XViewerColumn(Columns.Goal.name(), Columns.Goal.name(), 80, XViewerAlign.Left, true, SortDataType.Integer,
            false, ""));

      return new ResultsEditorTableTab("Data", columns, rows);

   }

   private IResultsEditorTab createHtmlTab() {
      return new ResultsEditorHtmlTab(TITLE, "Report", AHTML.simplePage(getHtmlReport()));
   }

   private IResultsEditorTab createArtifactTab() {
      List<XViewerColumn> artColumns = Arrays.asList(
         new XViewerColumn("Artifact", "Artifact", 200, XViewerAlign.Left, true, SortDataType.String, false,
            "Requirement Artifact"),
         new XViewerColumn("ID", "ID", 200, XViewerAlign.Left, true, SortDataType.String, false, "TestScript Name"));

      List<IResultsXViewerRow> artRows = new ArrayList<>();
      try {
         List<Artifact> userArts = ArtifactQuery.getArtifactListFromType(CoreArtifactTypes.User, CoreBranches.COMMON);
         for (int x = 0; x < (userArts.size() > 10 ? 10 : userArts.size()); x++) {
            Artifact artifact = userArts.get(x);
            artRows.add(new ResultsXViewerRow(new String[] {artifact.getName(), artifact.getIdString()}, artifact));
         }
      } catch (OseeCoreException ex) {
         // do nothing
      }

      return new ResultsEditorTableTab("Artifact", artColumns, artRows);

   }

   public String getHtmlReport() {

      StringBuilder sb = new StringBuilder();
      sb.append(AHTML.heading(3, TITLE));
      sb.append(AHTML.beginMultiColumnTable(100, 1));
      sb.append(AHTML.addHeaderRowMultiColumnTable(
         new String[] {Columns.Date.name(), Columns.Priority_123_Open_Bugs.name(), Columns.Goal.name()}));
      for (int x = 0; x < ExampleTableData.chartDateStrs.size(); x++) {
         sb.append(AHTML.addRowMultiColumnTable(ExampleTableData.chartDateStrs.get(x),
            "" + ExampleTableData.chartValueStrs.get(x), "" + ExampleTableData.chartValueStrsGoal.get(x)));
      }
      sb.append(AHTML.endMultiColumnTable());
      return sb.toString();
   }

}
