/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.config;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.ev.IAtsWorkPackage;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.core.util.ConvertAtsConfigGuidAttributesOperations;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.util.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * See ConvertAtsConfigGuidAttributes for explanation of what this operation does.
 *
 * @author Donald G. Dunne
 */
public class ConvertAtsConfigGuidAttributesOperation {

   private final IAtsServices services;

   private final AttributeTypeToken WorkPackageGuid = AtsAttributeTypes.createType(1152921504606847876L,
      "Work Package Guid", "Work Package for this Team Workflow, Review, Task or Goal");

   public ConvertAtsConfigGuidAttributesOperation(IAtsServices services) {
      this.services = services;
   }

   public XResultData createUpdateConfig(XResultData rd) {

      services.getLogger().error("starting");

      // Convert Team Def and AIs for Team Workflow
      List<ArtifactId> artIdList = new LinkedList<>();
      artIdList.addAll(services.getQueryService().createQuery(WorkItemType.TeamWorkflow).andNotExists(
         AtsAttributeTypes.TeamDefinitionReference).getItemIds());
      artIdList.addAll(services.getQueryService().createQuery(WorkItemType.TeamWorkflow).andNotExists(
         ConvertAtsConfigGuidAttributesOperations.TeamDefinition).getItemIds());
      List<Collection<ArtifactId>> subDivide = Collections.subDivide(artIdList, 2000);
      int size = subDivide.size(), count = 1;
      for (Collection<ArtifactId> artIds : subDivide) {
         services.getLogger().info(String.format("processing %s / %s", count++, size));
         List<Long> ids = new LinkedList<>();
         for (ArtifactId art : artIds) {
            ids.add(art.getId());
         }
         Collection<ArtifactToken> allArtifacts = services.getArtifacts(ids);
         IAtsChangeSet changes = services.createChangeSet("Update TeamDef, AI and WorkPkg TeamWf GUIDs");
         for (ArtifactToken art : allArtifacts) {
            ConvertAtsConfigGuidAttributesOperations.convertTeamDefinitionIfNeeded(changes, art, services);
            ConvertAtsConfigGuidAttributesOperations.convertActionableItemsIfNeeded(changes, art, services);
            services.getLogger().error("Work Item - " + art.toStringWithId());
         }
         TransactionId transaction = changes.executeIfNeeded();
         if (transaction != null && transaction.isValid()) {
            services.getLogger().info("================================== > executed");
         }
         try {
            Thread.sleep(5 * 1000);
         } catch (InterruptedException ex) {
            // do nothing
         }
      }

      // convert work packages
      List<ArtifactId> artIdList2 = new LinkedList<>();
      artIdList2.addAll(getWorkItemIdsMissingWorkPacakge());
      List<Collection<ArtifactId>> subDivide2 = Collections.subDivide(artIdList2, 2000);
      int size2 = subDivide2.size(), count2 = 1;
      for (Collection<ArtifactId> artIds : subDivide2) {
         services.getLogger().info(String.format("processing %s / %s", count2++, size2));
         List<Long> ids = new LinkedList<>();
         for (ArtifactId art : artIds) {
            ids.add(art.getId());
         }
         Collection<ArtifactToken> allArtifacts = services.getArtifacts(ids);
         IAtsChangeSet changes = services.createChangeSet("Update Work Package GUIDs");
         for (ArtifactToken art : allArtifacts) {
            convertWorkPackageIfNeeded(changes, art);
         }
         TransactionId transaction = changes.executeIfNeeded();
         if (transaction != null && transaction.isValid()) {
            services.getLogger().info("================================== > executed");
         }
         try {
            Thread.sleep(5 * 1000);
         } catch (InterruptedException ex) {
            // do nothing
         }
      }

      // Convert Program Team Definition
      IAtsChangeSet changes = services.createChangeSet("Update Program Team Def GUID");
      for (IAtsProgram program : services.getQueryService().createQuery(AtsArtifactTypes.Program).getItems(
         IAtsProgram.class)) {
         ConvertAtsConfigGuidAttributesOperations.convertTeamDefinitionIfNeeded(changes, program.getStoreObject(),
            services);
      }
      changes.executeIfNeeded();

      // Delete AIs for Action, they shouldn't have them
      changes = services.createChangeSet("Remove Action AI and TeamDef GUIDs");
      for (ArtifactToken actionArt : services.getQueryService().getArtifacts(AtsArtifactTypes.Action,
         services.getAtsBranch())) {
         changes.deleteAttributes(actionArt, ConvertAtsConfigGuidAttributesOperations.TeamDefinition);
         changes.deleteAttributes(actionArt, ConvertAtsConfigGuidAttributesOperations.ActionableItem);
      }
      changes.executeIfNeeded();

      // Convert AIs for ats.Review (stand-alone reviews)
      changes = services.createChangeSet("Update AIs for ats.Review GUIDs");
      for (IAtsAbstractReview program : services.getQueryService().createQuery(WorkItemType.Review).getItems(
         IAtsAbstractReview.class)) {
         ConvertAtsConfigGuidAttributesOperations.convertActionableItemsIfNeeded(changes, program.getStoreObject(),
            services);
      }
      changes.executeIfNeeded();
      services.getLogger().error("complete");
      return rd;
   }

