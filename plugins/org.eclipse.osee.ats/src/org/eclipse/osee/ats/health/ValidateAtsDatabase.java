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
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogItem;
import org.eclipse.osee.ats.api.workflow.log.LogType;
import org.eclipse.osee.ats.core.config.TeamDefinitions;
import org.eclipse.osee.ats.core.config.Versions;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.core.util.ConvertAtsConfigGuidAttributesOperations;
import org.eclipse.osee.ats.core.workflow.transition.TransitionManager;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.workflow.review.AbstractReviewArtifact;
import org.eclipse.osee.ats.workflow.task.TaskArtifact;
import org.eclipse.osee.ats.workflow.task.internal.AtsTaskCache;
import org.eclipse.osee.ats.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.world.WorldXNavigateItemAction;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.core.util.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.ElapsedTime;
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
import org.eclipse.osee.framework.skynet.core.utility.ConnectionHandler;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.notify.OseeEmail;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.jdbc.JdbcStatement;

/**
 * @author Donald G. Dunne
 */
public class ValidateAtsDatabase extends WorldXNavigateItemAction {

   private static String SELECT_COMMON_ART_IDS = "SELECT art1.art_id, txs1.branch_id " + //
      "FROM osee_artifact art1, osee_txs txs1 " + //
      "WHERE art1.gamma_id = txs1.gamma_id AND txs1.tx_current = 1 AND txs1.branch_id = ? " + //
      "ORDER BY art1.art_id, txs1.branch_id ";
   private boolean fixAttributeValues = false;
   private final Set<String> atsIds = new HashSet<>();
   private final Map<String, String> legacyPcrIdToParentId = new HashMap<>(50000);
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

