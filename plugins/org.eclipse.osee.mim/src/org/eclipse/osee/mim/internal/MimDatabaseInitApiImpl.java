/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.mim.internal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.ActionResult;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.NewActionData;
import org.eclipse.osee.ats.api.workflow.NewActionResult;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionResult;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.mim.MimApi;
import org.eclipse.osee.mim.MimDatabaseInitApi;
import org.eclipse.osee.mim.MimDemoBranches;
import org.eclipse.osee.mim.types.InterfaceConnection;
import org.eclipse.osee.mim.types.InterfaceElementImportToken;
import org.eclipse.osee.mim.types.InterfaceEnumOrdinalType;
import org.eclipse.osee.mim.types.InterfaceEnumeration;
import org.eclipse.osee.mim.types.InterfaceEnumerationSet;
import org.eclipse.osee.mim.types.InterfaceMessageToken;
import org.eclipse.osee.mim.types.InterfaceNode;
import org.eclipse.osee.mim.types.InterfaceStructureToken;
import org.eclipse.osee.mim.types.InterfaceSubMessageToken;
import org.eclipse.osee.mim.types.MIMImportUtil;
import org.eclipse.osee.mim.types.MimImportSummary;
import org.eclipse.osee.mim.types.PlatformTypeImportToken;
import org.eclipse.osee.mim.types.TransportType;
import org.eclipse.osee.orcs.rest.model.transaction.TransactionBuilderData;
import org.eclipse.osee.orcs.rest.model.transaction.TransactionBuilderDataFactory;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

public class MimDatabaseInitApiImpl implements MimDatabaseInitApi {
   private final MimApi mimApi;

   public MimDatabaseInitApiImpl(MimApi mimApi) {
      this.mimApi = mimApi;
   }

   @Override
   public NewActionResult createDemoBranches() {
      NewActionData data = new NewActionData();
      data.setAiIds(Arrays.asList(DemoArtifactToken.SAW_PL_MIM_AI.getIdString()));
      data.setAsUserId(DemoUsers.Joe_Smith.getIdString());
      data.setChangeType(ChangeTypes.Improvement);
      data.setCreatedByUserId(DemoUsers.Joe_Smith.getIdString());
      data.setDescription("MIM demo data");
      data.setOriginator(ArtifactId.valueOf(DemoUsers.Joe_Smith.getId()));
      data.setPriority("3");
      data.setTitle("MIM Demo");
      data.setVersionId(ArtifactId.valueOf(DemoArtifactToken.SAW_Product_Line.getId()));

      NewActionResult result = new NewActionResult();
      IAtsChangeSet changes = mimApi.getAtsApi().createChangeSet(getClass().getSimpleName());
      ActionResult actionResult = mimApi.getAtsApi().getActionService().createAction(data, changes);

      if (actionResult.getResults().isFailed()) {
         result.getResults().error("Error creating action");
         return result;
      }

      changes.execute();

      IAtsTeamWorkflow teamWf = actionResult.getTeamWfs().iterator().next();
      Branch workingBranch = mimApi.getOrcsApi().getBranchOps().createWorkingBranch(MimDemoBranches.MIM_DEMO,
         DemoBranches.SAW_PL, teamWf.getArtifactId());
      result.setWorkingBranchId(workingBranch);
      return result;
   }

   @Override
   public TransactionResult populateDemoBranch(BranchId branchId) {
      TransactionResult txResult = new TransactionResult();
      XResultData resultData = new XResultData();
      try {
         TransactionBuilderData txData = getDemoTxData(branchId, getDemoImportSummary());
         TransactionBuilderDataFactory txBdf = new TransactionBuilderDataFactory(mimApi.getOrcsApi());
         TransactionBuilder tx = txBdf.loadFromJson((new ObjectMapper()).writeValueAsString(txData));
         TransactionToken token = tx.commit();
         txResult.setTx(token);
         resultData.setIds(
            tx.getTxDataReadables().stream().map(readable -> readable.getIdString()).collect(Collectors.toList()));
      } catch (JsonProcessingException ex) {
         resultData.error("Error processing tx json");
      }
      return txResult;
   }

