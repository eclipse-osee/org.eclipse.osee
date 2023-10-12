/*********************************************************************
 * Copyright (c) 2023 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.testscript.internal;

import java.io.InputStream;
import java.util.LinkedList;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.orcs.rest.model.transaction.AddRelation;
import org.eclipse.osee.orcs.rest.model.transaction.CreateArtifact;
import org.eclipse.osee.orcs.rest.model.transaction.TransactionBuilderData;
import org.eclipse.osee.testscript.ScriptDefApi;
import org.eclipse.osee.testscript.TmoImportApi;

/**
 * @author Ryan T. Baldwin
 */
public class TmoImportApiImpl implements TmoImportApi {

   private final ScriptDefApi scriptDefApi;
   private int keyIndex = 0;

   public TmoImportApiImpl(ScriptDefApi scriptDefApi) {
      this.scriptDefApi = scriptDefApi;
   }

   @Override
   public ScriptDefToken getScriptDefinition(InputStream stream) {
      return new TmoReader().getScriptDefinition(stream);
   }

   @Override
   public TransactionBuilderData getTxBuilderData(BranchId branch, ScriptDefToken scriptDef) {
      return this.getTxBuilderData(branch, new TransactionBuilderData(), scriptDef);
   }

   @Override
   public TransactionBuilderData getTxBuilderData(BranchId branch, TransactionBuilderData data,
      ScriptDefToken scriptDef) {
      return this.getTxBuilderData(branch, data, scriptDef, true);
   }

