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
import java.util.List;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorProvider;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorTab;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.skynet.results.table.IResultsXViewerRow;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsEditorTableTab;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsXViewerRow;

/**
 * @author Donald G. Dunne
 */
public final class XViewerExample extends XNavigateItemAction {

   public static final String TITLE = "XViewer Example";
   private static enum Columns {
      Date,
      String1,
      String2;
   };

   public XViewerExample(XNavigateItem parent) {
      super(parent, TITLE, PluginUiImage.ADMIN);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      ResultsEditor.open(new IResultsEditorProvider() {

         @Override
         public String getEditorName() {
            return TITLE;
         }

         @Override
         public List<IResultsEditorTab> getResultsEditorTabs() {
            List<IResultsXViewerRow> rows = new ArrayList<>();
            List<IResultsXViewerRow> bigRows = new ArrayList<>();
            for (int x = 0; x < 50000; x++) {
               if (x < 15000) {
                  rows.add(new ResultsXViewerRow(new String[] {"Date " + x, "hello", "world"}));
               }
               bigRows.add(new ResultsXViewerRow(new String[] {"Date " + x, "hello", "world"}));
            }
            List<XViewerColumn> columns = Arrays.asList(
               new XViewerColumn(Columns.Date.name(), Columns.Date.name(), 80, XViewerAlign.Left, true,
                  SortDataType.String, false, ""),
               new XViewerColumn(Columns.String1.name(), Columns.String1.name(), 80, XViewerAlign.Left, true,
                  SortDataType.Integer, false, ""),
               new XViewerColumn(Columns.String2.name(), Columns.String2.name(), 80, XViewerAlign.Left, true,
                  SortDataType.Integer, false, ""));
            List<IResultsEditorTab> tabs = new ArrayList<>();
            tabs.add(new ResultsEditorTableTab("15,000 entries", columns, rows));
            tabs.add(new ResultsEditorTableTab("50,000 entries", columns, bigRows));
            return tabs;
         }

      });
   }

}
