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
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
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
   private static enum Columns {
      Date,
      Priority_123_Open_Bugs,
      Goal;
   };
   private final static List<String> chartDateStrs = Arrays.asList("09/07/2008", "09/21/2008", "10/05/2008",
      "10/19/2008", "11/02/2008", "11/16/2008", "11/30/2008", "12/14/2008", "12/28/2008", "01/11/2009", "01/25/2009",
      "02/08/2009", "02/22/2009", "03/08/2009", "03/22/2009", "04/05/2009", "04/19/2009");
   private final static List<Double> chartValueStrs = Arrays.asList(177.0, 174.0, 167.0, 161.0, 167.0, 167.0, 163.0,
      165.0, 171.0, 179.0, 178.0, 177.0, 164.0, 159.0, 159.0, 157.0, 157.0);
   private final static List<Double> chartValueStrsGoal = Arrays.asList(177.0, 174.0, 167.0, 161.0, 167.0, 167.0, 163.0,
      165.0, 171.0, 179.0, 177.0, 175.0, 173.0, 171.0, 169.0, 167.0, 165.0);

   public ResultsEditorExample(XNavigateItem parent) {
      super(parent, TITLE, PluginUiImage.ADMIN);
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
      for (int x = 0; x < chartDateStrs.size(); x++) {
         rows.add(new ResultsXViewerRow(new String[] {
            chartDateStrs.get(x),
            String.valueOf(chartValueStrs.get(x)),
            String.valueOf(chartValueStrsGoal.get(x))}));
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
      for (int x = 0; x < chartDateStrs.size(); x++) {
         sb.append(AHTML.addRowMultiColumnTable(chartDateStrs.get(x), "" + chartValueStrs.get(x),
            "" + chartValueStrsGoal.get(x)));
      }
      sb.append(AHTML.endMultiColumnTable());
      return sb.toString();
   }

}