   private Set<ArtifactId> getWorkItemIdsMissingWorkPacakge() {
      Collection<ArtifactId> haveWorkPackageGuid = services.getQueryService().getArtifactIdsFromQuery(
         "SELECT UNIQUE attr.art_id FROM OSEE_ATTRIBUTE attr, OSEE_TXS txs WHERE attr.GAMMA_ID = txs.GAMMA_ID AND " //
            + "txs.BRANCH_ID = 570 AND txs.TX_CURRENT = 1 AND attr.ATTR_TYPE_ID = 1152921504606847876");

      Collection<ArtifactId> haveWorkPackageUuid = services.getQueryService().getArtifactIdsFromQuery(
         "SELECT UNIQUE attr.art_id FROM OSEE_ATTRIBUTE attr, OSEE_TXS txs WHERE attr.GAMMA_ID = txs.GAMMA_ID AND " //
            + "txs.BRANCH_ID = 570 AND txs.TX_CURRENT = 1 AND attr.ATTR_TYPE_ID = 473096133909456789");

      Set<ArtifactId> missingWorkPackage = new HashSet<>();
      missingWorkPackage.addAll(Collections.setComplement(haveWorkPackageGuid, haveWorkPackageUuid));
      missingWorkPackage.addAll(Collections.setComplement(haveWorkPackageUuid, haveWorkPackageGuid));
      return missingWorkPackage;
   }

   boolean workPackagesLoaded = false;

   private void convertWorkPackageIfNeeded(IAtsChangeSet changes, ArtifactToken workItemArt) {
      if (!workPackagesLoaded) {
         for (ArtifactToken workPackageArt : services.getQueryService().getArtifacts(AtsArtifactTypes.WorkPackage,
            services.getAtsBranch())) {
            IAtsWorkPackage workPkg = services.getEarnedValueService().getWorkPackage(workPackageArt);
            guidToWorkPackage.put(services.getStoreService().getGuid(workPkg), workPkg);
         }
         workPackagesLoaded = true;
      }
      // convert guid to id
      ArtifactId workPackageId = services.getAttributeResolver().getSoleArtifactIdReference(workItemArt,
         AtsAttributeTypes.WorkPackageReference, ArtifactId.SENTINEL);
      if (workPackageId.isInvalid()) {
         String workPackageGuid =
            services.getAttributeResolver().getSoleAttributeValue(workItemArt, WorkPackageGuid, "");
         if (Strings.isValid(workPackageGuid)) {
            IAtsWorkPackage workPackage = guidToWorkPackage.get(workPackageGuid);
            if (workPackage == null) {
               services.getLogger().error(String.format("Work Package null for guid %s; deleting attribute for %s",
                  workPackageGuid, workItemArt.toStringWithId()));
               changes.deleteAttributes(workItemArt, WorkPackageGuid);
               return;
            } else {
               changes.setSoleAttributeValue(workItemArt, AtsAttributeTypes.WorkPackageReference,
                  workPackage.getStoreObject());
            }
         }
      }
      // convert id to guid
      String workPackageGuid = services.getAttributeResolver().getSoleAttributeValue(workItemArt, WorkPackageGuid, "");
      if (Strings.isInValid(workPackageGuid)) {
         ArtifactId workPackageArt = services.getAttributeResolver().getSoleArtifactIdReference(workItemArt,
            AtsAttributeTypes.WorkPackageReference, ArtifactId.SENTINEL);
         ArtifactReadable artifact = (ArtifactReadable) services.getArtifact(workPackageArt);
         if (artifact != null) {
            changes.setSoleAttributeValue(workItemArt, WorkPackageGuid, artifact.getGuid());
         }
      }
   }

   java.util.Map<String, IAtsWorkPackage> guidToWorkPackage = new HashMap<>();

}
