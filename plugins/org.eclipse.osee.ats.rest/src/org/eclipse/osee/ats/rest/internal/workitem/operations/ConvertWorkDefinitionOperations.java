/*********************************************************************
 * Copyright (c) 2018 Boeing
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

package org.eclipse.osee.ats.rest.internal.workitem.operations;

import static org.eclipse.osee.ats.api.data.AtsTypeTokenProvider.ats;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeString;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * Methods in support of converting from computed work definitions or string attribute work definition names to Artifact
 * Referenced Attributes
 *
 * @author Donald G. Dunne
 */
public class ConvertWorkDefinitionOperations {

   private final AtsApi atsApi;
   private final OrcsApi orcsApi;

   // Types would need to be moved back temporarily to AtsAttributeTypes to be loaded correctly
   AttributeTypeString WorkflowDefinition = ats.createString(1152921504606847149L, "ats.Workflow Definition",
      MediaType.TEXT_PLAIN, "Specific work flow definition id used by this Workflow artifact");
   AttributeTypeString RelatedPeerWorkflowDefinition =
      ats.createString(1152921504606847870L, "ats.Related Peer Workflow Definition", MediaType.TEXT_PLAIN,
         "Specific work flow definition id used by Peer To Peer Reviews for this Team");
   AttributeTypeString RelatedTaskWorkflowDefinition =
      ats.createString(1152921504606847152L, "ats.Related Task Workflow Definition", MediaType.TEXT_PLAIN,
         "Specific work flow definition id used by Tasks related to this Workflow");

   public ConvertWorkDefinitionOperations(AtsApi atsApi, OrcsApi orcsApi) {
      this.atsApi = atsApi;
      this.orcsApi = orcsApi;
   }

   public void convert(XResultData rd) {
      convertMissingWorkItemWorkDefRefAttribute(rd);
      convertMissingWorkItemWorkDefNameAttribute(rd);
      convertMissingWorkItemWorkDefAttribute(rd);
      convertMissingTeamDefinitionWorkDefAttributes(rd);
      convertMissingTeamWorkFlowWorkDefAttributes(rd);
   }

   private void convertMissingWorkItemWorkDefNameAttribute(XResultData rd) {
      List<ArtifactId> artIdList = new LinkedList<>();
      artIdList.addAll(
         atsApi.getQueryService().createQuery(WorkItemType.WorkItem).andAttr(AtsAttributeTypes.CurrentStateType,
            StateType.Working.name()).andNotExists(WorkflowDefinition).getItemIds());
      List<Collection<ArtifactId>> subDivide = Collections.subDivide(artIdList, 500);
      int size = subDivide.size();
      int count = 1;
      int updatedCount = 0;
      for (Collection<ArtifactId> artIds : subDivide) {
         rd.logf(String.format("WorkItem Set: Processing %s / %s\n", count++, size));
         List<Long> ids = new LinkedList<>();
         for (ArtifactId art : artIds) {
            ids.add(art.getId());
         }
         Collection<ArtifactToken> allArtifacts = atsApi.getQueryService().getArtifacts(ids);
         IAtsChangeSet changes = atsApi.createChangeSet("Update Workflow Definition Name");
         for (ArtifactToken art : allArtifacts) {
            IAtsWorkItem workItem = atsApi.getWorkItemService().getWorkItem(art);
            boolean deleted = atsApi.getStoreService().isDeleted(workItem);
            if (deleted) {
               continue;
            }
            ArtifactId workDefArt = atsApi.getAttributeResolver().getSoleAttributeValue(art,
               AtsAttributeTypes.WorkflowDefinitionReference, ArtifactId.SENTINEL);
            if (workDefArt.isValid()) {
               WorkDefinition workDefinition = atsApi.getWorkDefinitionService().getWorkDefinition(workDefArt);
               if (workDefinition == null || workDefinition.isInvalid()) {
                  rd.error(String.format("null Work Definition for %s", workItem.toStringWithId()));
               } else {
                  changes.setSoleAttributeValue(workItem, WorkflowDefinition, workDefinition.getName());
                  updatedCount++;
               }
            }
         }
         changes.executeIfNeeded();
         try {
            Thread.sleep(5 * 1000);
         } catch (InterruptedException ex) {
            // do nothing
         }
      }
      rd.logf("Updtated %s Work Items\n", updatedCount);
   }

