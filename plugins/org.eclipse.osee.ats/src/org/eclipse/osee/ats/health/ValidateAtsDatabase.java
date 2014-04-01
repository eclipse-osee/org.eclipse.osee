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
package org.eclipse.osee.ats.health;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogItem;
import org.eclipse.osee.ats.api.workflow.log.LogType;
import org.eclipse.osee.ats.core.AtsCore;
import org.eclipse.osee.ats.core.client.branch.AtsBranchManagerCore;
import org.eclipse.osee.ats.core.client.review.AbstractReviewArtifact;
import org.eclipse.osee.ats.core.client.review.AtsReviewCache;
import org.eclipse.osee.ats.core.client.review.defect.ReviewDefectManager;
import org.eclipse.osee.ats.core.client.review.role.UserRoleManager;
import org.eclipse.osee.ats.core.client.task.TaskArtifact;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.util.AtsChangeSet;
import org.eclipse.osee.ats.core.client.util.AtsTaskCache;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.config.AtsVersionService;
import org.eclipse.osee.ats.core.config.TeamDefinitions;
import org.eclipse.osee.ats.core.users.AtsCoreUsers;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.core.workflow.log.AtsLogUtility;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.core.workflow.transition.TransitionManager;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.world.WorldXNavigateItemAction;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.skynet.core.utility.ElapsedTime;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.notify.OseeEmail;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public class ValidateAtsDatabase extends WorldXNavigateItemAction {

   private static String SELECT_COMMON_ART_IDS = "SELECT /*+ ordered */ art1.art_id, txs1.branch_id " + //
   "FROM osee_artifact art1, osee_txs txs1 " + //
   "WHERE art1.gamma_id = txs1.gamma_id AND txs1.tx_current = 1 AND txs1.branch_id = ? " + //
   "ORDER BY art1.art_id, txs1.branch_id ";
   private boolean fixAssignees = true;
   private boolean fixAttributeValues = true;
   private final Set<String> atsIds = new HashSet<String>();
   private final Map<String, String> legacyPcrIdToParentId = new HashMap<String, String>(50000);
   private String emailOnComplete = null;
   private final ValidateResults results = new ValidateResults();

   public ValidateAtsDatabase(XNavigateItem parent) {
      this("Validate ATS Database", parent);
   }

   public ValidateAtsDatabase(String name, XNavigateItem parent) {
      super(parent, name, PluginUiImage.ADMIN);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      if (!MessageDialog.openConfirm(Displays.getActiveShell(), getName(), getName())) {
         return;
      }
      Jobs.startJob(new Report(getName()), true);
   }

   public void performTaskAndPend() throws InterruptedException {
      Report job = new Report(getName());
      job.setUser(true);
      job.setPriority(Job.LONG);
      job.schedule();
      job.join();
   }

   public class Report extends Job {

      public Report(String name) {
         super(name);
      }

      @Override
      protected IStatus run(IProgressMonitor monitor) {
         try {
            ElapsedTime et = new ElapsedTime(getName());
            XResultData rd = new XResultData();

            runIt(monitor, rd);

            String elapsedStr = et.end();
            rd.log("\n\n" + elapsedStr);
            XResultDataUI.report(rd, getName());
            if (Strings.isValid(emailOnComplete)) {
               String html = XResultDataUI.getReport(rd, getName()).getManipulatedHtml();
               OseeEmail.emailHtml(java.util.Collections.singleton(emailOnComplete),
                  String.format("Sync - %s [%s]", DateUtil.getDateNow(), getName()), html);
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
            return new Status(IStatus.ERROR, Activator.PLUGIN_ID, -1, ex.getMessage(), ex);
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
      //      ElapsedTime elapsedTime = new ElapsedTime("ValidateAtsDatabase - load ArtIds");
      artIdLists = loadAtsBranchArtifactIds(xResultData, monitor);
      //      elapsedTime.end();

      // Un-comment to process specific artifact from common - Test Mode
      //      artIdLists = new ArrayList<Collection<Integer>>();
      //      List<Integer> ids = new ArrayList<Integer>();
      //      ids.add(new Integer(1070598));
      //      artIdLists.add(ids);

      // Un-comment to load from guid list
      //      artIdLists = getFromGuids();

      if (monitor != null) {
         monitor.beginTask(getName(), artIdLists.size());
      }

      // Remove this after 0.9.7 release and last sync
      OseeEventManager.setDisableEvents(true);
      try {

         atsIds.clear();
         legacyPcrIdToParentId.clear();

         //         int artSetNum = 1;
         for (Collection<Integer> artIdList : artIdLists) {
            // Don't process all lists if just trying to test this report
            //            elapsedTime =
            //               new ElapsedTime(String.format("ValidateAtsDatabase - load Artifact set %d/%d", artSetNum++,
            //                  artIdLists.size()));
            Date date = new Date();
            Collection<Artifact> allArtifacts =
               ArtifactQuery.getArtifactListFromIds(artIdList, AtsUtilCore.getAtsBranch());
            results.logTestTimeSpent(date, "ArtifactQuery.getArtifactListFromIds");
            //            elapsedTime.end();

            // NOTE: Use DoesNotWorkItemAts to process list of IDs

            // remove all deleted/purged artifacts first
            List<Artifact> artifacts = new ArrayList<Artifact>(allArtifacts.size());
            for (Artifact artifact : allArtifacts) {
               if (!artifact.isDeleted()) {
                  artifacts.add(artifact);
               }
            }
            count += artifacts.size();

            testAtsAttributevaluesWithPersist(artifacts);
            testCompletedCancelledStateAttributesSetWithPersist(artifacts);
            testCompletedCancelledPercentComplete(artifacts);
            testStateAttributeDuplications(artifacts);
            testArtifactIds(artifacts);
            testStateInWorkDefinition(artifacts);
            testAttributeSetWorkDefinitionsExist(artifacts);
            testAtsActionsHaveTeamWorkflow(artifacts);
            testAtsWorkflowsHaveAction(artifacts);
            testAtsWorkflowsHaveZeroOrOneVersion(artifacts);
            testAtsWorkflowsValidVersion(artifacts);
            testTasksHaveParentWorkflow(artifacts);
            testReviewsHaveParentWorkflowOrActionableItems(artifacts);
            testReviewsHaveValidDefectAndRoleXml(artifacts);
            testTeamWorkflows(artifacts);
            testAtsBranchManager(artifacts);
            testTeamDefinitions(artifacts, results);
            testVersionArtifacts(artifacts, results);
            testParallelConfig(artifacts, results);
            testStateMachineAssignees(artifacts);
            testAtsLogs(artifacts);
            testActionableItemToTeamDefinition(artifacts, results);

            for (IAtsHealthCheck atsHealthCheck : AtsHealthCheck.getAtsHealthCheckItems()) {
               atsHealthCheck.validateAtsDatabase(artifacts, results);
            }

            // Clear ATS caches
            for (Artifact artifact : artifacts) {
               if (artifact instanceof TeamWorkFlowArtifact) {
                  AtsTaskCache.decache((TeamWorkFlowArtifact) artifact);
                  AtsReviewCache.decache((TeamWorkFlowArtifact) artifact);
               }
               if (!(artifact instanceof User)) {
                  ArtifactCache.deCache(artifact);
               }
            }

            if (monitor != null) {
               monitor.worked(1);
            }
         }
         // Log resultMap data into xResultData
         results.addResultsMapToResultData(xResultData);
         results.addTestTimeMapToResultData(xResultData);

      } finally {
         OseeEventManager.setDisableEvents(false);
      }
      xResultData.reportSevereLoggingMonitor(monitorLog);
      if (monitor != null) {
         xResultData.log(monitor, "Completed processing " + count + " artifacts.");
      }
   }

   private List<Collection<Integer>> getFromGuids() {
      List<String> guids = Arrays.asList("AD3zXUb9kkF08ltQPwQA", "AD3zWI5UrUmhDxwBekAA", "BPLQGf99g2qG_LoknEQA");
      List<Artifact> artifacts = ArtifactQuery.getArtifactListFromIds(guids, AtsUtilCore.getAtsBranch());
      List<Integer> artIds = new ArrayList<Integer>();
      for (Artifact art : artifacts) {
         artIds.add(art.getArtId());
      }
      return Arrays.asList((Collection<Integer>) artIds);
   }

   public void testCompletedCancelledStateAttributesSetWithPersist(Collection<Artifact> artifacts) {
      try {
         SkynetTransaction transaction =
            TransactionManager.createTransaction(AtsUtilCore.getAtsBranch(), "Validate ATS Database");
         testCompletedCancelledStateAttributesSet(artifacts, transaction, results);
         transaction.execute();
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         results.log("testCompletedCancelledStateAttributesSet", "Error: Exception: " + ex.getLocalizedMessage());
      }
   }

   public static void testCompletedCancelledStateAttributesSet(Collection<Artifact> artifacts, SkynetTransaction transaction, ValidateResults results) {
      Date date = new Date();
      for (Artifact artifact : artifacts) {
         try {
            if (artifact instanceof AbstractWorkflowArtifact) {
               AbstractWorkflowArtifact awa = (AbstractWorkflowArtifact) artifact;
               if (awa.isCompleted()) {
                  IAtsStateDefinition stateDef = awa.getStateDefinition();
                  if (stateDef == null) {
                     results.log(artifact, "testCompletedCancelledStateAttributesSet", String.format(
                        "Error: State Definition null for state [%s] for [%s]", awa.getCurrentStateName(),
                        XResultDataUI.getHyperlink(artifact)));
                  } else if (stateDef.getStateType() != StateType.Completed) {
                     results.log(artifact, "testCompletedCancelledStateAttributesSet", String.format(
                        "Error: awa.isCompleted()==true but State [%s] not Completed state for [%s]",
                        stateDef.getName(), XResultDataUI.getHyperlink(artifact)));
                     if (stateDef.getStateType() == StateType.Working) {
                        awa.setSoleAttributeFromString(AtsAttributeTypes.CurrentStateType, StateType.Working.name());
                        AtsChangeSet changes = new AtsChangeSet(ValidateAtsDatabase.class.getSimpleName());
                        TransitionManager.logWorkflowUnCompletedEvent(awa, stateDef, changes, AtsCore.getAttrResolver());
                        TransitionManager.logWorkflowUnCancelledEvent(awa, stateDef, changes, AtsCore.getAttrResolver());
                        awa.persist(transaction);
                        results.log(artifact, "testCompletedCancelledStateAttributesSet", "FIXED");
                     } else {
                        results.log(artifact, "testCompletedCancelledStateAttributesSet", "MANUAL FIX REQUIRED");
                     }
                  } else if (awa.getCompletedBy() == null || awa.getCompletedDate() == null || !Strings.isValid(awa.getCompletedFromState())) {
                     results.log(
                        artifact,
                        "testCompletedCancelledStateAttributesSet",
                        String.format("Error: Completed [%s] missing one or more Completed attributes for [%s]",
                           awa.getArtifactTypeName(), XResultDataUI.getHyperlink(artifact)));
                     fixCompletedByAttributes(transaction, awa, results);
                  }
               }
               if (awa.isCancelled()) {
                  IAtsStateDefinition stateDef = awa.getStateDefinition();
                  if (stateDef.getStateType() != StateType.Cancelled) {
                     results.log(artifact, "testCompletedCancelledStateAttributesSet", String.format(
                        "Error: awa.isCancelled()==true but State [%s] not Cancelled state for [%s]",
                        stateDef.getName(), XResultDataUI.getHyperlink(artifact)));
                     results.log(artifact, "testCompletedCancelledStateAttributesSet", "MANUAL FIX REQUIRED");
                  } else if (awa.getCancelledBy() == null || awa.getCancelledDate() == null || !Strings.isValid(awa.getCancelledFromState())) {
                     results.log(
                        artifact,
                        "testCompletedCancelledStateAttributesSet",
                        String.format("Error: Cancelled missing Cancelled By attribute for [%s]",
                           XResultDataUI.getHyperlink(artifact)));
                     fixCancelledByAttributes(transaction, awa, results);
                  }
               }
            }
         } catch (Exception ex) {
            results.log(artifact, "testCompletedCancelledStateAttributesSet",
               "Error: on [" + artifact.toStringWithId() + "] exception: " + ex.getLocalizedMessage());
         }
      }
      results.logTestTimeSpent(date, "testCompletedCancelledStateAttributesSet");
   }

   private void testCompletedCancelledPercentComplete(Collection<Artifact> artifacts) {
      Date date = new Date();
      try {
         SkynetTransaction transaction =
            TransactionManager.createTransaction(AtsUtilCore.getAtsBranch(), "Validate ATS Database");
         for (Artifact artifact : artifacts) {
            try {
               if (artifact instanceof AbstractWorkflowArtifact) {
                  AbstractWorkflowArtifact awa = (AbstractWorkflowArtifact) artifact;
                  Integer percentComplete = awa.getSoleAttributeValue(AtsAttributeTypes.PercentComplete, 0);
                  if (awa.isCompletedOrCancelled() && percentComplete != 100) {
                     results.log(artifact, "testCompletedCancelledPercentComplete", String.format(
                        "Error: Completed/Cancelled Percent Complete != 100; is [%d] for [%s]", percentComplete,
                        XResultDataUI.getHyperlink(artifact)));
                     awa.setSoleAttributeValue(AtsAttributeTypes.PercentComplete, 100);
                     awa.persist(transaction);
                     results.log(artifact, "testCompletedCancelledPercentComplete", "FIXED");
                  }
               }
            } catch (Exception ex) {
               results.log(artifact, "testCompletedCancelledPercentComplete",
                  "Error: on [" + artifact.toStringWithId() + "] exception: " + ex.getLocalizedMessage());
            }
         }
         transaction.execute();
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         results.log("testCompletedCancelledPercentComplete", "Error: Exception: " + ex.getLocalizedMessage());
      }

      results.logTestTimeSpent(date, "testCompletedCancelledStateAttributesSet");
   }

   private static void fixCancelledByAttributes(SkynetTransaction transaction, AbstractWorkflowArtifact awa, ValidateResults results) throws OseeCoreException {
      IAtsLogItem cancelledItem = getCancelledLogItem(awa);
      if (cancelledItem != null) {
         results.log(awa, "testCompletedCancelledStateAttributesSet", String.format(
            "   FIXED to By [%s] From State [%s] Date [%s] Reason [%s]", cancelledItem.getUserId(),
            cancelledItem.getDate(), cancelledItem.getState(), cancelledItem.getMsg()));
         awa.setSoleAttributeValue(AtsAttributeTypes.CancelledBy, cancelledItem.getUserId());
         awa.setSoleAttributeValue(AtsAttributeTypes.CancelledDate, cancelledItem.getDate());
         awa.setSoleAttributeValue(AtsAttributeTypes.CancelledFromState, cancelledItem.getState());
         if (Strings.isValid(cancelledItem.getMsg())) {
            awa.setSoleAttributeValue(AtsAttributeTypes.CancelledReason, cancelledItem.getMsg());
         }
         awa.persist(transaction);
      }
   }

   private static void fixCompletedByAttributes(SkynetTransaction transaction, AbstractWorkflowArtifact awa, ValidateResults results) throws OseeCoreException {
      IAtsLogItem completedItem = getPreviousStateLogItem(awa);
      if (completedItem != null) {
         results.log(
            awa,
            "testCompletedCancelledStateAttributesSet",
            String.format("   FIXED to By [%s] From State [%s] Date [%s]", completedItem.getUserId(),
               completedItem.getDate(), completedItem.getState()));
         awa.setSoleAttributeValue(AtsAttributeTypes.CompletedBy, completedItem.getUserId());
         awa.setSoleAttributeValue(AtsAttributeTypes.CompletedDate, completedItem.getDate());
         awa.setSoleAttributeValue(AtsAttributeTypes.CompletedFromState, completedItem.getState());
         awa.persist(transaction);
      }
   }

   private static IAtsLogItem getCancelledLogItem(AbstractWorkflowArtifact awa) throws OseeCoreException {
      String currentStateName = awa.getCurrentStateName();
      IAtsLogItem fromItem = null;
      for (IAtsLogItem item : awa.getLog().getLogItemsReversed()) {
         if (item.getType() == LogType.StateCancelled && Strings.isValid(item.getState()) && !currentStateName.equals(item.getState())) {
            fromItem = item;
            break;
         }
      }
      if (fromItem == null) {
         fromItem = getPreviousStateLogItem(awa);
      }
      return fromItem;
   }

   private static IAtsLogItem getPreviousStateLogItem(AbstractWorkflowArtifact awa) throws OseeCoreException {
      String currentStateName = awa.getCurrentStateName();
      IAtsLogItem fromItem = null;
      for (IAtsLogItem item : awa.getLog().getLogItemsReversed()) {
         if (item.getType() == LogType.StateComplete && Strings.isValid(item.getState()) && !currentStateName.equals(item.getState())) {
            fromItem = item;
            break;
         }
      }
      return fromItem;
   }

   private void testStateAttributeDuplications(Collection<Artifact> artifacts) throws OseeCoreException {
      SkynetTransaction transaction =
         TransactionManager.createTransaction(AtsUtilCore.getAtsBranch(), "Validate ATS Database");
      Date date = new Date();
      for (Artifact artifact : artifacts) {
         try {
            if (artifact instanceof AbstractWorkflowArtifact) {
               AbstractWorkflowArtifact awa = (AbstractWorkflowArtifact) artifact;
               Map<String, Attribute<String>> stateNamesToStateStr = new HashMap<String, Attribute<String>>();
               Attribute<String> currentStateAttr = awa.getSoleAttribute(AtsAttributeTypes.CurrentState);
               String currentStateStr = currentStateAttr.getValue();
               String currentStateName = currentStateStr.replaceAll(";.*$", "");
               stateNamesToStateStr.put(currentStateName, currentStateAttr);

               @SuppressWarnings("deprecation")
               List<Attribute<String>> attributes = awa.getAttributes(AtsAttributeTypes.State);
               for (Attribute<String> stateAttr : attributes) {
                  String stateStr = stateAttr.getValue();
                  String stateName = stateStr.replaceAll(";.*$", "");
                  Attribute<String> storedStateAttr = stateNamesToStateStr.get(stateName);
                  String storedStateStr = "";
                  if (storedStateAttr != null) {
                     storedStateStr = stateNamesToStateStr.get(stateName).getValue();
                  }
                  // If != null, this stateName has already been found
                  if (Strings.isValid(storedStateStr)) {
                     String errorStr =
                        "Error: " + artifact.getArtifactTypeName() + " - " + awa.getAtsId() + " duplicate state: " + stateName;
                     // delete if state attr is same as current state
                     if (currentStateName.equals(stateName)) {
                        errorStr +=
                           String.format(" - state [%s] matches currentState [%s] lastModified [%s] - FIXED", stateStr,
                              currentStateStr, awa.getLastModified());
                        stateAttr.delete();
                     }
                     // delete if strings are same (name; assignee; hours; percent)
                     else if (stateStr.equals(storedStateStr)) {
                        errorStr +=
                           String.format(" - stateStr [%s] matches storedStateStr [%s] lastModified [%s] - FIXED",
                              stateStr, storedStateStr, awa.getLastModified());
                        stateAttr.delete();
                     }
                     // else attempt to delete the oldest
                     else if (stateAttr.getGammaId() < storedStateAttr.getGammaId()) {
                        errorStr +=
                           String.format(
                              " - stateStr [%s] earlier than storedStateStr [%s] - deleted stateAttr - FIXED",
                              stateStr, storedStateStr, awa.getLastModified());
                        stateAttr.delete();
                     } else if (storedStateAttr.getGammaId() < stateAttr.getGammaId()) {
                        errorStr +=
                           String.format(
                              " - stateStr [%s] later than storedStateStr [%s] - deleted storeStateAttr - FIXED",
                              stateStr, storedStateStr, awa.getLastModified());
                        storedStateAttr.delete();
                     } else {
                        errorStr += " - NO FIX AVAIL";
                     }
                     results.log(artifact, "testStateAttributeDuplications", errorStr);
                  } else {
                     stateNamesToStateStr.put(stateName, stateAttr);
                  }
               }
               if (awa.isDirty()) {
                  awa.persist(transaction);
               }
            }
         } catch (Exception ex) {
            results.log(artifact, "testStateAttributeDuplications",
               "Error: " + artifact.getArtifactTypeName() + " exception: " + ex.getLocalizedMessage());
         }
      }
      transaction.execute();
      results.logTestTimeSpent(date, "testStateAttributeDuplications");
   }

   public void testAtsAttributevaluesWithPersist(Collection<Artifact> artifacts) {
      try {
         SkynetTransaction transaction =
            TransactionManager.createTransaction(AtsUtilCore.getAtsBranch(), "Validate ATS Database");
         testAtsAttributeValues(transaction, results, fixAttributeValues, artifacts);
         transaction.execute();
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         results.log("testAtsAttributeValues", "Error: Exception: " + ex.getLocalizedMessage());
      }
   }

   private void testAttributeSetWorkDefinitionsExist(Collection<Artifact> artifacts) {
      Date date = new Date();
      for (Artifact artifact : artifacts) {
         try {
            String workDefName = artifact.getSoleAttributeValue(AtsAttributeTypes.WorkflowDefinition, "");
            if (Strings.isValid(workDefName) && AtsClientService.get().getWorkDefinitionAdmin().getWorkDefinition(
               workDefName) == null) {
               results.log(
                  artifact,
                  "testAttributeSetWorkDefinitionsExist",
                  String.format(
                     "Error: ats.Work Definition attribute value [%s] not valid work definition for " + XResultDataUI.getHyperlink(artifact),
                     workDefName));
            }
         } catch (Exception ex) {
            results.log(artifact, "testAttributeSetWorkDefinitionsExist",
               "Error: " + artifact.getArtifactTypeName() + " exception: " + ex.getLocalizedMessage());
         }
      }
      results.logTestTimeSpent(date, "testAttributeSetWorkDefinitionsExist");
   }

   private void testStateInWorkDefinition(Collection<Artifact> artifacts) {
      Date date = new Date();
      for (Artifact artifact : artifacts) {
         try {
            if (artifact instanceof AbstractWorkflowArtifact) {
               AbstractWorkflowArtifact awa = (AbstractWorkflowArtifact) artifact;
               if (awa.isInWork()) {
                  String currentStatename = awa.getCurrentStateName();
                  IAtsWorkDefinition workDef = awa.getWorkDefinition();
                  if (workDef.getStateByName(currentStatename) == null) {
                     results.log(
                        artifact,
                        "testStateInWorkDefinition",
                        String.format(
                           "Error: Current State [%s] not valid for Work Definition [%s] for " + XResultDataUI.getHyperlink(artifact),
                           currentStatename, workDef.getName()));
                  }
               }
            }
         } catch (Exception ex) {
            results.log(artifact, "testStateInWorkDefinition",
               "Error: " + artifact.getArtifactTypeName() + " exception: " + ex.getLocalizedMessage());
         }
      }
      results.logTestTimeSpent(date, "testStateInWorkDefinition");
   }

   private void testArtifactIds(Collection<Artifact> artifacts) {
      Date date = new Date();
      for (Artifact artifact : artifacts) {
         try {
            if (artifact.isDeleted()) {
               continue;
            }
            // Check that duplicate Legacy PCR IDs team arts do not exist with different parent actions
            if (artifact.isOfType(AtsArtifactTypes.TeamWorkflow)) {
               TeamWorkFlowArtifact teamWf = (TeamWorkFlowArtifact) artifact;
               String legacyPcrId = artifact.getSoleAttributeValueAsString(AtsAttributeTypes.LegacyPcrId, null);
               if (legacyPcrId != null) {
                  String parentActionId = teamWf.getParentActionArtifact().getAtsId();
                  String storedParentActionId = legacyPcrIdToParentId.get(legacyPcrId);
                  if (storedParentActionId != null) {
                     if (!storedParentActionId.equals(parentActionId)) {
                        String errorStr =
                           String.format(
                              "Error: Duplicate Legacy PCR Ids [%s] in Different Actions: teamWf %s parentActionId[%s] != storedActionId [%s] ",
                              legacyPcrId, teamWf.toStringWithId(), parentActionId, storedParentActionId);
                        results.log(artifact, "testArtifactIds", errorStr);
                     }
                  } else {
                     legacyPcrIdToParentId.put(legacyPcrId, parentActionId);
                  }
               }
            }
         } catch (Exception ex) {
            results.log(artifact, "testArtifactIds",
               "Error: " + artifact.getArtifactTypeName() + " exception: " + ex.getLocalizedMessage());
         }
      }
      results.logTestTimeSpent(date, "testArtifactIds");
   }

   public static void testVersionArtifacts(Collection<Artifact> artifacts, ValidateResults results) throws OseeCoreException {
      Date date = new Date();
      for (Artifact artifact : artifacts) {
         if (artifact.isDeleted()) {
            continue;
         }
         if (artifact.isOfType(AtsArtifactTypes.Version)) {
            IAtsVersion version =
               AtsClientService.get().getAtsConfig().getSoleByGuid(artifact.getGuid(), IAtsVersion.class);
            if (version != null) {
               try {
                  long parentBranchUuid = version.getBaselineBranchUuid();
                  if (parentBranchUuid > 0) {
                     validateBranchUuid(version, parentBranchUuid, results);
                  }
                  if (AtsVersionService.get().getTeamDefinition(version) == null) {
                     results.log(artifact, "testVersionArtifacts",
                        "Error: " + version.toStringWithId() + " not related to Team Definition");
                  }

               } catch (Exception ex) {
                  results.log(
                     artifact,
                     "testVersionArtifacts",
                     "Error: " + version.getName() + " exception testing testVersionArtifacts: " + ex.getLocalizedMessage());
               }
            }
         }
      }
      results.logTestTimeSpent(date, "testVersionArtifacts");
   }

   public static void testParallelConfig(List<Artifact> artifacts, ValidateResults results) throws OseeCoreException {
      Date date = new Date();
      for (Artifact artifact : artifacts) {
         if (artifact.isDeleted()) {
            continue;
         }
         if (artifact.isOfType(AtsArtifactTypes.Version)) {
            IAtsVersion version =
               AtsClientService.get().getAtsConfig().getSoleByGuid(artifact.getGuid(), IAtsVersion.class);
            if (version != null) {
               for (IAtsVersion parallelVersion : version.getParallelVersions()) {
                  if (parallelVersion != null) {
                     try {
                        if (!AtsClientService.get().getBranchService().isBranchValid(parallelVersion)) {
                           results.log(
                              artifact,
                              "testParallelConfig",
                              "Error: [" + parallelVersion.toStringWithId() + "] in parallel config without parent branch guid");
                        }
                     } catch (Exception ex) {
                        results.log(
                           artifact,
                           "testParallelConfig",
                           "Error: " + version.getName() + " exception testing testVersionArtifacts: " + ex.getLocalizedMessage());
                     }
                  }
               }
            }
         }
      }
      results.logTestTimeSpent(date, "testParallelConfig");
   }

   public static void testTeamDefinitions(Collection<Artifact> artifacts, ValidateResults results) throws OseeCoreException {
      Date date = new Date();
      for (Artifact art : artifacts) {
         if (art.isDeleted()) {
            continue;
         }
         if (art.isOfType(AtsArtifactTypes.TeamDefinition)) {
            IAtsTeamDefinition teamDef =
               AtsClientService.get().getAtsConfig().getSoleByGuid(art.getGuid(), IAtsTeamDefinition.class);
            try {
               long parentBranchUuid = teamDef.getBaselineBranchUuid();
               if (parentBranchUuid > 0) {
                  validateBranchUuid(teamDef, parentBranchUuid, results);
               }
            } catch (Exception ex) {
               results.log("testTeamDefinitionss",
                  "Error: " + teamDef.getName() + " exception testing testTeamDefinitions: " + ex.getLocalizedMessage());
            }
         }
      }
      results.logTestTimeSpent(date, "testTeamDefinitions");
   }

   private void testTeamWorkflows(Collection<Artifact> artifacts) {
      Date date = new Date();
      for (Artifact artifact : artifacts) {
         if (artifact.isDeleted()) {
            continue;
         }
         if (artifact.isOfType(AtsArtifactTypes.TeamWorkflow)) {
            TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) artifact;
            try {
               if (teamArt.getActionableItemsDam().getActionableItems().isEmpty()) {
                  results.log(artifact, "testTeamWorkflows",
                     "Error: TeamWorkflow " + XResultDataUI.getHyperlink(teamArt) + " has 0 ActionableItems");
               }
               if (teamArt.getTeamDefinition() == null) {
                  results.log(artifact, "testTeamWorkflows",
                     "Error: TeamWorkflow " + XResultDataUI.getHyperlink(teamArt) + " has no TeamDefinition");
               }
               List<String> badGuids = getInvalidGuids(teamArt.getActionableItemsDam().getActionableItemGuids());
               if (!badGuids.isEmpty()) {
                  results.log(
                     artifact,
                     "testTeamWorkflows",
                     "Error: TeamWorkflow " + XResultDataUI.getHyperlink(teamArt) + " has AI guids that don't exisit " + badGuids);
               }
            } catch (Exception ex) {
               results.log(artifact, "testTeamWorkflows",
                  teamArt.getArtifactTypeName() + " exception: " + ex.getLocalizedMessage());
            }
         }
      }
      results.logTestTimeSpent(date, "testTeamWorkflows");
   }

   private List<String> getInvalidGuids(List<String> guids) throws OseeCoreException {
      List<String> badGuids = new ArrayList<String>();
      for (String guid : guids) {
         if (AtsClientService.get().getAtsConfig().getSoleByGuid(guid) == null) {
            badGuids.add(guid);
         }
      }
      return badGuids;
   }

   private void testAtsBranchManager(Collection<Artifact> artifacts) {
      Date date = new Date();
      for (Artifact artifact : artifacts) {
         if (artifact.isDeleted()) {
            continue;
         }
         if (artifact.isOfType(AtsArtifactTypes.TeamWorkflow)) {
            TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) artifact;
            try {
               Branch workingBranch = (Branch) AtsBranchManagerCore.getWorkingBranch(teamArt);
               if (workingBranch != null && workingBranch.getBranchType() != BranchType.BASELINE) {
                  if (workingBranch.getBranchState() != BranchState.COMMITTED) {
                     Collection<IOseeBranch> branchesCommittedTo = AtsBranchManagerCore.getBranchesCommittedTo(teamArt);
                     if (branchesCommittedTo.size() > 0) {
                        results.log(
                           artifact,
                           "testAtsBranchManagerA",
                           "Error: TeamWorkflow " + XResultDataUI.getHyperlink(teamArt) + " has committed branches but working branch [" + workingBranch.getGuid() + "] != COMMITTED");
                     }
                  } else if (workingBranch.getBranchState() == BranchState.COMMITTED && !workingBranch.getArchiveState().isArchived()) {
                     Collection<IOseeBranch> branchesLeftToCommit =
                        AtsBranchManagerCore.getBranchesLeftToCommit(teamArt);
                     if (branchesLeftToCommit.size() == 0) {
                        results.log(
                           artifact,
                           "testAtsBranchManagerA",
                           "Error: TeamWorkflow " + XResultDataUI.getHyperlink(teamArt) + " has committed all branches but working branch [" + workingBranch.getGuid() + "] != ARCHIVED");
                     }
                  }
               }
            } catch (Exception ex) {
               results.log("testAtsBranchManager",
                  teamArt.getArtifactTypeName() + " exception: " + ex.getLocalizedMessage());
            }
         }
      }
      results.logTestTimeSpent(date, "testAtsBranchManager");
   }

   public static void validateBranchUuid(IAtsConfigObject name, long parentBranchUuid, ValidateResults results) {
      Date date = new Date();
      try {
         Branch branch = BranchManager.getBranchByUuid(parentBranchUuid);
         if (branch.getArchiveState().isArchived()) {
            results.log("validateBranchUuid", String.format(
               "Error: [%s][%s][%s] has Parent Branch Id attribute set to Archived Branch [%s] named [%s]",
               name.getName(), name.getGuid(), name, parentBranchUuid, branch));
         } else if (!branch.getBranchType().isBaselineBranch()) {
            results.log(
               "validateBranchUuid",
               String.format(
                  "Error: [%s][%s][%s] has Parent Branch Id attribute [%s][%s] that is a [%s] branch; should be a BASELINE branch",
                  name.getName(), name.getGuid(), name, branch.getBranchType().name(), parentBranchUuid, branch));
         }
      } catch (BranchDoesNotExist ex) {
         results.log("validateBranchUuid", String.format(
            "Error: [%s][%s][%s] has Parent Branch Id attribute [%s] that references a non-existant", name.getName(),
            name.getGuid(), name, parentBranchUuid));
      } catch (Exception ex) {
         results.log("validateBranchUuid",
            "Error: " + name.getName() + " [" + name.toStringWithId() + "] exception: " + ex.getLocalizedMessage());
      }
      results.logTestTimeSpent(date, "validateBranchUuid");
   }

   public static List<Collection<Integer>> loadAtsBranchArtifactIds(XResultData xResultData, IProgressMonitor monitor) throws OseeCoreException {
      if (xResultData == null) {
         xResultData = new XResultData();
      }
      xResultData.log(monitor, "testLoadAllCommonArtifactIds - Started " + DateUtil.getMMDDYYHHMM());
      List<Integer> artIds = getCommonArtifactIds();
      // ArtifactQuery.selectArtifactListFromBranch(AtsUtilCore.getAtsBranchToken(), EXCLUDE_DELETED);

      if (artIds.isEmpty()) {
         xResultData.logError("Error: Artifact load returned 0 artifacts to check");
      }
      xResultData.log(monitor, "testLoadAllCommonArtifactIds - Completed " + DateUtil.getMMDDYYHHMM());
      return Collections.subDivide(artIds, 4000);
   }

   private static List<Integer> getCommonArtifactIds() throws OseeCoreException {
      OseeConnection connection = ConnectionHandler.getConnection();
      IOseeStatement chStmt = ConnectionHandler.getStatement(connection);
      List<Integer> artIds = new ArrayList<Integer>();
      ElapsedTime time = new ElapsedTime("getCommonArtifactIds");
      try {
         chStmt.runPreparedQuery(SELECT_COMMON_ART_IDS, new Object[] {AtsUtilCore.getAtsBranch().getUuid()});
         while (chStmt.next()) {
            artIds.add(chStmt.getInt(1));
         }
      } finally {
         chStmt.close();
         connection.close();
      }
      time.end();
      return artIds;
   }

   public static void testAtsAttributeValues(SkynetTransaction transaction, ValidateResults results, boolean fixAttributeValues, Collection<Artifact> artifacts) {
      Date date = new Date();
      for (Artifact artifact : artifacts) {
         if (artifact.isDeleted()) {
            continue;
         }
         try {
            // Test for null attribute values
            for (Attribute<?> attr : artifact.getAttributes()) {
               if (attr.getValue() == null) {
                  results.log(
                     artifact,
                     "testAtsAttributeValues",
                     "Error: Artifact: " + XResultDataUI.getHyperlink(artifact) + " Types: " + artifact.getArtifactTypeName() + " - Null Attribute");
                  if (fixAttributeValues) {
                     attr.delete();
                  }
               }
            }

            if (artifact instanceof AbstractWorkflowArtifact) {
               checkAndResolveDuplicateAttributes(artifact, fixAttributeValues, results, transaction);
            }

            if (artifact.hasDirtyAttributes()) {
               artifact.persist(transaction);
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
            results.log(artifact, "testAtsAttributeValues",
               "Error: " + artifact.getArtifactTypeName() + " exception: " + ex.getLocalizedMessage());
         }
      }
      results.logTestTimeSpent(date, "testAtsAttributeValues");
   }

   @SuppressWarnings("deprecation")
   private static void checkAndResolveDuplicateAttributes(Artifact artifact, boolean fixAttributeValues, ValidateResults results, SkynetTransaction transaction) throws OseeCoreException {
      for (AttributeType attrType : artifact.getAttributeTypesUsed()) {
         if (artifact.isDeleted()) {
            continue;
         }
         int count = artifact.getAttributeCount(attrType);
         if (count > attrType.getMaxOccurrences()) {
            String result =
               String.format(
                  "Error: Artifact: " + XResultDataUI.getHyperlink(artifact) + " Type [%s] AttrType [%s] Max [%d] Actual [%d] Values [%s] ",
                  artifact.getArtifactTypeName(), attrType.getName(), attrType.getMaxOccurrences(), count,
                  artifact.getAttributesToString(attrType));
            Map<String, Attribute<?>> valuesAttrMap = new HashMap<String, Attribute<?>>();
            int latestGamma = 0;
            StringBuffer fixInfo = new StringBuffer(" - FIX AVAILABLE");
            for (Attribute<?> attr : artifact.getAttributes(attrType)) {
               if (attr.getGammaId() > latestGamma) {
                  latestGamma = attr.getGammaId();
               }
               String info = String.format("[Gamma [%s] Value [%s]]", attr.getGammaId(), attr.getValue());
               valuesAttrMap.put(info, attr);
               fixInfo.append(info);
            }
            fixInfo.append(" - KEEP Gamma");
            fixInfo.append(latestGamma);
            if (latestGamma != 0) {
               result += fixInfo;
               if (fixAttributeValues) {
                  for (Attribute<?> attr : artifact.getAttributes(attrType)) {
                     if (attr.getGammaId() != latestGamma) {
                        attr.delete();
                     }
                  }
                  artifact.persist(transaction);
                  results.log("checkAndResolveDuplicateAttributesForAttributeNameContains", "Fixed");
               }
            }
            results.log("checkAndResolveDuplicateAttributesForAttributeNameContains", result);
         }
      }
   }

   private void testAtsActionsHaveTeamWorkflow(Collection<Artifact> artifacts) {
      Date date = new Date();
      for (Artifact artifact : artifacts) {
         if (artifact.isDeleted()) {
            continue;
         }
         try {
            if (artifact.isOfType(AtsArtifactTypes.Action) && artifact.getRelatedArtifactsCount(AtsRelationTypes.ActionToWorkflow_WorkFlow) == 0) {
               results.log(artifact, "testAtsActionsHaveTeamWorkflow",
                  "Error: Action " + XResultDataUI.getHyperlink(artifact) + " has no Team Workflows\n");
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
            results.log(artifact, "testAtsActionsHaveTeamWorkflow", "Error: Exception: " + ex.getLocalizedMessage());
         }
      }
      results.logTestTimeSpent(date, "testAtsActionsHaveTeamWorkflow");
   }

   private void testAtsWorkflowsHaveAction(Collection<Artifact> artifacts) {
      Date date = new Date();
      for (Artifact artifact : artifacts) {
         if (artifact.isDeleted()) {
            continue;
         }
         if (artifact.isOfType(AtsArtifactTypes.TeamWorkflow)) {
            try {
               int actionCount =
                  ((TeamWorkFlowArtifact) artifact).getRelatedArtifactsCount(AtsRelationTypes.ActionToWorkflow_Action);
               if (actionCount != 1) {
                  results.log(
                     artifact,
                     "testAtsWorkflowsHaveAction",
                     "Error: Team " + XResultDataUI.getHyperlink(artifact) + " has " + actionCount + " parent Action, should be 1\n");
               }
            } catch (Exception ex) {
               results.log(artifact, "testAtsWorkflowsHaveAction",
                  "Error: Team " + artifact.getName() + " has no parent Action: exception " + ex);
            }
         }
      }
      results.logTestTimeSpent(date, "testAtsWorkflowsHaveAction");
   }

   private void testAtsWorkflowsHaveZeroOrOneVersion(Collection<Artifact> artifacts) {
      Date date = new Date();
      for (Artifact artifact : artifacts) {
         if (artifact.isDeleted()) {
            continue;
         }
         try {
            if (artifact.isOfType(AtsArtifactTypes.TeamWorkflow)) {
               TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) artifact;
               if (teamArt.getRelatedArtifacts(AtsRelationTypes.TeamWorkflowTargetedForVersion_Version).size() > 1) {
                  results.log(
                     artifact,
                     "testAtsWorkflowsHaveZeroOrOneVersion",
                     "Error: Team workflow " + XResultDataUI.getHyperlink(teamArt) + " has " + teamArt.getRelatedArtifacts(
                        AtsRelationTypes.TeamWorkflowTargetedForVersion_Version).size() + " versions");
               }
               // Test that targeted version belongs to teamDefHoldingVersion
               else {
                  IAtsVersion verArt = AtsVersionService.get().getTargetedVersion(teamArt);
                  if (verArt != null && teamArt.getTeamDefinition().getTeamDefinitionHoldingVersions() != null) {
                     if (!teamArt.getTeamDefinition().getTeamDefinitionHoldingVersions().getVersions().contains(verArt)) {
                        results.log(
                           artifact,
                           "testAtsWorkflowsHaveZeroOrOneVersion",
                           "Error: Team workflow " + XResultDataUI.getHyperlink(teamArt) + " has version" + XResultDataUI.getHyperlink(teamArt) + " that does not belong to teamDefHoldingVersions" + XResultDataUI.getHyperlink(AtsClientService.get().getConfigArtifact(
                              teamArt.getTeamDefinition().getTeamDefinitionHoldingVersions())));
                     }
                  }
               }
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
            results.log(artifact, "testAtsWorkflowsHaveZeroOrOneVersion",
               "Error: Exception: " + ex.getLocalizedMessage());
         }
      }
      results.logTestTimeSpent(date, "testAtsWorkflowsHaveZeroOrOneVersion");
   }

   private void testAtsWorkflowsValidVersion(Collection<Artifact> artifacts) {
      Date date = new Date();
      for (Artifact artifact : artifacts) {
         if (artifact.isDeleted()) {
            continue;
         }
         try {
            if (artifact.isOfType(AtsArtifactTypes.TeamWorkflow)) {
               TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) artifact;
               IAtsVersion verArt = AtsVersionService.get().getTargetedVersion(teamArt);
               if (verArt != null && teamArt.getTeamDefinition().getTeamDefinitionHoldingVersions() != null) {
                  if (!teamArt.getTeamDefinition().getTeamDefinitionHoldingVersions().getVersions().contains(verArt)) {
                     results.log(
                        artifact,
                        "testAtsWorkflowsValidVersion",
                        "Error: Team workflow " + XResultDataUI.getHyperlink(teamArt) + " has version" + XResultDataUI.getHyperlink(artifact) + " that does not belong to teamDefHoldingVersions" + XResultDataUI.getHyperlink(AtsClientService.get().getConfigArtifact(
                           teamArt.getTeamDefinition().getTeamDefinitionHoldingVersions())));
                  }
               }
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
            results.log(artifact, "testAtsWorkflowsValidVersion", "Error: Exception: " + ex.getLocalizedMessage());
         }
      }
      results.logTestTimeSpent(date, "testAtsWorkflowsValidVersion");
   }

   private void testTasksHaveParentWorkflow(Collection<Artifact> artifacts) {
      Date date = new Date();
      for (Artifact artifact : artifacts) {
         if (artifact.isDeleted()) {
            continue;
         }
         try {
            if (artifact.isOfType(AtsArtifactTypes.Task)) {
               TaskArtifact taskArtifact = (TaskArtifact) artifact;
               if (taskArtifact.getRelatedArtifactsCount(AtsRelationTypes.TeamWfToTask_TeamWf) != 1) {
                  results.log(
                     artifact,
                     "testTasksHaveParentWorkflow",
                     "Error: Task " + XResultDataUI.getHyperlink(taskArtifact) + " has " + taskArtifact.getRelatedArtifacts(
                        AtsRelationTypes.TeamWfToTask_TeamWf).size() + " parents.");
               }
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
            results.log(artifact, "testTasksHaveParentWorkflow", "Error: Exception: " + ex.getLocalizedMessage());
         }
      }
      results.logTestTimeSpent(date, "testTasksHaveParentWorkflow");
   }

   public static void testActionableItemToTeamDefinition(Collection<Artifact> artifacts, ValidateResults results) {
      Date date = new Date();
      for (Artifact artifact : artifacts) {
         if (artifact.isDeleted()) {
            continue;
         }
         try {
            if (artifact.isOfType(AtsArtifactTypes.ActionableItem)) {
               IAtsActionableItem aia =
                  AtsClientService.get().getAtsConfig().getSoleByGuid(artifact.getGuid(), IAtsActionableItem.class);
               if (aia.isActionable() && TeamDefinitions.getImpactedTeamDefs(Arrays.asList(aia)).isEmpty()) {
                  results.log(
                     artifact,
                     "testActionableItemToTeamDefinition",
                     "Error: ActionableItem " + XResultDataUI.getHyperlink(artifact.getName(), artifact) + " has no related IAtsTeamDefinition and is set to Actionable");
               }
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
            results.log(artifact, "testActionableItemToTeamDefinition", "Error: Exception: " + ex.getLocalizedMessage());
         }
      }
      results.logTestTimeSpent(date, "testActionableItemToTeamDefinition");

   }

   @SuppressWarnings("deprecation")
   private void testReviewsHaveValidDefectAndRoleXml(Collection<Artifact> artifacts) {
      Date date = new Date();
      for (Artifact artifact : artifacts) {
         if (artifact.isDeleted()) {
            continue;
         }
         if (artifact instanceof AbstractReviewArtifact) {
            AbstractReviewArtifact reviewArtifact = (AbstractReviewArtifact) artifact;
            try {
               if (reviewArtifact.getAttributes(AtsAttributeTypes.ReviewDefect).size() > 0 && ReviewDefectManager.getDefectItems(
                  reviewArtifact).isEmpty()) {
                  results.log(
                     artifact,
                     "testReviewsHaveValidDefectAndRoleXml",
                     "Error: Review " + XResultDataUI.getHyperlink(reviewArtifact) + " has defect attribute, but no defects (xml parsing error).");
               }
               if (reviewArtifact.getAttributes(AtsAttributeTypes.Role).size() > 0 && UserRoleManager.getUserRoles(
                  reviewArtifact).isEmpty()) {
                  results.log(
                     artifact,
                     "testReviewsHaveValidDefectAndRoleXml",
                     "Error: Review " + XResultDataUI.getHyperlink(reviewArtifact) + " has role attribute, but no roles (xml parsing error).");
               }
            } catch (Exception ex) {
               results.log(artifact, "testReviewsHaveValidDefectAndRoleXml",
                  "Error: Exception processing Review defect test " + ex.getLocalizedMessage());
            }
         }
      }
      results.logTestTimeSpent(date, "testReviewsHaveValidDefectAndRoleXml");
   }

   private void testReviewsHaveParentWorkflowOrActionableItems(Collection<Artifact> artifacts) {
      Date date = new Date();
      for (Artifact artifact : artifacts) {
         if (artifact.isDeleted()) {
            continue;
         }
         try {
            if (artifact instanceof AbstractReviewArtifact) {
               AbstractReviewArtifact reviewArtifact = (AbstractReviewArtifact) artifact;
               if (reviewArtifact.getRelatedArtifactsCount(AtsRelationTypes.TeamWorkflowToReview_Team) == 0 && reviewArtifact.getActionableItemsDam().getActionableItemGuids().isEmpty()) {
                  results.log(
                     artifact,
                     "testReviewsHaveParentWorkflowOrActionableItems",
                     "Error: Review " + XResultDataUI.getHyperlink(reviewArtifact) + " has 0 related parents and 0 actionable items.");
               }
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
            results.log(artifact, "testTeamDefinitionHasWorkflow", "Error: Exception: " + ex.getLocalizedMessage());
         }
      }
      results.logTestTimeSpent(date, "testReviewsHaveParentWorkflowOrActionableItems");
   }

   private void testAtsLogs(Collection<Artifact> artifacts) {
      Date date = new Date();
      for (Artifact artifact : artifacts) {
         if (artifact.isDeleted()) {
            continue;
         }
         try {
            if (artifact instanceof AbstractWorkflowArtifact) {
               AbstractWorkflowArtifact awa = (AbstractWorkflowArtifact) artifact;
               try {
                  awa.getLog();
                  if (awa.getCreatedBy() == null) {
                     try {
                        results.log(
                           artifact,
                           "testAtsLogs",
                           "Error: " + awa.getArtifactTypeName() + " " + XResultDataUI.getHyperlink(awa) + " originator == null");
                     } catch (Exception ex) {
                        results.log(
                           artifact,
                           "testAtsLogs",
                           "Error: " + awa.getArtifactTypeName() + " " + XResultDataUI.getHyperlink(awa) + " exception accessing originator: " + ex.getLocalizedMessage());
                     }
                  }
                  for (IStateToken state : Arrays.asList(TeamState.Completed, TeamState.Cancelled)) {
                     if (awa.isInState(state)) {
                        IAtsLogItem logItem = awa.getStateMgr().getStateStartedData(state);
                        if (logItem == null) {
                           try {
                              results.log(
                                 artifact,
                                 "testAtsLogs",
                                 "Error: " + awa.getArtifactTypeName() + " " + XResultDataUI.getHyperlink(awa) + " state \"" + state + "\" logItem == null");
                           } catch (Exception ex) {
                              results.log(
                                 artifact,
                                 "testAtsLogs",
                                 "Error: " + awa.getArtifactTypeName() + " " + XResultDataUI.getHyperlink(awa) + " exception accessing logItem: " + ex.getLocalizedMessage());

                           }
                        } else if (logItem.getDate() == null) {
                           try {
                              results.log(
                                 artifact,
                                 "testAtsLogs",
                                 "Error: " + awa.getArtifactTypeName() + " " + XResultDataUI.getHyperlink(awa) + " state \"" + state + "\" logItem.date == null");
                           } catch (Exception ex) {
                              results.log(
                                 artifact,
                                 "testAtsLogs",
                                 "Error: " + awa.getArtifactTypeName() + " " + XResultDataUI.getHyperlink(awa) + " exception accessing logItem.date: " + ex.getLocalizedMessage());

                           }
                        }
                     }
                  }
                  // Generate html log which will exercise all the conversions
                  AtsLogUtility.getHtml(awa.getLog(),
                     AtsCore.getLogFactory().getLogProvider(awa, AtsCore.getAttrResolver()), true);
                  // Verify that all users are resolved
                  for (IAtsLogItem logItem : awa.getLog().getLogItems()) {
                     if (logItem.getUserId() == null) {
                        results.log(
                           artifact,
                           "testAtsLogs",
                           "Error: " + awa.getArtifactTypeName() + " " + XResultDataUI.getHyperlink(awa) + " user == null for userId \"" + logItem.getUserId() + "\"");
                     }
                  }
               } catch (Exception ex) {
                  results.log(
                     artifact,
                     "testAtsLogs",
                     "Error: " + awa.getArtifactTypeName() + " " + XResultDataUI.getHyperlink(awa) + " exception accessing AtsLog: " + ex.getLocalizedMessage());
               }
            }
         } catch (Exception ex) {
            results.log(artifact, "testAtsLogs",
               "Error: " + artifact.getArtifactTypeName() + " exception accessing logItem: " + ex.getLocalizedMessage());

         }

      }
      results.logTestTimeSpent(date, "testAtsLogs");
   }
   private static IAtsUser unAssignedUser;
   private static IAtsUser oseeSystemUser;

   private void testStateMachineAssignees(Collection<Artifact> artifacts) {
      Date date = new Date();
      if (unAssignedUser == null) {
         unAssignedUser = AtsCoreUsers.UNASSIGNED_USER;
         oseeSystemUser = AtsCoreUsers.SYSTEM_USER;
      }
      for (Artifact artifact : artifacts) {
         if (artifact.isDeleted()) {
            continue;
         }
         if (artifact instanceof AbstractWorkflowArtifact) {
            try {
               AbstractWorkflowArtifact awa = (AbstractWorkflowArtifact) artifact;
               Collection<? extends IAtsUser> assignees = awa.getStateMgr().getAssignees();
               if ((awa.isCompleted() || awa.isCancelled()) && assignees.size() > 0) {
                  results.log(
                     artifact,
                     "testStateMachineAssignees",
                     "Error: " + awa.getArtifactTypeName() + " " + XResultDataUI.getHyperlink(awa) + " cancel/complete with attribute assignees");
                  if (fixAssignees) {
                     awa.getStateMgr().clearAssignees();
                     awa.persist(getClass().getSimpleName());
                     results.log(artifact, "testStateMachineAssignees", "Fixed");
                  }
               }
               if (assignees.size() > 1 && assignees.contains(unAssignedUser)) {
                  results.log(
                     artifact,
                     "testStateMachineAssignees",
                     "Error: " + awa.getArtifactTypeName() + " " + XResultDataUI.getHyperlink(awa) + " is unassigned and assigned => " + AtsObjects.toString(
                        "; ", assignees));
                  if (fixAssignees) {
                     awa.getStateMgr().removeAssignee(unAssignedUser);
                     results.log(artifact, "testStateMachineAssignees", "Fixed");
                  }
               }
               if (assignees.contains(oseeSystemUser)) {
                  results.log(
                     artifact,
                     "testStateMachineAssignees",
                     "Error: " + awa.getAtsId() + " is assigned to OseeSystem; invalid assignment - MANUAL FIX REQUIRED");
               }
               if (!awa.isCompleted() && !awa.isCancelled() && assignees.isEmpty()) {
                  results.log(
                     artifact,
                     "testStateMachineAssignees",
                     "Error: " + awa.getArtifactTypeName() + " " + XResultDataUI.getHyperlink(awa) + " In Work without assignees");
               }
            } catch (Exception ex) {
               results.log("testStateMachineAssignees",
                  "Error: Exception testing assignees: " + ex.getLocalizedMessage());
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      }
      results.logTestTimeSpent(date, "testStateMachineAssignees");
   }

   public void setFixAssignees(boolean fixAssignees) {
      this.fixAssignees = fixAssignees;
   }

   public void setFixAttributeValues(boolean fixAttributeValues) {
      this.fixAttributeValues = fixAttributeValues;
   }

   public void setEmailOnComplete(String emailOnComplete) {
      this.emailOnComplete = emailOnComplete;
   }

}