   @Override
   public TransactionBuilderData getTxBuilderData(BranchId branch, TransactionBuilderData data,
      ScriptDefToken scriptDef, boolean reset) {

      data.setBranch(branch.getIdString());
      data.setTxComment("TMO Import");

      if (data.getCreateArtifacts() == null || reset) {
         data.setCreateArtifacts(new LinkedList<>());
      }
      if (data.getAddRelations() == null || reset) {
         data.setAddRelations(new LinkedList<>());
      }
      if (reset) {
         keyIndex = 0;
      }

      ////// Test Script Definition //////
      // If there is an existing definition for this script, do not create a new one.
      String scriptDefKey;
      ScriptDefToken existingDef =
         this.scriptDefApi.getAllByFilter(branch, scriptDef.getName()).stream().findFirst().orElse(
            ScriptDefToken.SENTINEL);
      if (existingDef.isValid()) {
         scriptDefKey = existingDef.getArtifactId().getIdString();
      } else {
         scriptDefKey = getKey();
         CreateArtifact scriptDefArtifact = scriptDef.createArtifact(scriptDefKey);
         data.getCreateArtifacts().add(scriptDefArtifact);
      }

      ////// Test Script Result //////
      ScriptResultToken scriptResult = scriptDef.getScriptResults().get(0);
      scriptResult.setName(scriptDef.getName() + " Result " + scriptResult.getExecutionDate());
      CreateArtifact scriptResultArtifact = scriptResult.createArtifact(getKey());
      data.getCreateArtifacts().add(scriptResultArtifact);
      data.getAddRelations().add(createAddRelation(CoreRelationTypes.TestScriptDefToTestScriptResults, scriptDefKey,
         scriptResultArtifact.getKey()));

      // Result logs
      for (ScriptLogToken log : scriptResult.getLogs()) {
         log.setName(scriptResult.getName() + " " + log.getLogLevel());
         CreateArtifact logArt = log.createArtifact(getKey());
         data.getCreateArtifacts().add(logArt);
         data.getAddRelations().add(createAddRelation(CoreRelationTypes.TestScriptResultsToScriptLog,
            scriptResultArtifact.getKey(), logArt.getKey()));
      }

      // Result Attention Messages
      for (AttentionLocationToken token : scriptResult.getAttentionMessages()) {
         token.setName(scriptResult.getName() + " Attention");
         CreateArtifact attnArt = token.createArtifact(getKey());
         data.getCreateArtifacts().add(attnArt);
         data.getAddRelations().add(createAddRelation(CoreRelationTypes.TestScriptResultsToAttentionMessage,
            scriptResultArtifact.getKey(), attnArt.getKey()));
      }

      // Result Version Information
      for (VersionInformationToken version : scriptResult.getVersionInformation()) {
         version.setName(scriptResult.getName() + " " + version.getName());
         CreateArtifact versionArt = version.createArtifact(getKey());
         data.getCreateArtifacts().add(versionArt);
         data.getAddRelations().add(
            createAddRelation(CoreRelationTypes.TestScriptResultsToVersionInformation_VersionInformation,
               scriptResultArtifact.getKey(), versionArt.getKey()));
      }

      // Result Logging Summaries
      for (LoggingSummaryToken summary : scriptResult.getLoggingSummaries()) {
         summary.setName(scriptResult.getName() + " Logging Summary");
         CreateArtifact summaryArt = summary.createArtifact(getKey());
         data.getCreateArtifacts().add(summaryArt);
         data.getAddRelations().add(
            createAddRelation(CoreRelationTypes.TestScriptResultsToLoggingSummary_LoggingSummary,
               scriptResultArtifact.getKey(), summaryArt.getKey()));

         // Error Entries
         for (ErrorEntryToken errorEntry : summary.getErrorEntries()) {
            errorEntry.setName(summary.getName() + " " + errorEntry.getErrorSeverity() + " Error");
            CreateArtifact errorArt = errorEntry.createArtifact(getKey());
            data.getCreateArtifacts().add(errorArt);
            data.getAddRelations().add(createAddRelation(CoreRelationTypes.LoggingSummaryToErrorEntry_ErrorEntry,
               summaryArt.getKey(), errorArt.getKey()));
         }
      }

      ////// Test Cases //////
      for (TestCaseToken testCase : scriptResult.getTestCases()) {
         CreateArtifact testCaseArt = testCase.createArtifact(getKey());
         data.getCreateArtifacts().add(testCaseArt);
         data.getAddRelations().add(createAddRelation(CoreRelationTypes.TestScriptResultsToTestCase_TestCase,
            scriptResultArtifact.getKey(), testCaseArt.getKey()));

         // Test Case Attention Messages
         for (AttentionLocationToken attention : testCase.getAttentionMessages()) {
            attention.setName(testCase.getName() + " Attention");
            CreateArtifact attnArt = attention.createArtifact(getKey());
            data.getCreateArtifacts().add(attnArt);
            data.getAddRelations().add(
               createAddRelation(CoreRelationTypes.TestCaseToAttentionLocation_AttentionLocation, testCaseArt.getKey(),
                  attnArt.getKey()));
         }

         // Test Case Logs
         for (ScriptLogToken log : testCase.getLogs()) {
            log.setName(testCase.getName() + " " + log.getLogLevel());
            CreateArtifact logArt = log.createArtifact(getKey());
            data.getCreateArtifacts().add(logArt);
            data.getAddRelations().add(createAddRelation(CoreRelationTypes.TestCaseToScriptLog_ScriptLog,
               testCaseArt.getKey(), logArt.getKey()));
         }

         // Test Case Trace
         for (TraceToken trace : testCase.getTrace()) {
            trace.setName(testCase.getName() + " Trace");
            CreateArtifact traceArt = trace.createArtifact(getKey());
            data.getCreateArtifacts().add(traceArt);
            data.getAddRelations().add(
               createAddRelation(CoreRelationTypes.TestCaseToTrace_Trace, testCaseArt.getKey(), traceArt.getKey()));

            // Trace Logs
            for (ScriptLogToken log : trace.getLogs()) {
               log.setName(trace.getName() + " " + log.getLogLevel());
               CreateArtifact logArt = log.createArtifact(getKey());
               data.getCreateArtifacts().add(logArt);
               data.getAddRelations().add(
                  createAddRelation(CoreRelationTypes.TraceToScriptLog_ScriptLog, traceArt.getKey(), logArt.getKey()));
            }
         }

         ////// Test Points //////
         for (TestPointToken testPoint : testCase.getTestPoints()) {
            CreateArtifact testPointArt = testPoint.createArtifact(getKey());
            data.getCreateArtifacts().add(testPointArt);
            data.getAddRelations().add(createAddRelation(CoreRelationTypes.TestCaseToTestPoint_TestPoint,
               testCaseArt.getKey(), testPointArt.getKey()));

            // Test Point Locations
            for (AttentionLocationToken location : testPoint.getLocations()) {
               location.setName(testPoint.getName() + " Location " + location.getLocationId());
               CreateArtifact locationArt = location.createArtifact(getKey());
               data.getCreateArtifacts().add(locationArt);
               data.getAddRelations().add(
                  createAddRelation(CoreRelationTypes.TestPointToAttentionLocation_AttentionLocation,
                     testPointArt.getKey(), locationArt.getKey()));

               // Test Point Location Stack Traces
               for (StackTraceToken stackTrace : location.getStackTraces()) {
                  stackTrace.setName(location.getName() + " " + stackTrace.getSource());
                  CreateArtifact stackTraceArt = stackTrace.createArtifact(getKey());
                  data.getCreateArtifacts().add(stackTraceArt);
                  data.getAddRelations().add(
                     createAddRelation(CoreRelationTypes.AttentionLocationToStackTrace_StackTrace, locationArt.getKey(),
                        stackTraceArt.getKey()));
               }
            }

            // Test Point Info Groups
            for (InfoGroupToken infoGroup : testPoint.getInfoGroups()) {
               CreateArtifact infoGroupArt = infoGroup.createArtifact(getKey());
               data.getCreateArtifacts().add(infoGroupArt);
               data.getAddRelations().add(createAddRelation(CoreRelationTypes.TestPointToInfoGroup_InfoGroup,
                  testPointArt.getKey(), infoGroupArt.getKey()));

               // Info
               for (InfoToken info : infoGroup.getInfo()) {
                  CreateArtifact infoArt = info.createArtifact(getKey());
                  data.getCreateArtifacts().add(infoArt);
                  data.getAddRelations().add(createAddRelation(CoreRelationTypes.InfoGroupToInfo_Info,
                     infoGroupArt.getKey(), infoArt.getKey()));
               }
            }

            // Sub Test Points
            createSubTestPointArtifacts(data, testPoint, testPointArt.getKey());
         }
      }

      return data;
   }

