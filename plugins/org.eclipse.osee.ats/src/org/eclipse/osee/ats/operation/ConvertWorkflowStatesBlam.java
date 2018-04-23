/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.operation;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.workflow.ConvertWorkflowStatesOperation;
import org.eclipse.osee.ats.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.util.result.XResultData;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public class ConvertWorkflowStatesBlam extends AbstractBlam {

   private final static String SOURCE_TEAM_WORKFLOWS = "Team Workflow(s) (drop here)";
   private final static String FROM_TO_MAP = "From State to State mapping (from:to;from:to)";
   private final static String PERSIST = "Persist (else report only)";

   public ConvertWorkflowStatesBlam() {
      // do nothing
   }

   @Override
   public void runOperation(final VariableMap variableMap, IProgressMonitor monitor) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            try {
               List<TeamWorkFlowArtifact> sourceTeamArts = org.eclipse.osee.framework.jdk.core.util.Collections.castAll(
                  TeamWorkFlowArtifact.class, variableMap.getArtifacts(SOURCE_TEAM_WORKFLOWS));
               String fromToMapStr = variableMap.getString(FROM_TO_MAP);
               boolean persist = variableMap.getBoolean(PERSIST);

               if (sourceTeamArts.isEmpty()) {
                  AWorkbench.popup("ERROR", "Must drag in Source Team Workflow(s).");
                  return;
               }
               if (fromToMapStr.isEmpty()) {
                  AWorkbench.popup("ERROR", "Must enter from/to mappings.");
                  return;
               }
               Map<String, String> fromToMap = createFromToMap(fromToMapStr);
               if (fromToMap == null) {
                  return;
               }
               if (fromToMap.isEmpty()) {
                  AWorkbench.popup("ERROR", "No From/To values extracted.");
                  return;
               }
               final XResultData rd = new XResultData();
               ConvertWorkflowStatesOperation operation =
                  new ConvertWorkflowStatesOperation(fromToMap, sourceTeamArts, persist, rd);
               Operations.executeAsJob(operation, true, Job.LONG, new JobChangeAdapter() {

                  @Override
                  public void done(IJobChangeEvent event) {
                     XResultDataUI.report(rd, getName());
                  }

               });
            } catch (Exception ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }

      });
   }

   private Map<String, String> createFromToMap(String fromToMapStr) {
      Map<String, String> fromToMap = new HashMap<>();
      for (String line : fromToMapStr.split(";")) {
         String[] fromTo = line.split(":");
         if (fromTo.length != 2 || fromTo[0].isEmpty() || fromTo[1].isEmpty()) {
            AWorkbench.popup(String.format("Invalid from:to values [%s]", line));
            return null;
         }
         fromToMap.put(fromTo[0], fromTo[1]);
      }
      return fromToMap;
   };

   @Override
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XListDropViewer\" displayName=\"" + SOURCE_TEAM_WORKFLOWS + "\" />" +
      //
         "<XWidget xwidgetType=\"XText\" displayName=\"" + FROM_TO_MAP + "\" horizontalLabel=\"true\" />" +
         //
         "<XWidget xwidgetType=\"XCheckBox\" displayName=\"" + PERSIST + "\" defaultValue=\"false\"/>" +
         //
         "</xWidgets>";
   }

   @Override
   public String getDescriptionUsage() {
      return "Takes source Team Workflow(s) and converts state names.  \nUse this BLAM when Work Definition changes require state re-name or removal.  \nThis BLAM will fix old workflows\nNOTE: ATS log will show name changes, but not OSEE history.";
   }

   @Override
   public String getName() {
      return "Convert Workflow State Names";
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("ATS");
   }

}