   public void runIt(IProgressMonitor monitor, XResultData xResultData) {
      SevereLoggingMonitor monitorLog = new SevereLoggingMonitor();
      OseeLog.registerLoggerListener(monitorLog);

      int count = 0;
      // Break artifacts into blocks so don't run out of memory

      // Un-comment to process whole Common branch - Normal Mode
      //      ElapsedTime elapsedTime = new ElapsedTime("ValidateAtsDatabase - load ArtIds");
      List<Collection<ArtifactId>> artIdLists = loadAtsBranchArtifactIds(xResultData, monitor);
      //      elapsedTime.end();

      // Un-comment to process specific artifact from common - Test Mode
      //      artIdLists = new ArrayList<>();
      //      List<Integer> ids = new ArrayList<>();
      //      ids.add(new Integer(1070598));
      //      artIdLists.add(ids);

      if (monitor != null) {
         monitor.beginTask(getName(), artIdLists.size());
      }

      // Remove this after 0.9.7 release and last sync
      OseeEventManager.setDisableEvents(true);
      try {

         atsIds.clear();
         legacyPcrIdToParentId.clear();

         //         int artSetNum = 1;
         for (Collection<ArtifactId> artIdList : artIdLists) {
            // Don't process all lists if just trying to test this report
            //            elapsedTime =
            //               new ElapsedTime(String.format("ValidateAtsDatabase - load Artifact set %d/%d", artSetNum++,
            //                  artIdLists.size()));
            Date date = new Date();
            Collection<Artifact> allArtifacts =
               ArtifactQuery.getArtifactListFrom(artIdList, AtsClientService.get().getAtsBranch());
            results.logTestTimeSpent(date, "ArtifactQuery.getArtifactListFromIds");
            //            elapsedTime.end();

            // NOTE: Use DoesNotWorkItemAts to process list of IDs

            // remove all deleted/purged artifacts first
            List<Artifact> artifacts = new ArrayList<>(allArtifacts.size());
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
            testAtsWorkflowsValidVersion(artifacts);
            testTasksHaveParentWorkflow(artifacts);
            testReviewsHaveParentWorkflowOrActionableItems(artifacts);
            testTeamWorkflows(artifacts);
            testAtsBranchManager(artifacts);
            testTeamDefinitions(artifacts, results);
            testVersionArtifacts(artifacts, results);
            testParallelConfig(artifacts, results);
            testActionableItemToTeamDefinition(artifacts, results);

            for (IAtsHealthCheck atsHealthCheck : AtsHealthCheck.getAtsHealthCheckItems()) {
               atsHealthCheck.validateAtsDatabase(artifacts, results);
            }

            /**
             * Clear ATS caches; Don't run during tests. Not good for tests to have cached copies held and then new
             * copies loaded. Can result in duplicate non-historical artifacts in JVM.
             */
            for (Artifact artifact : artifacts) {
               if (artifact instanceof TeamWorkFlowArtifact) {
                  AtsTaskCache.decache((TeamWorkFlowArtifact) artifact);
               }
               if (!AtsUtil.isInTest() && !(artifact instanceof User)) {
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

   public void testCompletedCancelledStateAttributesSetWithPersist(Collection<Artifact> artifacts) {
      try {
         SkynetTransaction transaction =
            TransactionManager.createTransaction(AtsClientService.get().getAtsBranch(), "Validate ATS Database");
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
                     results.log(artifact, "testCompletedCancelledStateAttributesSet",
                        String.format("Error: State Definition null for state [%s] for [%s]", awa.getCurrentStateName(),
                           XResultDataUI.getHyperlink(artifact)));
                  } else if (stateDef.getStateType() != StateType.Completed) {
                     results.log(artifact, "testCompletedCancelledStateAttributesSet",
                        String.format("Error: awa.isCompleted()==true but State [%s] not Completed state for [%s]",
                           stateDef.getName(), XResultDataUI.getHyperlink(artifact)));
                     if (stateDef.getStateType() == StateType.Working) {
                        awa.setSoleAttributeFromString(AtsAttributeTypes.CurrentStateType, StateType.Working.name());
                        IAtsChangeSet changes =
                           AtsClientService.get().createChangeSet(ValidateAtsDatabase.class.getSimpleName());
                        TransitionManager.logWorkflowUnCompletedEvent(awa, stateDef, changes,
                           AtsClientService.get().getAttributeResolver());
                        TransitionManager.logWorkflowUnCancelledEvent(awa, stateDef, changes,
                           AtsClientService.get().getAttributeResolver());
                        awa.persist(transaction);
                        results.log(artifact, "testCompletedCancelledStateAttributesSet", "FIXED");
                     } else {
                        results.log(artifact, "testCompletedCancelledStateAttributesSet", "MANUAL FIX REQUIRED");
                     }
                  } else if (awa.getCompletedBy() == null || awa.getCompletedDate() == null || !Strings.isValid(
                     awa.getCompletedFromState())) {
                     results.log(artifact, "testCompletedCancelledStateAttributesSet",
                        String.format("Error: Completed [%s] missing one or more Completed attributes for [%s]",
                           awa.getArtifactTypeName(), XResultDataUI.getHyperlink(artifact)));
                     fixCompletedByAttributes(transaction, awa, results);
                  }
               }
               if (awa.isCancelled()) {
                  IAtsStateDefinition stateDef = awa.getStateDefinition();
                  if (stateDef.getStateType() != StateType.Cancelled) {
                     results.log(artifact, "testCompletedCancelledStateAttributesSet",
                        String.format("Error: awa.isCancelled()==true but State [%s] not Cancelled state for [%s]",
                           stateDef.getName(), XResultDataUI.getHyperlink(artifact)));
                     results.log(artifact, "testCompletedCancelledStateAttributesSet", "MANUAL FIX REQUIRED");
                  } else if (awa.getCancelledBy() == null || awa.getCancelledDate() == null || !Strings.isValid(
                     awa.getCancelledFromState())) {
                     results.log(artifact, "testCompletedCancelledStateAttributesSet",
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
            TransactionManager.createTransaction(AtsClientService.get().getAtsBranch(), "Validate ATS Database");
         for (Artifact artifact : artifacts) {
            try {
               if (artifact instanceof AbstractWorkflowArtifact) {
                  AbstractWorkflowArtifact awa = (AbstractWorkflowArtifact) artifact;
                  Integer percentComplete = awa.getSoleAttributeValue(AtsAttributeTypes.PercentComplete, 0);
                  if (awa.isCompletedOrCancelled() && percentComplete != 100) {
                     results.log(artifact, "testCompletedCancelledPercentComplete",
                        String.format("Error: Completed/Cancelled Percent Complete != 100; is [%d] for [%s]",
                           percentComplete, XResultDataUI.getHyperlink(artifact)));
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

   private static void fixCancelledByAttributes(SkynetTransaction transaction, AbstractWorkflowArtifact awa, ValidateResults results) {
      IAtsLogItem cancelledItem = getCancelledLogItem(awa);
      if (cancelledItem != null) {
         results.log(awa, "testCompletedCancelledStateAttributesSet",
            String.format("   FIXED to By [%s] From State [%s] Date [%s] Reason [%s]", cancelledItem.getUserId(),
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

   private static void fixCompletedByAttributes(SkynetTransaction transaction, AbstractWorkflowArtifact awa, ValidateResults results) {
      IAtsLogItem completedItem = getPreviousStateLogItem(awa);
      if (completedItem != null) {
         results.log(awa, "testCompletedCancelledStateAttributesSet",
            String.format("   FIXED to By [%s] From State [%s] Date [%s]", completedItem.getUserId(),
               completedItem.getDate(), completedItem.getState()));
         awa.setSoleAttributeValue(AtsAttributeTypes.CompletedBy, completedItem.getUserId());
         awa.setSoleAttributeValue(AtsAttributeTypes.CompletedDate, completedItem.getDate());
         awa.setSoleAttributeValue(AtsAttributeTypes.CompletedFromState, completedItem.getState());
         awa.persist(transaction);
      }
   }

   private static IAtsLogItem getCancelledLogItem(AbstractWorkflowArtifact awa) {
      String currentStateName = awa.getCurrentStateName();
      IAtsLogItem fromItem = null;
      for (IAtsLogItem item : awa.getLog().getLogItemsReversed()) {
         if (item.getType() == LogType.StateCancelled && Strings.isValid(item.getState()) && !currentStateName.equals(
            item.getState())) {
            fromItem = item;
            break;
         }
      }
      if (fromItem == null) {
         fromItem = getPreviousStateLogItem(awa);
      }
      return fromItem;
   }

   private static IAtsLogItem getPreviousStateLogItem(AbstractWorkflowArtifact awa) {
      String currentStateName = awa.getCurrentStateName();
      IAtsLogItem fromItem = null;
      for (IAtsLogItem item : awa.getLog().getLogItemsReversed()) {
         if (item.getType() == LogType.StateComplete && Strings.isValid(item.getState()) && !currentStateName.equals(
            item.getState())) {
            fromItem = item;
            break;
         }
      }
      return fromItem;
   }

   private void testStateAttributeDuplications(Collection<Artifact> artifacts) {
      SkynetTransaction transaction =
         TransactionManager.createTransaction(AtsClientService.get().getAtsBranch(), "Validate ATS Database");
      Date date = new Date();
      for (Artifact artifact : artifacts) {
         try {
            if (artifact instanceof AbstractWorkflowArtifact) {
               AbstractWorkflowArtifact awa = (AbstractWorkflowArtifact) artifact;
               Map<String, Attribute<String>> stateNamesToStateStr = new HashMap<>();
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
                        errorStr += String.format(" - state [%s] matches currentState [%s] lastModified [%s] - FIXED",
                           stateStr, currentStateStr, awa.getLastModified());
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
                     else if (storedStateAttr != null && stateAttr.getGammaId().isLessThan(
                        storedStateAttr.getGammaId())) {
                        errorStr += String.format(
                           " - stateStr [%s] earlier than storedStateStr [%s] - deleted stateAttr - FIXED - last mod %s",
                           stateStr, storedStateStr, awa.getLastModified());
                        stateAttr.delete();
                     } else if (storedStateAttr != null && storedStateAttr.getGammaId().isLessThan(
                        stateAttr.getGammaId())) {
                        errorStr += String.format(
                           " - stateStr [%s] later than storedStateStr [%s] - deleted storeStateAttr - FIXED - last mod %s",
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
            TransactionManager.createTransaction(AtsClientService.get().getAtsBranch(), "Validate ATS Database");
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
            // delete after 26.0
            String workDefName =
               artifact.getSoleAttributeValue(ConvertAtsConfigGuidAttributesOperations.WorkflowDefinition, "");
            if (Strings.isValid(workDefName) && AtsClientService.get().getWorkDefinitionService().getWorkDefinition(
               workDefName) == null) {
               results.log(artifact, "testAttributeSetWorkDefinitionsExist",
                  String.format(
                     "Error: ats.Work Definition attribute value [%s] not valid work definition for " + XResultDataUI.getHyperlink(
                        artifact),
                     workDefName));
            }

            ArtifactId workDefArt =
               artifact.getSoleAttributeValue(AtsAttributeTypes.WorkflowDefinitionReference, ArtifactId.SENTINEL);
            if (workDefArt.isValid() && AtsClientService.get().getWorkDefinitionService().getWorkDefinition(
               workDefArt) == null) {
               results.log(artifact, "testAttributeSetWorkDefinitionsExist",
                  String.format(
                     "Error: ats.Work Definition attribute value [%s] not valid work definition for " + XResultDataUI.getHyperlink(
                        artifact),
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
                     results.log(artifact, "testStateInWorkDefinition",
                        String.format(
                           "Error: Current State [%s] not valid for Work Definition [%s] for " + XResultDataUI.getHyperlink(
                              artifact),
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
                        String errorStr = String.format(
                           "Error: Duplicate Legacy PCR Ids [%s] in Different Actions: teamWf %s parentActionId[%s] != storedActionId [%s] ",
                           legacyPcrId, teamWf.toStringWithId(), parentActionId, storedParentActionId);
                        results.log(artifact, "testArtifactIds", errorStr);
                     }
                  } else {
                     legacyPcrIdToParentId.put(legacyPcrId, parentActionId);
                  }
               }
            }
            // Test that ATS Id is set
            if (artifact instanceof IAtsWorkItem) {
               if (artifact.getSoleAttributeValue(AtsAttributeTypes.AtsId, null) == null) {
                  String errorStr =
                     String.format("Error: ATS Id not set for work item [%s] ", artifact.toStringWithId());
                  results.log(artifact, "testArtifactIds", errorStr);
               }
            }
         } catch (Exception ex) {
            results.log(artifact, "testArtifactIds",
               "Error: " + artifact.getArtifactTypeName() + " exception: " + ex.getLocalizedMessage());
         }
      }
      results.logTestTimeSpent(date, "testArtifactIds");
   }

   public static void testVersionArtifacts(Collection<Artifact> artifacts, ValidateResults results) {
      Date date = new Date();
      for (Artifact artifact : artifacts) {
         if (artifact.isDeleted()) {
            continue;
         }
         if (artifact.isOfType(AtsArtifactTypes.Version)) {
            IAtsVersion version = AtsClientService.get().getVersionService().getById(artifact);
            if (version != null) {
               try {
                  BranchId parentBranchId = version.getBaselineBranchId();
                  if (parentBranchId.isValid()) {
                     validateBranchId(version, parentBranchId, results);
                  }
                  if (AtsClientService.get().getVersionService().getTeamDefinition(version) == null) {
                     results.log(artifact, "testVersionArtifacts",
                        "Error: " + version.toStringWithId() + " not related to Team Definition");
                  }

               } catch (Exception ex) {
                  results.log(artifact, "testVersionArtifacts",
                     "Error: " + version.getName() + " exception testing testVersionArtifacts: " + ex.getLocalizedMessage());
               }
            }
         }
      }
      results.logTestTimeSpent(date, "testVersionArtifacts");
   }

   public static void testParallelConfig(List<Artifact> artifacts, ValidateResults results) {
      Date date = new Date();
      for (Artifact artifact : artifacts) {
         if (artifact.isDeleted()) {
            continue;
         }
         if (artifact.isOfType(AtsArtifactTypes.Version)) {
            IAtsVersion version = AtsClientService.get().getVersionService().getById(artifact);
            if (version != null) {
               for (IAtsVersion parallelVersion : Versions.getParallelVersions(version, AtsClientService.get())) {
                  if (parallelVersion != null) {
                     try {
                        if (!AtsClientService.get().getBranchService().isBranchValid(parallelVersion)) {
                           results.log(artifact, "testParallelConfig",
                              "Error: [" + parallelVersion.toStringWithId() + "] in parallel config without parent branch id");
                        }
                     } catch (Exception ex) {
                        results.log(artifact, "testParallelConfig",
                           "Error: " + version.getName() + " exception testing testVersionArtifacts: " + ex.getLocalizedMessage());
                     }
                  }
               }
            }
         }
      }
      results.logTestTimeSpent(date, "testParallelConfig");
   }

   public static void testTeamDefinitions(Collection<Artifact> artifacts, ValidateResults results) {
      Date date = new Date();
      for (Artifact art : artifacts) {
         if (art.isDeleted()) {
            continue;
         }
         if (art.isOfType(AtsArtifactTypes.TeamDefinition)) {
            IAtsTeamDefinition teamDef = AtsClientService.get().getTeamDefinitionService().getTeamDefinitionById(art);
            try {
               BranchId parentBranchId = teamDef.getBaselineBranchId();
               if (parentBranchId.isValid()) {
                  validateBranchId(teamDef, parentBranchId, results);
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
               if (!AtsClientService.get().getWorkItemService().getActionableItemService().hasActionableItems(
                  teamArt)) {
                  results.log(artifact, "testTeamWorkflows",
                     "Error: TeamWorkflow " + XResultDataUI.getHyperlink(teamArt) + " has 0 ActionableItems");
               }
               if (teamArt.getTeamDefinition() == null) {
                  results.log(artifact, "testTeamWorkflows",
                     "Error: TeamWorkflow " + XResultDataUI.getHyperlink(teamArt) + " has no TeamDefinition");
               }
               List<Long> badIds = getInvalidIds(AtsObjects.toIds(
                  AtsClientService.get().getWorkItemService().getActionableItemService().getActionableItems(teamArt)));
               if (!badIds.isEmpty()) {
                  results.log(artifact, "testTeamWorkflows", "Error: TeamWorkflow " + XResultDataUI.getHyperlink(
                     teamArt) + " has AI ids that don't exisit " + badIds);
               }
            } catch (Exception ex) {
               results.log(artifact, "testTeamWorkflows",
                  teamArt.getArtifactTypeName() + " exception: " + ex.getLocalizedMessage());
            }
         }
      }
      results.logTestTimeSpent(date, "testTeamWorkflows");
   }

   private List<Long> getInvalidIds(List<Long> ids) {
      List<Long> badIds = new ArrayList<>();
      for (Long id : ids) {
         if (AtsClientService.get().getQueryService().getArtifact(id) == null) {
            badIds.add(id);
         }
      }
      return badIds;
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
               BranchId workingBranch = AtsClientService.get().getBranchService().getWorkingBranch(teamArt);
               if (workingBranch != null && workingBranch.isValid() && !BranchManager.getType(
                  workingBranch).isBaselineBranch()) {
                  if (!BranchManager.getState(workingBranch).isCommitted()) {
                     Collection<BranchId> branchesCommittedTo =
                        AtsClientService.get().getBranchService().getBranchesCommittedTo(teamArt);
                     if (!branchesCommittedTo.isEmpty()) {
                        results.log(artifact, "testAtsBranchManagerA",
                           "Error: TeamWorkflow " + XResultDataUI.getHyperlink(
                              teamArt) + " has committed branches but working branch [" + workingBranch + "] != COMMITTED");
                     }
                  } else if (!BranchManager.isArchived(workingBranch)) {
                     Collection<BranchId> branchesLeftToCommit =
                        AtsClientService.get().getBranchService().getBranchesLeftToCommit(teamArt);
                     if (branchesLeftToCommit.isEmpty()) {
                        results.log(artifact, "testAtsBranchManagerA",
                           "Error: TeamWorkflow " + XResultDataUI.getHyperlink(
                              teamArt) + " has committed all branches but working branch [" + workingBranch + "] != ARCHIVED");
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

   public static void validateBranchId(IAtsConfigObject configObj, BranchId parentBranchId, ValidateResults results) {
      Date date = new Date();
      try {
         BranchId branch = parentBranchId;
         if (BranchManager.isArchived(branch)) {
            Branch brch = BranchManager.getBranch(branch);
            results.log("validateBranchId",
               String.format("Error: Config Object %s has Parent Branch Id attribute [%s] set to Archived Branch %s.",
                  configObj.toStringWithId(), parentBranchId, brch.toStringWithId()));
         } else if (!BranchManager.getType(branch).isBaselineBranch()) {
            results.log("validateBranchId",
               String.format(
                  "Error: Config Object %s has Parent Branch Id attribute [%s] that is a [%s] branch; should be a BASELINE branch.",
                  configObj.toStringWithId(), parentBranchId, BranchManager.getType(branch).getName()));
         }
      } catch (BranchDoesNotExist ex) {
         results.log("validateBranchId",
            String.format("Error: Config Object %s has Parent Branch Id attribute [%s] that does not exist.",
               configObj.toStringWithId(), parentBranchId));
      } catch (Exception ex) {
         results.log("validateBranchId",
            "Error: " + configObj.getName() + " [" + configObj.toStringWithId() + "] exception: " + ex.getLocalizedMessage());
      }
      results.logTestTimeSpent(date, "validateBranchId");
   }

   public static List<Collection<ArtifactId>> loadAtsBranchArtifactIds(XResultData xResultData, IProgressMonitor monitor) {
      if (xResultData == null) {
         xResultData = new XResultData();
      }
      xResultData.log(monitor, "testLoadAllCommonArtifactIds - Started " + DateUtil.getMMDDYYHHMM());
      List<ArtifactId> artIds = getCommonArtifactIds(xResultData);
      if (artIds.isEmpty()) {
         xResultData.error("Error: Artifact load returned 0 artifacts to check");
      }
      xResultData.log(monitor, "testLoadAllCommonArtifactIds - Completed " + DateUtil.getMMDDYYHHMM());
      return Collections.subDivide(artIds, 4000);
   }

   private static List<ArtifactId> getCommonArtifactIds(XResultData xResultData) {
      List<ArtifactId> artIds = new ArrayList<>();
      xResultData.log(null, "getCommonArtifactIds - Started " + DateUtil.getMMDDYYHHMM());
      JdbcStatement chStmt = ConnectionHandler.getStatement();
      try {
         chStmt.runPreparedQuery(SELECT_COMMON_ART_IDS, AtsClientService.get().getAtsBranch());
         while (chStmt.next()) {
            artIds.add(ArtifactId.valueOf(chStmt.getLong(1)));
         }
      } finally {
         chStmt.close();
         xResultData.log(null, "getCommonArtifactIds - Completed " + DateUtil.getMMDDYYHHMM());
      }
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
                  results.log(artifact, "testAtsAttributeValues", "Error: Artifact: " + XResultDataUI.getHyperlink(
                     artifact) + " Types: " + artifact.getArtifactTypeName() + " - Null Attribute");
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
   private static void checkAndResolveDuplicateAttributes(Artifact artifact, boolean fixAttributeValues, ValidateResults results, SkynetTransaction transaction) {
      for (AttributeType attrType : artifact.getAttributeTypesUsed()) {
         if (artifact.isDeleted()) {
            continue;
         }
         int count = artifact.getAttributeCount(attrType);
         if (count > attrType.getMaxOccurrences()) {
            String result = String.format(
               "Error: Artifact: " + XResultDataUI.getHyperlink(
                  artifact) + " Type [%s] AttrType [%s] Max [%d] Actual [%d] Values [%s] ",
               artifact.getArtifactTypeName(), attrType.getName(), attrType.getMaxOccurrences(), count,
               artifact.getAttributesToString(attrType));
            Map<String, Attribute<?>> valuesAttrMap = new HashMap<>();
            GammaId latestGamma = GammaId.valueOf(0);
            StringBuffer fixInfo = new StringBuffer(" - FIX AVAILABLE");
            for (Attribute<?> attr : artifact.getAttributes(attrType)) {
               if (attr.getGammaId().isValid()) {
                  latestGamma = attr.getGammaId();
               }
               String info = String.format("[Gamma [%s] Value [%s]]", attr.getGammaId(), attr.getValue());
               valuesAttrMap.put(info, attr);
               fixInfo.append(info);
            }
            fixInfo.append(" - KEEP Gamma");
            fixInfo.append(latestGamma);
            if (latestGamma.isValid()) {
               result += fixInfo;
               if (fixAttributeValues) {
                  for (Attribute<?> attr : artifact.getAttributes(attrType)) {
                     if (attr.getGammaId().notEqual(latestGamma)) {
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
            if (artifact.isOfType(AtsArtifactTypes.Action) && artifact.getRelatedArtifactsCount(
               AtsRelationTypes.ActionToWorkflow_WorkFlow) == 0) {
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
                  results.log(artifact, "testAtsWorkflowsHaveAction", "Error: Team " + XResultDataUI.getHyperlink(
                     artifact) + " has " + actionCount + " parent Action, should be 1\n");
               }
            } catch (Exception ex) {
               results.log(artifact, "testAtsWorkflowsHaveAction",
                  "Error: Team " + artifact.getName() + " has no parent Action: exception " + ex);
            }
         }
      }
      results.logTestTimeSpent(date, "testAtsWorkflowsHaveAction");
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
               IAtsVersion verArt = AtsClientService.get().getVersionService().getTargetedVersion(teamArt);
               if (verArt != null && teamArt.getTeamDefinition().getTeamDefinitionHoldingVersions() != null) {
                  if (!teamArt.getTeamDefinition().getTeamDefinitionHoldingVersions().getVersions().contains(verArt)) {
                     results.log(artifact, "testAtsWorkflowsValidVersion",
                        "Error: Team workflow " + XResultDataUI.getHyperlink(
                           teamArt) + " has version" + XResultDataUI.getHyperlink(
                              artifact) + " that does not belong to teamDefHoldingVersions" + XResultDataUI.getHyperlink(
                                 AtsClientService.get().getQueryServiceClient().getArtifact(
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
                  results.log(artifact, "testTasksHaveParentWorkflow",
                     "Error: Task " + XResultDataUI.getHyperlink(
                        taskArtifact) + " has " + taskArtifact.getRelatedArtifacts(
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
                  AtsClientService.get().getActionableItemService().getActionableItemById(artifact);
               if (aia.isActionable() && TeamDefinitions.getImpactedTeamDefs(Arrays.asList(aia)).isEmpty()) {
                  results.log(artifact, "testActionableItemToTeamDefinition",
                     "Error: ActionableItem " + XResultDataUI.getHyperlink(artifact.getName(),
                        artifact) + " has no related IAtsTeamDefinition and is set to Actionable");
               }
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
            results.log(artifact, "testActionableItemToTeamDefinition",
               "Error: Exception: " + ex.getLocalizedMessage());
         }
      }
      results.logTestTimeSpent(date, "testActionableItemToTeamDefinition");

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
               if (reviewArtifact.getRelatedArtifactsCount(
                  AtsRelationTypes.TeamWorkflowToReview_Team) == 0 && !AtsClientService.get().getWorkItemService().getActionableItemService().hasActionableItems(
                     reviewArtifact)) {
                  results.log(artifact, "testReviewsHaveParentWorkflowOrActionableItems",
                     "Error: Review " + XResultDataUI.getHyperlink(
                        reviewArtifact) + " has 0 related parents and 0 actionable items.");
               }
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
            results.log(artifact, "testTeamDefinitionHasWorkflow", "Error: Exception: " + ex.getLocalizedMessage());
         }
      }
      results.logTestTimeSpent(date, "testReviewsHaveParentWorkflowOrActionableItems");
   }

   public void setFixAttributeValues(boolean fixAttributeValues) {
      this.fixAttributeValues = fixAttributeValues;
   }

   public void setEmailOnComplete(String emailOnComplete) {
      this.emailOnComplete = emailOnComplete;
   }

}