   private void createSubTestPointArtifacts(TransactionBuilderData data, TestPointToken testPoint,
      String testPointKey) {
      for (TestPointToken subTestPoint : testPoint.getSubTestPoints()) {
         CreateArtifact subTestPointArt = subTestPoint.createArtifact(getKey());
         data.getCreateArtifacts().add(subTestPointArt);
         data.getAddRelations().add(createAddRelation(CoreRelationTypes.TestPointGroupToTestPoint_TestPoint,
            testPointKey, subTestPointArt.getKey()));

         // Test Point Locations
         for (AttentionLocationToken location : testPoint.getLocations()) {
            location.setName(testPoint.getName() + " Location " + location.getLocationId());
            CreateArtifact locationArt = location.createArtifact(getKey());
            data.getCreateArtifacts().add(locationArt);
            data.getAddRelations().add(
               createAddRelation(CoreRelationTypes.TestPointToAttentionLocation_AttentionLocation,
                  subTestPointArt.getKey(), locationArt.getKey()));

            // Test Point Location Stack Traces
            for (StackTraceToken stackTrace : location.getStackTraces()) {
               stackTrace.setName(location.getName() + " " + stackTrace.getSource());
               CreateArtifact stackTraceArt = stackTrace.createArtifact(getKey());
               data.getCreateArtifacts().add(stackTraceArt);
               data.getAddRelations().add(createAddRelation(CoreRelationTypes.AttentionLocationToStackTrace_StackTrace,
                  locationArt.getKey(), stackTraceArt.getKey()));
            }
         }

         // Test Point Info Groups
         for (InfoGroupToken infoGroup : testPoint.getInfoGroups()) {
            CreateArtifact infoGroupArt = infoGroup.createArtifact(getKey());
            data.getCreateArtifacts().add(infoGroupArt);
            data.getAddRelations().add(createAddRelation(CoreRelationTypes.TestPointToInfoGroup_InfoGroup,
               subTestPointArt.getKey(), infoGroupArt.getKey()));

            // Info
            for (InfoToken info : infoGroup.getInfo()) {
               CreateArtifact infoArt = info.createArtifact(getKey());
               data.getCreateArtifacts().add(infoArt);
               data.getAddRelations().add(
                  createAddRelation(CoreRelationTypes.InfoGroupToInfo_Info, infoGroupArt.getKey(), infoArt.getKey()));
            }
         }

         // Sub Test Points
         createSubTestPointArtifacts(data, subTestPoint, subTestPointArt.getKey());
      }
   }

   private String getKey() {
      keyIndex++;
      return "key" + keyIndex;
   }

   private AddRelation createAddRelation(RelationTypeToken relType, String artAId, String artBId) {
      AddRelation rel = new AddRelation();
      rel.setTypeId(relType.getIdString());
      rel.setaArtId(artAId);
      rel.setbArtId(artBId);
      return rel;
   }

}
