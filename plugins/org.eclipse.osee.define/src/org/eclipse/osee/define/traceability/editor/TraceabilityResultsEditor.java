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
package org.eclipse.osee.define.traceability.editor;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.nebula.widgets.xviewer.XViewerLabelProvider;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
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
 * @author John R. Misinco
 */
public class TraceabilityResultsEditor extends AbstractOperation {

   private final List<Artifact> requirementArtifacts;

   public TraceabilityResultsEditor(String operationName, String pluginId, List<Artifact> requirementArtifacts) {
      super(operationName, pluginId);
      this.requirementArtifacts = requirementArtifacts;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      List<IResultsXViewerRow> artRows = new LinkedList<>();
      for (Artifact req : requirementArtifacts) {
         if (req.isOfType(CoreArtifactTypes.AbstractSoftwareRequirement)) {
            artRows.add(new ResultsXViewerRow(new String[] {req.getName(), ""}, req));
         }
      }
      List<XViewerColumn> artColumns = Arrays.asList(
         new XViewerColumn("Requirement", "Requirement", 500, XViewerAlign.Left, true, SortDataType.String, false,
            "Requirement Artifact"),
         new XViewerColumn("Relation", "Relation", 75, XViewerAlign.Left, true, SortDataType.String, false,
            "Relation Type"));

      final List<IResultsEditorTab> toReturn = new LinkedList<>();
      IResultsEditorLabelProvider provider = new IResultsEditorLabelProvider() {

         @Override
         public XViewerLabelProvider getLabelProvider(ResultsXViewer xViewer) {
            return new TraceabilityLabelProvider(xViewer);
         }

      };
      toReturn.add(
         new ResultsEditorTableTab("Traceability", artColumns, artRows, new TraceabilityContentProvider(), provider));

      Displays.ensureInDisplayThread(new Runnable() {

         @Override
         public void run() {
            ResultsEditor.open(new IResultsEditorProvider() {

               @Override
               public String getEditorName() {
                  return "Traceability Results Editor";
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
