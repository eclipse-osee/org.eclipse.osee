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
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn.SortDataType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorProvider;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorTab;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.skynet.results.table.IResultsXViewerRow;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsEditorTableTab;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsXViewerRow;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;

/**
 * @author John Misinco
 */
public class TraceabilityResultsEditor extends AbstractOperation {

   private final List<Artifact> requirementArtifacts;

   public TraceabilityResultsEditor(String operationName, String pluginId, List<Artifact> requirementArtifacts) {
      super(operationName, pluginId);
      this.requirementArtifacts = requirementArtifacts;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      final HashCollection<Artifact, Artifact> traceArtifacts = new HashCollection<Artifact, Artifact>();
      for (Artifact req : requirementArtifacts) {
         if (req.isOfType(CoreArtifactTypes.Requirement)) {
            traceArtifacts.put(req, req.getRelatedArtifacts(CoreRelationTypes.Verification__Verifier));
         }
      }
      List<XViewerColumn> artColumns =
         Arrays.asList(new XViewerColumn("Requirement", "Requirement", 200, SWT.LEFT, true, SortDataType.String, false,
            "Requirement Artifact"), new XViewerColumn("TestScript", "TestScript", 200, SWT.LEFT, true,
            SortDataType.String, false, "TestScript Name"));
      List<IResultsXViewerRow> artRows = new LinkedList<IResultsXViewerRow>();
      for (Artifact key : traceArtifacts.keySet()) {
         for (Artifact script : traceArtifacts.getValues(key)) {
            artRows.add(new ResultsXViewerRow(new String[] {key.getName(), script.getName()}, script));
         }
      }
      final List<IResultsEditorTab> toReturn = new LinkedList<IResultsEditorTab>();
      toReturn.add(new ResultsEditorTableTab("Traceability", artColumns, artRows));

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
