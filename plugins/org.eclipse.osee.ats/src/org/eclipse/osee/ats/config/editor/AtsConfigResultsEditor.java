/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.config.editor;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.nebula.widgets.xviewer.XViewerLabelProvider;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorProvider;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorTab;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.skynet.results.table.IResultsXViewerRow;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsEditorTableTab;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsEditorTableTab.IResultsEditorLabelProvider;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsXViewerRow;
import org.eclipse.osee.framework.ui.skynet.results.table.xresults.ResultsXViewer;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public class AtsConfigResultsEditor extends AbstractOperation {

   private final List<IAtsConfigObject> configObjects;

   public AtsConfigResultsEditor(String operationName, String pluginId, List<IAtsConfigObject> configObjects) {
      super(operationName, pluginId);
      this.configObjects = configObjects;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      List<IResultsXViewerRow> artRows = new LinkedList<>();
      for (IAtsConfigObject obj : configObjects) {
         artRows.add(new ResultsXViewerRow(new String[] {obj.getName(), ""}, obj));
      }
      List<XViewerColumn> artColumns = Arrays.asList(
         new XViewerColumn("Type", "Type", 175, XViewerAlign.Left, true, SortDataType.String, false, "Type"),
         new XViewerColumn("Name", "Name", 300, XViewerAlign.Left, true, SortDataType.String, false, "Name"),
         new XViewerColumn("Id", "Id", 150, XViewerAlign.Left, true, SortDataType.String, false, "Id"));

      final List<IResultsEditorTab> toReturn = new LinkedList<>();
      IResultsEditorLabelProvider provider = new IResultsEditorLabelProvider() {

         @Override
         public XViewerLabelProvider getLabelProvider(ResultsXViewer xViewer) {
            return new AtsConfigLabelProvider(xViewer);
         }

      };
      toReturn.add(new ResultsEditorTableTab("Config", artColumns, artRows, new AtsConfigContentProvider(), provider));

      Displays.ensureInDisplayThread(new Runnable() {

         @Override
         public void run() {
            ResultsEditor.open(new IResultsEditorProvider() {

               @Override
               public String getEditorName() {
                  return "ATS Config Viewer";
               }

               @Override
               public List<IResultsEditorTab> getResultsEditorTabs() {
                  return toReturn;
               }
            });
         }
      });
   }
}