   private void convertMissingWorkItemWorkDefAttribute(XResultData rd) {
      List<ArtifactId> artIdList = new LinkedList<>();
      artIdList.addAll(atsApi.getQueryService().createQuery(WorkItemType.WorkItem).andNotExists(
         AtsAttributeTypes.WorkflowDefinitionReference).getItemIds());
      List<Collection<ArtifactId>> subDivide = Collections.subDivide(artIdList, 500);
      int size = subDivide.size();
      int count = 1;
      int updatedCount = 0;
      for (Collection<ArtifactId> artIds : subDivide) {
         rd.logf(String.format("WorkItem Set: Processing %s / %s\n", count++, size));
         List<Long> ids = new LinkedList<>();
         for (ArtifactId art : artIds) {
            ids.add(art.getId());
         }
         Collection<ArtifactToken> allArtifacts = atsApi.getQueryService().getArtifacts(ids);
         IAtsChangeSet changes = atsApi.createChangeSet("Update Workflow Definition Ref");
         for (ArtifactToken art : allArtifacts) {
            IAtsWorkItem workItem = atsApi.getWorkItemService().getWorkItem(art);
            boolean deleted = atsApi.getStoreService().isDeleted(workItem);
            if (deleted) {
               continue;
            }
            ArtifactId workDefArt = atsApi.getAttributeResolver().getSoleAttributeValue(art,
               AtsAttributeTypes.WorkflowDefinitionReference, ArtifactId.SENTINEL);
            if (workDefArt.isInvalid()) {
               WorkDefinition workDefinition = workItem.getWorkDefinition();
               if (workDefinition == null) {
                  rd.error(String.format("null Work Definition for %s", workItem.toStringWithId()));
               } else {
                  changes.setSoleAttributeValue(workItem, AtsAttributeTypes.WorkflowDefinitionReference,
                     workDefinition);
                  updatedCount++;
               }
            }
         }
         changes.executeIfNeeded();
         try {
            Thread.sleep(5 * 1000);
         } catch (InterruptedException ex) {
            // do nothing
         }
      }
      rd.logf("Updtated %s Work Items\n", updatedCount);
   }

   private void convertMissingWorkItemWorkDefRefAttribute(XResultData rd) {
      List<ArtifactId> artIdList = new LinkedList<>();
      artIdList.addAll(atsApi.getQueryService().createQuery(WorkItemType.WorkItem).andNotExists(
         AtsAttributeTypes.WorkflowDefinitionReference).getItemIds());
      List<Collection<ArtifactId>> subDivide = Collections.subDivide(artIdList, 500);
      int size = subDivide.size();
      int count = 1;
      int updatedCount = 0;
      for (Collection<ArtifactId> artIds : subDivide) {
         rd.logf(String.format("WorkItem Set: Processing %s / %s\n", count++, size));
         List<Long> ids = new LinkedList<>();
         for (ArtifactId art : artIds) {
            ids.add(art.getId());
         }
         Collection<ArtifactToken> allArtifacts = atsApi.getQueryService().getArtifacts(ids);
         IAtsChangeSet changes = atsApi.createChangeSet("Update Workflow Definition");
         for (ArtifactToken art : allArtifacts) {
            IAtsWorkItem workItem = atsApi.getWorkItemService().getWorkItem(art);
            boolean deleted = atsApi.getStoreService().isDeleted(workItem);
            if (deleted) {
               continue;
            }
            ArtifactId workDefArt = atsApi.getAttributeResolver().getSoleAttributeValue(art,
               AtsAttributeTypes.WorkflowDefinitionReference, ArtifactId.SENTINEL);
            if (workDefArt.isInvalid()) {
               WorkDefinition workDefinition = workItem.getWorkDefinition();
               if (workDefinition == null) {
                  rd.error(String.format("null Work Definition for %s", workItem.toStringWithId()));
               } else {
                  changes.setSoleAttributeValue(workItem, AtsAttributeTypes.WorkflowDefinitionReference,
                     workDefinition);
                  updatedCount++;
               }
            }
         }
         changes.executeIfNeeded();
         try {
            Thread.sleep(5 * 1000);
         } catch (InterruptedException ex) {
            // do nothing
         }
      }
      rd.logf("Updtated %s Work Items\n", updatedCount);
   }

