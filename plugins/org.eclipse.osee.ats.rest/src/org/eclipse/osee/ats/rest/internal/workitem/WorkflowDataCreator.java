/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.osee.ats.rest.internal.workitem;

import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.BlockedReason;
import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.HoldReason;
import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.Title;
import static org.eclipse.osee.ats.api.workdef.WidgetOption.EDITABLE;
import static org.eclipse.osee.ats.api.workdef.WidgetOption.FILL_VERT;
import static org.eclipse.osee.ats.api.workdef.WidgetOption.NOT_EDITABLE;
import static org.eclipse.osee.ats.api.workdef.WidgetOption.RFT;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ai.ActionableItem;
import org.eclipse.osee.ats.api.config.AtsDisplayHint;
import org.eclipse.osee.ats.api.config.TeamDefinition;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.team.Priorities;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workdef.WidgetOption;
import org.eclipse.osee.ats.api.workdef.model.CompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.model.LayoutItem;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workdef.model.WidgetDefinition;
import org.eclipse.osee.ats.api.workdef.model.WorkDefOption;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.api.workdef.model.web.WfdState;
import org.eclipse.osee.ats.api.workdef.model.web.WfdWidgetComposite;
import org.eclipse.osee.ats.api.workdef.model.web.WfdWidgetDef;
import org.eclipse.osee.ats.api.workdef.model.web.WfeAttributeTypeToken;
import org.eclipse.osee.ats.api.workdef.model.web.WorkflowData;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.WorkflowAttachment;
import org.eclipse.osee.ats.core.workdef.WorkDefUtil;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributePojo;
import org.eclipse.osee.framework.core.data.AttributePojoBaseId;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.AttributeTypeString;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.IAttribute;
import org.eclipse.osee.framework.core.data.Multiplicity;
import org.eclipse.osee.framework.core.data.Multiplicity.MultiplicityToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.HashCollectionSet;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * @author Donald G. Dunne
 */
public class WorkflowDataCreator {

   private final WorkDefinition wd;
   private final AtsApi atsApi;
   private final IAtsWorkItem workItem;
   private final ArtifactTypeToken workItemArtifactType;
   private WorkflowData wfd;
   private final HashCollectionSet<AttributeTypeToken, AttributePojo<?>> attrTypeToAttrPojos =
      new HashCollectionSet<>();

   private final QueryBuilder atsBranchQuery;

   public WorkflowDataCreator(WorkDefinition wd, IAtsWorkItem workItem, AtsApi atsApi, OrcsApi orcsApi) {
      this.wd = wd;
      this.workItem = workItem;
      this.atsApi = atsApi;
      this.workItemArtifactType = atsApi.tokenService().getArtifactType(workItem.getArtifactType().getId());
      this.atsBranchQuery = orcsApi.getQueryFactory().fromBranch(atsApi.getAtsBranch());
   }

   public WorkflowData get() {
      wfd = new WorkflowData(workItem.getId(), workItem.getName());
      addWorkItemDetails();
      addAttributes();
      addAttachments();
      addWorkDefDetails();
      return wfd;
   }