   @Override
   public MimImportSummary getDemoImportSummary() {
      MimImportSummary summary = new MimImportSummary();

      TransportType transportType = createTransportType();
      summary.getTransportTypes().add(transportType);

      InterfaceConnection connection = createConnection();
      summary.getConnections().add(connection);

      List<InterfaceNode> nodes = createNodes();
      summary.getNodes().addAll(nodes);

      InterfaceMessageToken message = createMessage();
      summary.getMessages().add(message);

      InterfaceSubMessageToken submessage = createSubmessage();
      summary.getSubMessages().add(submessage);

      InterfaceStructureToken structure = createStructure();
      summary.getStructures().add(structure);

      List<InterfaceElementImportToken> elements = createElements();
      summary.getElements().addAll(elements);

      List<PlatformTypeImportToken> pTypes = createPlatformTypes(summary);
      summary.getPlatformTypes().addAll(pTypes);

      summary.getConnectionTransportTypeRelations().put(connection.getIdString(),
         Arrays.asList(transportType.getIdString()));
      summary.getConnectionNodeRelations().put(connection.getIdString(),
         nodes.stream().map(node -> node.getIdString()).collect(Collectors.toList()));
      summary.getConnectionMessageRelations().put(connection.getIdString(), Arrays.asList(message.getIdString()));
      summary.getMessagePublisherNodeRelations().put(message.getIdString(), Arrays.asList(nodes.get(0).getIdString()));
      summary.getMessageSubscriberNodeRelations().put(message.getIdString(), Arrays.asList(nodes.get(1).getIdString()));
      summary.getMessageSubmessageRelations().put(message.getIdString(), Arrays.asList(submessage.getIdString()));
      summary.getSubMessageStructureRelations().put(submessage.getIdString(), Arrays.asList(structure.getIdString()));
      summary.getStructureElementRelations().put(structure.getIdString(),
         elements.stream().map(element -> element.getIdString()).collect(Collectors.toList()));

      // Add element -> platform type relations. This assumes that elements and platform types were created in the right order
      for (int i = 0; i < elements.size(); i++) {
         InterfaceElementImportToken element = elements.get(i);
         PlatformTypeImportToken pType = pTypes.get(i);
         summary.getElementPlatformTypeRelations().put(element.getIdString(), Arrays.asList(pType.getIdString()));
      }

      return summary;
   }

   @Override
   public TransactionBuilderData getDemoTxData() {
      return getDemoTxData(MimDemoBranches.MIM_DEMO, getDemoImportSummary());
   }

   private TransactionBuilderData getDemoTxData(BranchId branchId, MimImportSummary summary) {
      return MIMImportUtil.getTxBuilderDataFromImportSummary(branchId, ApplicabilityId.BASE, summary);
   }

   private TransportType createTransportType() {
      TransportType transportType = new TransportType(getRandomId(), "Demo Transport");
      transportType.setByteAlignValidation(true);
      transportType.setByteAlignValidationSize(8);
      transportType.setMinimumPublisherMultiplicity(1);
      transportType.setMaximumPublisherMultiplicity(1);
      transportType.setMinimumSubscriberMultiplicity(1);
      transportType.setMaximumSubscriberMultiplicity(1);
      transportType.setMessageGeneration(true);
      transportType.setMessageGenerationPosition("0");
      transportType.setMessageGenerationType("Operational");
      return transportType;
   }

   private InterfaceConnection createConnection() {
      InterfaceConnection connection = new InterfaceConnection(getRandomId(), "Demo Connection");
      connection.setDescription("This is a demo connection");
      return connection;
   }

   private List<InterfaceNode> createNodes() {
      List<InterfaceNode> nodes = new LinkedList<>();
      InterfaceNode nodeA = new InterfaceNode(getRandomId(), "Demo Node A");
      InterfaceNode nodeB = new InterfaceNode(getRandomId(), "Demo Node B");

      nodes.add(nodeA);
      nodes.add(nodeB);
      return nodes;
   }

   private InterfaceMessageToken createMessage() {
      InterfaceMessageToken message = new InterfaceMessageToken(getRandomId(), "Message 1");
      message.setInterfaceMessageNumber("1");
      message.setInterfaceMessagePeriodicity("Periodic");
      message.setInterfaceMessageRate("5");
      message.setInterfaceMessageType("Operational");
      message.setInterfaceMessageWriteAccess(true);
      return message;
   }