   /**
    * Converts Team Workflows for Related Task Work Definition</br>
    */
   private void convertMissingTeamWorkFlowWorkDefAttributes(XResultData rd) {
      int updatedCount = 0;
      int count = 0;
      IAtsChangeSet changes = atsApi.createChangeSet("Update Team Workflow Related Task WorkDef");
      Collection<IAtsWorkItem> workItems = atsApi.getQueryService().createQuery(WorkItemType.TeamWorkflow).andExists(
         RelatedTaskWorkflowDefinition).getItems();
      int size = workItems.size();
      for (IAtsWorkItem workItem : workItems) {
         boolean deleted = atsApi.getStoreService().isDeleted(workItem);
         if (deleted) {
            continue;
         }
         rd.logf(String.format("TeamWfs TaskRel: Processing WorkItem WorkDef Set %s / %s", count++, size));
         updatedCount = setNewWorkDefRefIfNecessary(rd, updatedCount, changes, workItem.getStoreObject(),
            RelatedTaskWorkflowDefinition, AtsAttributeTypes.RelatedTaskWorkflowDefinitionReference);
      }
      changes.executeIfNeeded();
      rd.logf("Updtated %s Work Items for Related Task Work Def\n", updatedCount);
   }

   /**
    * Converts Team Definition Work Definition attributes including </br>
    * - Work Definition </br>
    * - Related Task Work Definition </br>
    * - Related Peer Work Definition </br>
    */
   private void convertMissingTeamDefinitionWorkDefAttributes(XResultData rd) {

      Map<AttributeTypeToken, AttributeTypeToken> oldAttrTypeToNewTypeMap = new HashMap<>();
      oldAttrTypeToNewTypeMap.put(WorkflowDefinition, AtsAttributeTypes.WorkflowDefinitionReference);
      oldAttrTypeToNewTypeMap.put(RelatedTaskWorkflowDefinition,
         AtsAttributeTypes.RelatedTaskWorkflowDefinitionReference);
      oldAttrTypeToNewTypeMap.put(RelatedPeerWorkflowDefinition,
         AtsAttributeTypes.RelatedPeerWorkflowDefinitionReference);

      Set<ArtifactToken> artifacts = new HashSet<>();
      for (Entry<AttributeTypeToken, AttributeTypeToken> entry : oldAttrTypeToNewTypeMap.entrySet()) {
         artifacts.addAll(orcsApi.getQueryFactory().fromBranch(atsApi.getAtsBranch()).andIsOfType(
            AtsArtifactTypes.TeamDefinition).andExists(entry.getKey()).getResults().getList());
      }

      int updatedCount = 0;
      IAtsChangeSet changes = atsApi.createChangeSet("Update Team Def Workflow Definition");
      for (ArtifactToken teamDefArt : artifacts) {
         boolean deleted = atsApi.getStoreService().isDeleted(teamDefArt);
         if (deleted) {
            continue;
         }
         for (Entry<AttributeTypeToken, AttributeTypeToken> entry : oldAttrTypeToNewTypeMap.entrySet()) {
            AttributeTypeToken oldAttrType = entry.getKey();
            AttributeTypeToken newAttrType = entry.getValue();
            updatedCount = setNewWorkDefRefIfNecessary(rd, updatedCount, changes, teamDefArt, oldAttrType, newAttrType);
         }
      }
      changes.executeIfNeeded();
      rd.logf("Updtated %s Team Def Attributes\n", updatedCount);
      System.gc();
   }

   private int setNewWorkDefRefIfNecessary(XResultData rd, int updatedCount, IAtsChangeSet changes, ArtifactToken artifact, AttributeTypeToken oldAttrType, AttributeTypeToken newAttrType) {
      String oldWorkDefName = atsApi.getAttributeResolver().getSoleAttributeValue(artifact, oldAttrType, "");
      if (Strings.isValid(oldWorkDefName)) {

         ArtifactId newWorkDefArt =
            atsApi.getAttributeResolver().getSoleAttributeValue(artifact, newAttrType, ArtifactId.SENTINEL);

         if (newWorkDefArt.isInvalid()) {
            WorkDefinition workDefinition = atsApi.getWorkDefinitionService().getWorkDefinitionByName(oldWorkDefName);
            if (workDefinition == null || workDefinition.isInvalid()) {
               rd.error(String.format("null/invalid Work Definition for work def name [%s] and art %s", oldWorkDefName,
                  artifact));
            } else {
               changes.setSoleAttributeValue(artifact, newAttrType, workDefinition);
               updatedCount++;
            }
         }
      }
      return updatedCount;
   }

}