   private void addWorkItemDetails() {
      Iterable<IAttribute<?>> wiAttrs = atsBranchQuery.andId(workItem.getArtifactId()).asArtifact().getAttributesNew();

      wfd.setArtTypeName(workItem.getArtifactTypeName());
      wfd.setArtifactTypeIcon(workItem.getArtifactType().getIcon());
      wfd.setTx(((ArtifactReadable) workItem.getStoreObject()).getTransaction());

      ArtifactId teamDefId = ArtifactId.SENTINEL;
      Collection<ArtifactId> aiIds = new ArrayList<>();

      // Work item attributes
      for (IAttribute<?> attr : wiAttrs) {
         Object type = attr.getAttributeType();

         if (AtsAttributeTypes.CurrentStateName.equals(type)) {
            wfd.setCurrentStateName(AttributePojo.valueOf(attr, workItemArtifactType));
         } else if (AtsAttributeTypes.CurrentStateType.equals(type)) {
            wfd.setCurrentStateType(AttributePojo.valueOf(attr, workItemArtifactType));
         } else if (AtsAttributeTypes.ChangeType.equals(type)) {
            wfd.setChangeType(AttributePojo.valueOf(attr, workItemArtifactType, getChangeTypes()));
         } else if (AtsAttributeTypes.CreatedDate.equals(type)) {
            wfd.setCreatedDate(AttributePojo.valueOf(attr, workItemArtifactType));
         } else if (AtsAttributeTypes.AtsId.equals(type)) {
            wfd.setAtsId(AttributePojo.valueOf(attr, workItemArtifactType));
         } else if (AtsAttributeTypes.LegacyPcrId.equals(type)) {
            wfd.setLegacyPcrId(AttributePojo.valueOf(attr, workItemArtifactType));
         } else if (AtsAttributeTypes.PcrId.equals(type)) {
            wfd.addPcrId(AttributePojo.valueOf(attr, workItemArtifactType));
         } else if (AtsAttributeTypes.Points.equals(type)) {
            wfd.setPoints(AttributePojo.valueOf(attr, workItemArtifactType));
         } else if (AtsAttributeTypes.TeamDefinitionReference.equals(type)) {
            Object v = attr.getValue();
            if (v instanceof ArtifactId) {
               ArtifactId vId = (ArtifactId) v;
               if (vId.isValid()) {
                  teamDefId = vId;
               }
            }
         } else if (AtsAttributeTypes.ActionableItemReference.equals(type)) {
            // Collect AI references
            Object v = attr.getValue();
            if (v instanceof ArtifactId) {
               ArtifactId vId = (ArtifactId) v;
               if (vId.isValid()) {
                  aiIds.add(vId);
               }
            }
         }
      }

      // Team Definition (id + name attribute)
      if (teamDefId != null && teamDefId.isValid()) {
         TeamDefinition teamDef = atsApi.getConfigService().getConfigurations().getIdToTeamDef().get(teamDefId.getId());
         if (teamDef != null) {
            for (IAttribute<?> attr : atsBranchQuery.andId(teamDefId).asArtifact().getAttributeList(
               CoreAttributeTypes.Name)) {
               AttributePojo<?> teamDefNameAttribute = AttributePojo.valueOf(attr, workItemArtifactType);
               AttributePojoBaseId<?> teamDefinition =
                  AttributePojoBaseId.valueOf(teamDefId.getId(), teamDefNameAttribute);
               wfd.setTeamDefinition(teamDefinition);
               break;
            }
         }
      }

      // Actionable Items ((id + name attribute)(s))
      for (ArtifactId aiId : aiIds) {
         ActionableItem ai = atsApi.getConfigService().getConfigurations().getIdToAi().get(aiId.getId());
         if (ai != null) {
            for (IAttribute<?> attr : atsBranchQuery.andId(ai.getArtifactId()).asArtifact().getAttributeList(
               CoreAttributeTypes.Name)) {
               AttributePojo<?> aiNameAttribute = AttributePojo.valueOf(attr, workItemArtifactType);
               AttributePojoBaseId<?> aiNameReference = AttributePojoBaseId.valueOf(ai.getId(), aiNameAttribute);
               wfd.addAiName(aiNameReference);
            }
         }
      }

      // Assignees ((id + name attribute)(s))
      for (AtsUser assignee : workItem.getAssignees()) {
         for (IAttribute<?> attr : atsBranchQuery.andId(assignee.getArtifactId()).asArtifact().getAttributeList(
            CoreAttributeTypes.Name)) {
            AttributePojo<?> assigneeNameAttribute = AttributePojo.valueOf(attr, workItemArtifactType);
            AttributePojoBaseId<?> assigneeNameReference =
               AttributePojoBaseId.valueOf(assignee.getId(), assigneeNameAttribute);
            wfd.addAssigneeName(assigneeNameReference);
         }

      }

      // Originator
      AtsUser originator = workItem.getCreatedBy();
      if (originator != null) {
         for (IAttribute<?> attr : atsBranchQuery.andId(originator.getArtifactId()).asArtifact().getAttributeList(
            CoreAttributeTypes.Name)) {
            AttributePojo<?> originatorNameAttribute = AttributePojo.valueOf(attr, workItemArtifactType);
            AttributePojoBaseId<?> originatorNameReference =
               AttributePojoBaseId.valueOf(originator.getId(), originatorNameAttribute);
            wfd.setOriginator(originatorNameReference);
            break;
         }
      }

      // Parent action artifact attributes
      ArtifactId parentActionArtifactId = workItem.getParentAction().getArtifactId();
      if (parentActionArtifactId.isValid()) {
         for (IAttribute<?> attr : atsBranchQuery.andId(parentActionArtifactId).asArtifact().getAttributeList(
            AtsAttributeTypes.AtsId)) {
            wfd.setActionId(AttributePojo.valueOf(attr, workItemArtifactType));
            break;
         }
      }

      // Parent workflow artifact attributes
      ArtifactId parentWorkflowArtifactId = workItem.getParentTeamWorkflow().getArtifactId();
      if (parentWorkflowArtifactId.isValid()) {
         for (IAttribute<?> attr : atsBranchQuery.andId(parentWorkflowArtifactId).asArtifact().getAttributeList(
            AtsAttributeTypes.AtsId)) {
            wfd.setParentWorkflowAtsId(AttributePojo.valueOf(attr, workItemArtifactType));
            break;
         }
      }

      // Editable
      wfd.setEditable(WorkDefUtil.isEditable(workItem, atsApi));

      // Targeted version
      IAtsVersion ver = atsApi.getVersionService().getTargetedVersion(workItem);
      if (ver != null) {
         ArtifactId targetedVersionArtifactId = ver.getArtifactToken();
         if (targetedVersionArtifactId != null) {
            for (IAttribute<?> attr : atsBranchQuery.andId(targetedVersionArtifactId).asArtifact().getAttributeList(
               CoreAttributeTypes.Name)) {
               AttributePojo<?> targetedVersionNameAttribute = AttributePojo.valueOf(attr, workItemArtifactType);
               AttributePojoBaseId<?> targetedVersionNameReference =
                  AttributePojoBaseId.valueOf(targetedVersionArtifactId.getId(), targetedVersionNameAttribute);
               wfd.setTargetedVersion(targetedVersionNameReference);
               break;
            }

         }
      }
   }

