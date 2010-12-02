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
package org.eclipse.osee.ats.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.artifact.log.LogItem;
import org.eclipse.osee.ats.health.ValidateAtsDatabase;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.ElapsedTime;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.notify.OseeNotificationManager;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageType;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public class ConvertAtsFor097Database extends XNavigateItemAction {
   private HashCollection<String, String> testNameToResultsMap = null;

   public ConvertAtsFor097Database(XNavigateItem parent) {
      super(parent, "Convert ATS for 0.9.7 Database", PluginUiImage.ADMIN);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      if (!MessageDialog.openConfirm(Displays.getActiveShell(), getName(),
         getName() + "\n\nThis will set appropriate attribute types that were added in 0.9.7 to replace\n" + //
         "the work that was done previously by the ats.Log attribute.  \n" //
            + "This can be run mulitple times without error, but needs to be \n" //
            + "run after types are imported into database.")) {
         return;
      }
      Jobs.startJob(new Report(getName()), true);
   }

   public class Report extends Job {

      public Report(String name) {
         super(name);
      }

      @Override
      protected IStatus run(IProgressMonitor monitor) {
         try {
            OseeNotificationManager.getInstance().setEmailEnabled(false);
            XResultData rd = new XResultData(false);
            runIt(monitor, rd);
            rd.report(getName());
         } catch (Exception ex) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
            return new Status(IStatus.ERROR, AtsPlugin.PLUGIN_ID, -1, ex.getMessage(), ex);
         } finally {
            OseeNotificationManager.getInstance().setEmailEnabled(true);
         }
         monitor.done();
         return Status.OK_STATUS;
      }
   }

   public void runIt(IProgressMonitor monitor, XResultData xResultData) throws OseeCoreException {
      SevereLoggingMonitor monitorLog = new SevereLoggingMonitor();
      OseeLog.registerLoggerListener(monitorLog);

      int count = 0;
      // Break artifacts into blocks so don't run out of memory
      List<Collection<Integer>> artIdLists = null;

      // Un-comment to process whole Common branch - Normal Mode
      ElapsedTime elapsedTime = new ElapsedTime(getName() + " - load ArtIds");
      artIdLists = ValidateAtsDatabase.loadAtsBranchArtifactIds(xResultData, monitor);
      elapsedTime.end();

      // Un-comment to process specific artifact from common - Test Mode
      //      artIdLists = Arrays.asList((Collection<Integer>) Arrays.asList(new Integer(510936)));

      if (monitor != null) {
         monitor.beginTask(getName(), artIdLists.size());
      }
      int artSetNum = 1;
      testNameToResultsMap = new HashCollection<String, String>();
      for (Collection<Integer> artIdList : artIdLists) {
         SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), getName());
         // Don't process all lists if just trying to test this report
         elapsedTime =
            new ElapsedTime(String.format(getName() + " - load Artifact set %d/%d", artSetNum++, artIdLists.size()));
         Collection<Artifact> artifacts = ArtifactQuery.getArtifactListFromIds(artIdList, AtsUtil.getAtsBranch());
         elapsedTime.end();
         count += artifacts.size();
         convertWorkPageDefinitions(testNameToResultsMap, artifacts, transaction);
         convertWorkflowArtifacts(testNameToResultsMap, artifacts, transaction);
         if (monitor != null) {
            monitor.worked(1);
         }
         transaction.execute();
      }
      // Log resultMap data into xResultData
      String[] keys = testNameToResultsMap.keySet().toArray(new String[testNameToResultsMap.keySet().size()]);
      Arrays.sort(keys);
      for (String testName : keys) {
         xResultData.log(testName);
         for (String result : testNameToResultsMap.getValues(testName)) {
            xResultData.log(result);
         }
      }
      xResultData.reportSevereLoggingMonitor(monitorLog);
      if (monitor != null) {
         xResultData.log(monitor, "Completed processing " + count + " artifacts.");
      }
   }

   public static void convertWorkflowArtifacts(HashCollection<String, String> testNameToResultsMap, Collection<Artifact> artifacts, SkynetTransaction transaction) {
      for (Artifact artifact : artifacts) {
         try {
            if (artifact.isOfType(AtsArtifactTypes.StateMachineArtifact)) {
               AbstractWorkflowArtifact aba = (AbstractWorkflowArtifact) artifact;
               setCurrentStateType(testNameToResultsMap, aba);
               setCompletedAttributes(testNameToResultsMap, aba);
               setCancelledAttributes(testNameToResultsMap, aba);
               setCreatedAttributes(testNameToResultsMap, aba);
               if (aba.isDirty()) {
                  aba.persist(transaction);
               }
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
            testNameToResultsMap.put("convertWorkPageDefinitions", "Error: Exception: " + ex.getLocalizedMessage());
         }
      }
   }

   private static void setCreatedAttributes(HashCollection<String, String> testNameToResultsMap, AbstractWorkflowArtifact aba) throws OseeCoreException {
      if (aba.getSoleAttributeValueAsString(AtsAttributeTypes.CreatedBy, null) == null) {
         aba.setSoleAttributeValue(AtsAttributeTypes.CreatedBy, aba.getLog().internalGetOriginator().getUserId());
         aba.setSoleAttributeValue(AtsAttributeTypes.CreatedDate, aba.getLog().internalGetCreationDate());
         testNameToResultsMap.put("setCreatedAttributes",
            "Info: " + XResultData.getHyperlink(aba.getName(), aba) + " adding created attributes.");
      }
   }

   private static void setCancelledAttributes(HashCollection<String, String> testNameToResultsMap, AbstractWorkflowArtifact aba) throws OseeCoreException {
      String stateType = aba.getSoleAttributeValueAsString(AtsAttributeTypes.CurrentStateType, "");
      boolean stateTypeSaysCancelled = stateType.equals(WorkPageType.Cancelled.name());
      boolean currentStateIsCancelled = aba.getCurrentStateName().equals(TeamState.Cancelled.name());
      if (currentStateIsCancelled || stateTypeSaysCancelled) {
         LogItem item = aba.getLog().internalGetCancelledLogItem();
         aba.setSoleAttributeValue(AtsAttributeTypes.CancelledBy, item.getUserId());
         aba.setSoleAttributeValue(AtsAttributeTypes.CancelledDate, item.getDate());
         aba.setSoleAttributeValue(AtsAttributeTypes.CancelledFromState, aba.getLog().internalGetCancelledFromState());
         aba.setSoleAttributeValue(AtsAttributeTypes.CancelledReason, aba.getLog().internalGetCancelledReason());
         testNameToResultsMap.put("setCancelledAttributes",
            "Info: " + XResultData.getHyperlink(aba.getName(), aba) + " adding cancelled attributes.");
      }
   }

   private static void setCompletedAttributes(HashCollection<String, String> testNameToResultsMap, AbstractWorkflowArtifact aba) throws OseeCoreException {
      String stateType = aba.getSoleAttributeValueAsString(AtsAttributeTypes.CurrentStateType, "");
      boolean stateTypeSaysCompleted = stateType.equals(WorkPageType.Completed.name());
      boolean currentStateIsCompleted = aba.getCurrentStateName().equals(TeamState.Completed.name());
      if (currentStateIsCompleted || stateTypeSaysCompleted) {
         LogItem item = aba.getLog().internalGetCompletedLogItem();
         aba.setSoleAttributeValue(AtsAttributeTypes.CompletedBy, item.getUserId());
         aba.setSoleAttributeValue(AtsAttributeTypes.CompletedDate, item.getDate());
         aba.setSoleAttributeValue(AtsAttributeTypes.CompletedFromState, aba.getLog().internalGetCompletedFromState());
         testNameToResultsMap.put("setCompletedAttributes",
            "Info: " + XResultData.getHyperlink(aba.getName(), aba) + " adding completed attributes.");
      }
   }

   private static void setCurrentStateType(HashCollection<String, String> testNameToResultsMap, AbstractWorkflowArtifact aba) throws OseeCoreException {
      String stateType = aba.getSoleAttributeValueAsString(AtsAttributeTypes.CurrentStateType, "");
      if (aba.getCurrentStateName().equals("Completed") && !stateType.equals(WorkPageType.Completed.name())) {
         aba.setSoleAttributeValue(AtsAttributeTypes.CurrentStateType, WorkPageType.Completed.name());
      } else if (aba.getCurrentStateName().equals("Cancelled") && !stateType.equals(WorkPageType.Cancelled.name())) {
         aba.setSoleAttributeValue(AtsAttributeTypes.CurrentStateType, WorkPageType.Cancelled.name());
      } else if (!stateType.equals(WorkPageType.Working.name())) {
         aba.setSoleAttributeValue(AtsAttributeTypes.CurrentStateType, WorkPageType.Working.name());
      }
      testNameToResultsMap.put("setCurrentStateType",
         "Info: " + XResultData.getHyperlink(aba.getName(), aba) + " adding current state type.");
   }

   public static void convertWorkPageDefinitions(HashCollection<String, String> testNameToResultsMap, Collection<Artifact> artifacts, SkynetTransaction transaction) {
      for (Artifact artifact : artifacts) {
         try {
            if (artifact.isOfType(CoreArtifactTypes.WorkPageDefinition)) {
               WorkPageDefinition page = new WorkPageDefinition(artifact);
               if (artifact.getSoleAttributeValueAsString(CoreAttributeTypes.WorkPageType, null) == null) {
                  testNameToResultsMap.put(
                     "convertWorkPageDefinitions",
                     "Info: WorkPageDefinition: " + XResultData.getHyperlink(artifact.getName(), artifact) + " adding Work Page Type attribute.");
                  if (page.getPageName().equals("Completed")) {
                     artifact.setSoleAttributeValue(CoreAttributeTypes.WorkPageType, WorkPageType.Completed.name());
                  } else if (page.getPageName().equals("Cancelled")) {
                     artifact.setSoleAttributeValue(CoreAttributeTypes.WorkPageType, WorkPageType.Cancelled.name());
                  } else {
                     artifact.setSoleAttributeValue(CoreAttributeTypes.WorkPageType, WorkPageType.Working.name());
                  }
               }
               if (artifact.isDirty()) {
                  artifact.persist(transaction);
               }
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
            testNameToResultsMap.put("convertWorkPageDefinitions", "Error: Exception: " + ex.getLocalizedMessage());
         }
      }
   }

}
