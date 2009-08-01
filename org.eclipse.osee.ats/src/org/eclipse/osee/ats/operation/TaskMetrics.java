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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact.TaskStates;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.widgets.SMAState;
import org.eclipse.osee.ats.util.widgets.XCurrentStateDam;
import org.eclipse.osee.ats.util.widgets.XStateDam;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.CountingMap;
import org.eclipse.osee.framework.jdk.core.type.MutableInteger;
import org.eclipse.osee.framework.jdk.core.util.io.CharBackedInputStream;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ISheetWriter;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.utility.AIFile;
import org.eclipse.osee.framework.skynet.core.utility.OseeData;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.swt.program.Program;

/**
 * @author Ryan D. Brooks
 */
public class TaskMetrics extends AbstractBlam {
   private final CountingMap<User> metrics;
   private final CharBackedInputStream charBak;
   private final ISheetWriter excelWriter;

   public TaskMetrics() throws IOException {
      metrics = new CountingMap<User>();
      charBak = new CharBackedInputStream();
      excelWriter = new ExcelXmlWriter(charBak.getWriter());
   }

   @Override
   public String getName() {
      return "Task Metrics";
   }

   
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws OseeCoreException {
      try {
         monitor.beginTask("TaskMetrics", 5);
         metrics.clear();

         ArtifactType descriptor = variableMap.getArtifactType("Artifact Type");

         List<Artifact> artifacts = ArtifactQuery.getArtifactListFromType(descriptor.getName(), AtsUtil.getAtsBranch());
         Set<Artifact> tasks = RelationManager.getRelatedArtifacts(artifacts, 1, AtsRelation.SmaToTask_Task);
         for (Artifact artifact : tasks) {
            if (artifact instanceof TaskArtifact) {
               tallyState((TaskArtifact) artifact);
            }
         }

         writeSummary();

         excelWriter.endWorkbook();
         IFile iFile = OseeData.getIFile("Task_Metrics.xml");
         AIFile.writeToFile(iFile, charBak);
         Program.launch(iFile.getLocation().toOSString());
      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      }
   }

   private void tallyState(TaskArtifact task) throws OseeCoreException {
      XStateDam stateDam = new XStateDam(task);

      SMAState state = stateDam.getState(TaskStates.InWork.name(), false);
      if (state == null) {
         XCurrentStateDam currentStateDam = new XCurrentStateDam(task);
         state = currentStateDam.getState(TaskStates.InWork.name(), false);
      }

      for (User user : state.getAssignees()) {
         int percentComplete = state.getPercentComplete();

         if (percentComplete == 100) {
            String resolution = task.getSoleAttributeValue(ATSAttributes.RESOLUTION_ATTRIBUTE.getStoreName(), "");

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

   
   @Override
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XArtifactTypeListViewer\" displayName=\"Artifact Type\" keyedBranch=\"common\" defaultValue=\"Lba Test Team Workflow\" /></xWidgets>";
   }

   public Collection<String> getCategories() {
      return Arrays.asList("ATS.Report");
   }
}