   private void addAttributes() {
      for (IAttribute<?> attr : atsBranchQuery.andId(workItem.getArtifactId()).asArtifact().getAttributesNew()) {
         AttributePojo<?> attrPojo = AttributePojo.valueOf(attr, workItemArtifactType);
         wfd.addAttribute(attrPojo);
         attrTypeToAttrPojos.put(attr.getAttributeType(), attrPojo);
      }
   }

   private void addAttachments() {
      for (ArtifactToken art : atsBranchQuery.andId(workItem.getArtifactId()).follow(
         CoreRelationTypes.SupportingInfo_SupportingInfo).asArtifact().getRelated(
            CoreRelationTypes.SupportingInfo_SupportingInfo)) {
         ArtifactReadable attachmentArt =
            atsBranchQuery.andIsOfType(CoreArtifactTypes.GeneralDocument).andId(art).asArtifact();
         WorkflowAttachment wfAttachment = new WorkflowAttachment(attachmentArt);
         wfd.addAttachment(wfAttachment);
      }
   }

   /*
    * Work Definition Details
    */

   private void addWorkDefDetails() {
      wfd.setWorkDefId(wd.getId());
      wfd.setWorkDefName(wd.getName());
      addWorkDefOptions();
      addStaticHeader();
      addTeamHeader();
      addStates();
      addPriorities();
   }

   private void addWorkDefOptions() {
      for (WorkDefOption opt : wd.getOptions()) {
         wfd.getWorkDefOption().add(opt.name());
      }
   }

   private void addStaticHeader() {
      WfdWidgetComposite hComp = wfd.getHeaderComposite();
      hComp.setColumns(1);

      // Title
      addWidgetDef(hComp, "Title", "XTextDam", "String", Title, workItem.getName(), RFT, EDITABLE);

      // State, Created, Originator
      addStateComposite(hComp);

      // Team Def, Ids
      addTeamComposite(hComp);

      // Version, Assignees
      addVersionComposite(hComp);

      // Blocked, Hold
      addBlockedHoldComposite(hComp);

      addWidgetDef(hComp, "Work Definition", "XLabel", "String", null, workItem.getWorkDefinition().getName(),
         NOT_EDITABLE);

   }

   private WfdWidgetDef addWidgetDef(WfdWidgetComposite wComp, String name, String xWidget, String dataType,
      AttributeTypeString attrType, String displayValue, WidgetOption... widgetOpts) {
      WfdWidgetDef wWidget = new WfdWidgetDef();
      wWidget.setName(name);
      wWidget.setWidgetName(xWidget);
      wWidget.setDataType(dataType);
      wWidget.setDisplayValue(displayValue);
      if (attrType != null) {
         addAttrValues(wWidget, attrType);
         wWidget.setAttributeType(new WfeAttributeTypeToken(attrType));
         setMultiplicity(wWidget, attrType);
      }
      for (WidgetOption opt : widgetOpts) {
         wWidget.getWidgetOptions().add(opt);
      }
      wWidget.setEditable(wWidget.getWidgetOptions().contains(EDITABLE));
      wWidget.setMultiLine(wWidget.getWidgetOptions().contains(FILL_VERT));
      wWidget.setRequired(wWidget.getWidgetOptions().contains(RFT));
      wComp.getWidgets().add(wWidget);
      return wWidget;
   }

