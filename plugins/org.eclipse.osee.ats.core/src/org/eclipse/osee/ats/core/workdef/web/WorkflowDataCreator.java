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

package org.eclipse.osee.ats.core.workdef.web;

import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.BlockedReason;
import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.HoldReason;
import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.Title;
import static org.eclipse.osee.ats.api.workdef.WidgetOption.EDITABLE;
import static org.eclipse.osee.ats.api.workdef.WidgetOption.FILL_VERT;
import static org.eclipse.osee.ats.api.workdef.WidgetOption.NOT_EDITABLE;
import static org.eclipse.osee.ats.api.workdef.WidgetOption.RFT;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ai.ActionableItem;
import org.eclipse.osee.ats.api.config.AtsDisplayHint;
import org.eclipse.osee.ats.api.config.TeamDefinition;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.team.Priorities;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workdef.WidgetOption;
import org.eclipse.osee.ats.api.workdef.model.CompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.model.LayoutItem;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workdef.model.WidgetDefinition;
import org.eclipse.osee.ats.api.workdef.model.WorkDefOption;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.api.workdef.model.web.WfdAttachment;
import org.eclipse.osee.ats.api.workdef.model.web.WfdAttribute;
import org.eclipse.osee.ats.api.workdef.model.web.WfdState;
import org.eclipse.osee.ats.api.workdef.model.web.WfdWidgetComposite;
import org.eclipse.osee.ats.api.workdef.model.web.WfdWidgetDef;
import org.eclipse.osee.ats.api.workdef.model.web.WfeAttributeTypeToken;
import org.eclipse.osee.ats.api.workdef.model.web.WorkflowData;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactImage;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.AttributeTypeString;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.IAttribute;
import org.eclipse.osee.framework.core.data.Multiplicity;
import org.eclipse.osee.framework.core.data.Multiplicity.MultiplicityToken;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.HashCollectionSet;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class WorkflowDataCreator {

   private final WorkDefinition wd;
   private final AtsApi atsApi;
   private final IAtsWorkItem workItem;
   private WorkflowData wfd;
   private final HashCollectionSet<AttributeTypeToken, WfdAttribute> attrTypeToWfdAttrs = new HashCollectionSet<>();

   public WorkflowDataCreator(WorkDefinition wd, IAtsWorkItem workItem, AtsApi atsApi) {
      this.wd = wd;
      this.workItem = workItem;
      this.atsApi = atsApi;
   }

   public WorkflowData get() {
      wfd = new WorkflowData(workItem.getId(), workItem.getName());
      addDetails();
      addAttributes();
      addWorkDefDetails();
      addImage();
      addAttachments();
      addTargtedVersion();
      return wfd;
   }

   private void addWorkDefDetails() {
      wfd.setWorkDefId(wd.getId());
      wfd.setWorkDefName(wd.getName());
      addWorkDefOptions();
      addStaticHeader();
      addTeamHeader();
      addStates();
      addChangeTypes();
      addPriorities();
   }

   private void addTargtedVersion() {
      IAtsVersion ver = atsApi.getVersionService().getTargetedVersion(workItem);
      if (ver != null) {
         wfd.setTargetedVersion(ver.getArtifactToken());
      }
   }

   private void addAttributes() {
      for (IAttribute<?> attr : atsApi.getAttributeResolver().getAttributesNew(workItem)) {
         WfdAttribute wAttr = new WfdAttribute();
         wAttr.setAttrId(attr.getId());
         wAttr.setAttrType(attr.getAttributeType());
         if (attr.getAttributeType().isDate() && attr.getValue() instanceof Date) {
            wAttr.setValue(DateUtil.getMMDDYYHHMM((Date) attr.getValue()));
         } else {
            wAttr.setValue(attr.getValue().toString());
         }
         wAttr.setGammaId(attr.getGammaId());
         ArtifactTypeToken artifactType = atsApi.tokenService().getArtifactType(workItem.getArtifactType().getId());
         Multiplicity multiplicity = artifactType.getMultiplicity(attr.getAttributeType());
         wAttr.setMultiplicity(multiplicity.getToken());
         if (attr.getAttributeType().isEnumerated()) {
            wAttr.setEnumOptions(((AttributeTypeEnum<?>) attr.getAttributeType()).getEnumStrValues());
         }
         wfd.getAttributes().add(wAttr);
         attrTypeToWfdAttrs.put(attr.getAttributeType(), wAttr);
      }
   }

   private void addAttachments() {
      for (ArtifactToken art : atsApi.getRelationResolver().getRelated(workItem,
         CoreRelationTypes.SupportingInfo_SupportingInfo)) {
         ArtifactToken linkArt = art.getToken();
         WfdAttachment attach = WfdAttachment.valueOf(linkArt);
         wfd.getAttachments().add(attach);
      }
      for (ArtifactToken art : atsApi.getRelationResolver().getRelated(workItem,
         CoreRelationTypes.SupportingInfo_IsSupportedBy)) {
         ArtifactToken linkArt = art.getToken();
         WfdAttachment attach = WfdAttachment.valueOf(linkArt);
         wfd.getAttachments().add(attach);
      }
   }

   private void addImage() {
      ArtifactTypeToken artType = atsApi.tokenService().getArtifactType(workItem.getArtifactType().getId());
      ArtifactImage artifactImage = atsApi.getConfigService().getArtTypeToImage().get(artType);
      if (artifactImage != null) {
         String image = String.format("%s/%s/%s", atsApi.getApplicationServerBase(), artifactImage.getBaseUrl(),
            artifactImage.getImageName());
         wfd.setArtTypeImage(image);
      }
   }

   private void addDetails() {
      wfd.setAtsId(workItem.getAtsId());
      wfd.setArtTypeName(workItem.getArtifactTypeName());
      wfd.setTx(((ArtifactReadable) workItem.getStoreObject()).getTransaction());
      ArtifactId teamDefId = atsApi.getAttributeResolver().getSoleAttributeValue(workItem,
         AtsAttributeTypes.TeamDefinitionReference, ArtifactId.SENTINEL);
      if (teamDefId.isValid()) {
         TeamDefinition teamDef = atsApi.getConfigService().getConfigurations().getIdToTeamDef().get(teamDefId.getId());
         if (teamDef != null) {
            wfd.setTeamName(teamDef.getName());
         }
      }
      Collection<ArtifactId> aiIds =
         atsApi.getAttributeResolver().getAttributeValues(workItem, AtsAttributeTypes.ActionableItemReference);
      for (ArtifactId aiId : aiIds) {
         ActionableItem ai = atsApi.getConfigService().getConfigurations().getIdToAi().get(aiId.getId());
         if (ai != null) {
            wfd.getAiNames().add(ai.getName());
         }
      }
      wfd.setCurrentStateName(workItem.getCurrentStateName());
      wfd.setCurrentStateType(workItem.getCurrentStateType());
      wfd.setAssigneesStr(workItem.getAssigneesStr());
      wfd.setOriginator(workItem.getCreatedBy().getName());
      wfd.setCreationDate(DateUtil.getMMDDYYHHMM(workItem.getCreatedDate()));
      if (workItem.getParentTeamWorkflow() != null) {
         wfd.setParentAtsId(workItem.getParentTeamWorkflow().getAtsId());
      }
      if (workItem.getParentAction() != null) {
         wfd.setActionId(workItem.getParentAction().getAtsId());
      }
      wfd.setLegacyPcrId(workItem.getLegacyId());
      wfd.setPcrIds(Collections.toString(",", workItem.getPcrIds()));
      wfd.setEditable(WorkDefUtil.isEditable(workItem, atsApi));
   }

   private void addStates() {
      for (StateDefinition stateDef : wd.getStates()) {
         addState(wfd, stateDef);
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

   private void addBlockedHoldComposite(WfdWidgetComposite hComp) {
      WfdWidgetComposite blockedComp = new WfdWidgetComposite("Blocked/Hold Composite");
      addWidgetDef(blockedComp, "Blocked", "XTextDam", "String", BlockedReason,
         atsApi.getAttributeResolver().getSoleAttributeValue(workItem, BlockedReason, ""), EDITABLE);
      addWidgetDef(blockedComp, "Hold", "XTextDam", "String", HoldReason,
         atsApi.getAttributeResolver().getSoleAttributeValue(workItem, HoldReason, ""), EDITABLE);
      hComp.getWidgets().add(blockedComp);
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

   private void addStateComposite(WfdWidgetComposite hComp) {
      WfdWidgetComposite stateComp = new WfdWidgetComposite("State Composite");
      addWidgetDef(stateComp, "Current State", "XLabel", "String", null, workItem.getCurrentStateName(), NOT_EDITABLE);
      addWidgetDef(stateComp, "Created", "XLabel", "String", null, DateUtil.getMMDDYYHHMM(workItem.getCreatedDate()),
         NOT_EDITABLE);
      addWidgetDef(stateComp, "Originator", "XLabel", "String", null, workItem.getCreatedBy().getName(), NOT_EDITABLE);
      stateComp.setColumns(6);
      hComp.getWidgets().add(stateComp);
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
         wWidget.setAttrType(new WfeAttributeTypeToken(attrType));
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
      Set<WfdAttribute> values = attrTypeToWfdAttrs.getValues(attrType);
      if (values != null) {
         wWidget.getAttrs().addAll(values);
      }
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
         wWidget.setAttrType(new WfeAttributeTypeToken(attrType));
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

   private void setMultiplicity(WfdWidgetDef wWidget, AttributeTypeToken attrType) {
      try {
         ArtifactTypeToken artifactType = atsApi.tokenService().getArtifactType(workItem.getArtifactType().getId());
         Multiplicity multiplicity = artifactType.getMultiplicity(attrType);
         wWidget.setMultiplicity(multiplicity.getToken());
      } catch (Exception ex) {
         wWidget.setMultiplicity(new MultiplicityToken("error: " + ex.getLocalizedMessage(), -1L));
      }
   }

   private void addWorkDefOptions() {
      for (WorkDefOption opt : wd.getOptions()) {
         wfd.getWorkDefOption().add(opt.name());
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

   private void addChangeTypes() {
      List<ChangeTypes> changeTypes = wd.getChangeTypes();
      if (changeTypes.size() > 0) {
         for (ChangeTypes changeType : changeTypes) {
            wfd.getChangeTypes().add(changeType.name());
         }
      } else {
         wfd.getChangeTypes().addAll(ChangeTypes.getDefaultValuesStrs());
      }
   }

}