   private InterfaceSubMessageToken createSubmessage() {
      InterfaceSubMessageToken submessage = new InterfaceSubMessageToken(getRandomId(), "Submessage 1");
      submessage.setInterfaceSubMessageNumber("1");
      return submessage;
   }

   private InterfaceStructureToken createStructure() {
      InterfaceStructureToken structure = new InterfaceStructureToken(getRandomId(), "Structure 1");
      structure.setInterfaceMinSimultaneity("1");
      structure.setInterfaceMaxSimultaneity("1");
      structure.setInterfaceStructureCategory("Miscellaneous");
      structure.setInterfaceTaskFileType(0);
      return structure;
   }

   private List<InterfaceElementImportToken> createElements() {
      List<InterfaceElementImportToken> elements = new LinkedList<>();
      InterfaceElementImportToken element = new InterfaceElementImportToken(getRandomId(), "Integer Element");
      element.setInterfaceElementAlterable(true);
      elements.add(element);

      element = new InterfaceElementImportToken(getRandomId(), "Boolean Element");
      element.setInterfaceElementIndexStart(1);
      element.setInterfaceElementIndexEnd(4);
      elements.add(element);

      element = new InterfaceElementImportToken(getRandomId(), "Float Element");
      elements.add(element);

      element = new InterfaceElementImportToken(getRandomId(), "Demo Fault");
      elements.add(element);

      return elements;
   }

   private List<PlatformTypeImportToken> createPlatformTypes(MimImportSummary summary) {
      List<PlatformTypeImportToken> pTypes = new LinkedList<>();
      PlatformTypeImportToken pType =
         new PlatformTypeImportToken(getRandomId(), "Integer", "unsigned integer", "32", "0", "2^31-1", "", "", "", "");
      pTypes.add(pType);

      pType = new PlatformTypeImportToken(getRandomId(), "Boolean", "boolean", "8", "0", "1", "", "", "", "");
      pTypes.add(pType);

      pType = new PlatformTypeImportToken(getRandomId(), "Float", "float", "32", "", "", "", "", "", "");
      pTypes.add(pType);

      // Create Demo Fault platform type, enum set, and relations
      pType = new PlatformTypeImportToken(getRandomId(), "Demo Fault", "enumeration", "32", "", "", "", "", "", "");
      pTypes.add(pType);
      InterfaceEnumerationSet enumSet = new InterfaceEnumerationSet(getRandomId(), "Demo Fault");
      summary.getEnumSets().add(enumSet);
      summary.getPlatformTypeEnumSetRelations().put(pType.getIdString(), Arrays.asList(enumSet.getIdString()));
      InterfaceEnumeration enumeration = new InterfaceEnumeration(getRandomId(), "Fault 1");
      enumeration.setOrdinal(1L);
      enumeration.setOrdinalType(InterfaceEnumOrdinalType.LONG);
      summary.getEnums().add(enumeration);
      enumeration = new InterfaceEnumeration(getRandomId(), "Fault 2");
      enumeration.setOrdinal(2L);
      enumeration.setOrdinalType(InterfaceEnumOrdinalType.LONG);
      summary.getEnums().add(enumeration);
      enumeration = new InterfaceEnumeration(getRandomId(), "Fault 3");
      enumeration.setOrdinal(3L);
      enumeration.setOrdinalType(InterfaceEnumOrdinalType.LONG);
      summary.getEnums().add(enumeration);
      enumeration = new InterfaceEnumeration(getRandomId(), "Fault 4");
      enumeration.setOrdinal(4L);
      enumeration.setOrdinalType(InterfaceEnumOrdinalType.LONG);
      summary.getEnums().add(enumeration);
      summary.getEnumSetEnumRelations().put(enumSet.getIdString(),
         summary.getEnums().stream().map(e -> e.getIdString()).collect(Collectors.toList()));

      return pTypes;
   }

   private long getRandomId() {
      return Math.abs(UUID.randomUUID().getMostSignificantBits());
   }

}