   private void addAttrValues(WfdWidgetDef wWidget, AttributeTypeToken attrType) {
      Set<AttributePojo<?>> values = attrTypeToAttrPojos.getValues(attrType);
      if (values != null) {
         wWidget.getAttributes().addAll(values);
      }
   }

   private void setMultiplicity(WfdWidgetDef wWidget, AttributeTypeToken attrType) {
      try {
         ArtifactTypeToken artifactType = atsApi.tokenService().getArtifactType(workItem.getArtifactType().getId());
         Multiplicity multiplicity = artifactType.getMultiplicity(attrType);
         wWidget.setMultiplicity(multiplicity.getToken());
      } catch (Exception ex) {
         wWidget.setMultiplicity(new MultiplicityToken("error: " + ex.getLocalizedMessage(), -1L));
      }
   }

   private void addStateComposite(WfdWidgetComposite hComp) {
      WfdWidgetComposite stateComp = new WfdWidgetComposite("State Composite");
      addWidgetDef(stateComp, "Current State", "XLabel", "String", null, workItem.getCurrentStateName(), NOT_EDITABLE);
      addWidgetDef(stateComp, "Created", "XLabel", "String", null, DateUtil.getMMDDYYHHMM(workItem.getCreatedDate()),
         NOT_EDITABLE);
      addWidgetDef(stateComp, "Originator", "XLabel", "String", null, workItem.getCreatedBy().getName(), NOT_EDITABLE);
      stateComp.setColumns(6);
      hComp.getWidgets().add(stateComp);
   }

   private void addTeamComposite(WfdWidgetComposite hComp) {
      WfdWidgetComposite teamComp = new WfdWidgetComposite("Team Composite");
      int count = 0;
      if (workItem.isTeamWorkflow()) {
         count++;
         addWidgetDef(teamComp, "Team", "XLabel", "String", null,
            workItem.getParentTeamWorkflow().getTeamDefinition().getName(), NOT_EDITABLE);
      } else if ((workItem.isTask() || workItem.isReview()) && workItem.getParentTeamWorkflow() != null) {
         count++;
         addWidgetDef(teamComp, "Parent Id", "XLabel", "String", null, workItem.getParentTeamWorkflow().getAtsId(),
            NOT_EDITABLE);
      }
      addWidgetDef(teamComp, "ATS Id", "XLabel", "String", null, workItem.getAtsId(), NOT_EDITABLE);
      count++;
      addWidgetDef(teamComp, "PCR Id(s)", "XLabel", "String", null, Collections.toString(",", workItem.getPcrIds()),
         NOT_EDITABLE);
      count++;
      IAtsAction action = workItem.getParentAction();
      if (action != null) {
         count++;
         addWidgetDef(teamComp, "Action Id", "XLabel", "String", null, workItem.getParentAction().getAtsId(),
            NOT_EDITABLE);
      }
      teamComp.setColumns(count * 2);
      hComp.getWidgets().add(teamComp);
   }

   private void addVersionComposite(WfdWidgetComposite hComp) {
      boolean showTargetedVersion = WorkDefUtil.isShowTargetedVersion(workItem, atsApi);
      if (!showTargetedVersion && workItem.isCompletedOrCancelled()) {
         return;
      }

      WfdWidgetComposite versionComp = new WfdWidgetComposite("Version Composite");
      int count = 0;
      if (showTargetedVersion) {
         count++;
         IAtsVersion ver = atsApi.getVersionService().getTargetedVersion(workItem);
         addWidgetDef(versionComp, "Targeted Version", "XTargetedVersionHyperlink", "String", null,
            (ver != null ? ver.getName() : ""));
      }
      if (workItem.isInWork()) {
         addWidgetDef(versionComp, "Assignee(s)", "XAssigneesHyperlink", "String", null,
            DateUtil.getMMDDYYHHMM(workItem.getCreatedDate()), NOT_EDITABLE);
      }
      versionComp.setColumns(count);
      hComp.getWidgets().add(versionComp);
   }

