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
package org.eclipse.osee.ats.operation;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map.Entry;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.util.widgets.SMAState;
import org.eclipse.osee.ats.util.widgets.XCurrentStateDam;
import org.eclipse.osee.ats.util.widgets.XStateDam;
import org.eclipse.osee.framework.jdk.core.type.CountingMap;
import org.eclipse.osee.framework.jdk.core.type.MutableInteger;
import org.eclipse.osee.framework.jdk.core.util.io.CharBackedInputStream;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ISheetWriter;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactTypeSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.FromArtifactsSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive;
import org.eclipse.osee.framework.skynet.core.artifact.search.InRelationSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.Operator;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;
import org.eclipse.osee.framework.ui.plugin.util.AIFile;
import org.eclipse.osee.framework.ui.plugin.util.OseeData;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;
import org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam;
import org.eclipse.swt.program.Program;

/**
 * @author Ryan D. Brooks
 */
public class TaskMetrics extends AbstractBlam {
   private static final ArtifactPersistenceManager artifactManager = ArtifactPersistenceManager.getInstance();
   private static final BranchPersistenceManager branchManager = BranchPersistenceManager.getInstance();
   private final CountingMap<User> metrics;
   private final CharBackedInputStream charBak;
   private final ISheetWriter excelWriter;

   public TaskMetrics() throws IOException {
      metrics = new CountingMap<User>();
      charBak = new CharBackedInputStream();
      excelWriter = new ExcelXmlWriter(charBak.getWriter());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap, org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.core.runtime.IProgressMonitor)
    */
   public void runOperation(BlamVariableMap variableMap, IProgressMonitor monitor) throws Exception {
      monitor.beginTask("TaskMetrics", 5);
      metrics.clear();

      ArtifactSubtypeDescriptor descriptor = variableMap.getArtifactSubtypeDescriptor("Artifact Type");

      FromArtifactsSearch teamWorkflowSearch =
            new FromArtifactsSearch(new ArtifactTypeSearch(descriptor.getName(), Operator.EQUAL));
      LinkedList<ISearchPrimitive> relatedCriteria = new LinkedList<ISearchPrimitive>();
      relatedCriteria.add(new InRelationSearch(teamWorkflowSearch, RelationSide.SmaToTask_Task));

      Collection<Artifact> artifacts =
            artifactManager.getArtifacts(relatedCriteria, true, branchManager.getAtsBranch());
      for (Artifact artifact : artifacts) {
         tallyState((TaskArtifact) artifact);
      }

      writeSummary();

      excelWriter.endWorkbook();
      IFile iFile = OseeData.getIFile("Task_Metrics.xml");
      AIFile.writeToFile(iFile, charBak);
      Program.launch(iFile.getLocation().toOSString());
   }

   private void tallyState(TaskArtifact task) throws SQLException {
      XStateDam stateDam = new XStateDam(task);

      SMAState state = stateDam.getState(TaskArtifact.INWORK_STATE, false);
      if (state == null) {
         XCurrentStateDam currentStateDam = new XCurrentStateDam(task);
         state = currentStateDam.getState(TaskArtifact.INWORK_STATE, false);
      }

      for (User user : state.getAssignees()) {
         int percentComplete = state.getPercentComplete();

         if (percentComplete == 100) {
            String resolution = task.getSoleAttributeValue(ATSAttributes.RESOLUTION_ATTRIBUTE.getStoreName());

            if (resolution.equals("Complete")) {
               metrics.put(user, 100);
            } else {
               metrics.put(user, 5);
            }
         } else {
            metrics.put(user, percentComplete);
         }
      }
   }

   private void writeSummary() throws IOException {
      excelWriter.startSheet("task metrics", 6);
      excelWriter.writeRow("Engineer", "TaskMetric");

      for (Entry<User, MutableInteger> entry : metrics.getCounts()) {
         User user = entry.getKey();
         MutableInteger metric = entry.getValue();
         excelWriter.writeRow(user.getName(), metric.toString());
      }
   }

   /*
    * (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getXWidgetXml()
    */
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XArtifactTypeListViewer\" displayName=\"Artifact Type\" keyedBranch=\"common\" defaultValue=\"Lba B3 Test Team Workflow\" /></xWidgets>";
   }
}