   private void addBlockedHoldComposite(WfdWidgetComposite hComp) {
      WfdWidgetComposite blockedComp = new WfdWidgetComposite("Blocked/Hold Composite");
      addWidgetDef(blockedComp, "Blocked", "XTextDam", "String", BlockedReason,
         atsApi.getAttributeResolver().getSoleAttributeValue(workItem, BlockedReason, ""), EDITABLE);
      addWidgetDef(blockedComp, "Hold", "XTextDam", "String", HoldReason,
         atsApi.getAttributeResolver().getSoleAttributeValue(workItem, HoldReason, ""), EDITABLE);
      hComp.getWidgets().add(blockedComp);
   }

   private void addTeamHeader() {
      List<LayoutItem> layoutItems = wd.getHeaderDef().getLayoutItems();
      handleLayoutItems(wfd.getHeaderComposite(), layoutItems);
   }

   private void addState(WorkflowData web, StateDefinition stateDef) {
      WfdState wState = new WfdState(stateDef.getName());
      wState.setStateType(stateDef.getStateType());
      wState.setToStates(stateDef.getToStateNames());

      List<LayoutItem> layoutItems = stateDef.getLayoutItems();
      handleLayoutItems(wState.getWidgetComposite(), layoutItems);
      web.addState(wState);
   }

   private void handleLayoutItems(WfdWidgetComposite wComp, List<LayoutItem> layoutItems) {
      for (LayoutItem layoutItem : layoutItems) {
         if (layoutItem.isWidget()) {
            WidgetDefinition widgetDef = (WidgetDefinition) layoutItem;
            WfdWidgetDef wWidget = createWfdWidget(widgetDef);
            wComp.getWidgets().add(wWidget);
         } else if (layoutItem.isComposite()) {
            CompositeLayoutItem lComp = (CompositeLayoutItem) layoutItem;
            WfdWidgetComposite childWComp =
               new WfdWidgetComposite(Strings.isValid(lComp.getName()) ? lComp.getName() : "Layout Composite");
            childWComp.setColumns(lComp.getNumColumns());
            handleLayoutItems(childWComp, lComp.getLayoutItems());
            wComp.getWidgets().add(childWComp);
         }
      }
   }

   private WfdWidgetDef createWfdWidget(WidgetDefinition widgetDef) {
      WfdWidgetDef wWidget = new WfdWidgetDef();
      wWidget.setName(widgetDef.getName());
      wWidget.setWidgetName(widgetDef.getXWidgetName());
      wWidget.setWidgetHints(widgetDef.getWidgetHints());
      AttributeTypeToken attrType = widgetDef.getAttributeType();
      boolean attrTypeEditable = true;
      if (attrType.isValid()) {
         wWidget.setAttributeType(new WfeAttributeTypeToken(attrType));
         wWidget.setDisplayName(attrType.getUnqualifiedName());
         setMultiplicity(wWidget, attrType);
         wWidget.setDataType(attrType.getStoreType());
         if (attrType.isEnumerated()) {
            wWidget.setEnumOptions(((AttributeTypeEnum<?>) attrType).getEnumStrValues());
         }
         // Get related attrs and add to widget for convenience
         addAttrValues(wWidget, attrType);
         if (attrType.getDisplayHints().contains(AtsDisplayHint.Read)) {
            attrTypeEditable = false;
         }
      }
      wWidget.setWidgetOptions(widgetDef.getOptions().getXOptions());
      wWidget.setRequired(widgetDef.getOptions().contains(WidgetOption.RFT));
      wWidget.setMultiLine(widgetDef.getOptions().contains(WidgetOption.FILL_VERT));
      boolean editable = widgetDef.getOptions().contains(WidgetOption.EDITABLE) || attrTypeEditable;
      wWidget.setEditable(editable);

      return wWidget;
   }

   private void addStates() {
      for (StateDefinition stateDef : wd.getStates()) {
         addState(wfd, stateDef);
      }
   }

   private void addPriorities() {
      List<Priorities> priorities = wd.getPriorities();
      if (priorities.size() > 0) {
         for (Priorities pri : wd.getPriorities()) {
            wfd.getPriorities().add(pri.name());
         }
      } else {
         for (String pri : Priorities.getDefaultValuesStrs()) {
            wfd.getPriorities().add(pri);
         }
      }
   }

   private List<String> getChangeTypes() {
      List<ChangeTypes> changeTypes = wd.getChangeTypes();

      return changeTypes.isEmpty() ? ChangeTypes.getDefaultValuesStrs() : changeTypes.stream().map(
         ChangeTypes::name).collect(Collectors.toList());
   }

}
