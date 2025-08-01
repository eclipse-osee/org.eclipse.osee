/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.ide.util.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.ui.skynet.widgets.XBranchSelectWidgetDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XOption;
import org.eclipse.osee.framework.ui.skynet.widgets.XTextFlatDam;
import org.eclipse.osee.framework.ui.skynet.widgets.util.DefaultAttributeXWidgetProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetRendererItem;

/**
 * Provides XFlatDam as default widget for specified attribute types
 *
 * @author Donald G. Dunne
 */
public class AtsAttributeXWidgetProvider extends DefaultAttributeXWidgetProvider {

   private static final Collection<AttributeTypeId> xFlatAttributeTypes = new ArrayList<>();
   private static final Map<AttributeTypeToken, ArtifactTypeToken> artRefAttrTypeToValidArtType =
      new HashMap<AttributeTypeToken, ArtifactTypeToken>();
   private static final Collection<AttributeTypeToken> artRefAttributeTypes;

   static {
      xFlatAttributeTypes.add(CoreAttributeTypes.WorkTransition);
      xFlatAttributeTypes.add(CoreAttributeTypes.WorkData);
      xFlatAttributeTypes.add(AtsAttributeTypes.CurrentStateAssignee);
      xFlatAttributeTypes.add(AtsAttributeTypes.CSCI);
      xFlatAttributeTypes.add(AtsAttributeTypes.TaskSetId);
      xFlatAttributeTypes.add(AtsAttributeTypes.WorkType);
      xFlatAttributeTypes.add(AtsAttributeTypes.JournalSubscriber);
      xFlatAttributeTypes.add(AtsAttributeTypes.Log);
      artRefAttrTypeToValidArtType.put(AtsAttributeTypes.ActionableItemReference, AtsArtifactTypes.ActionableItem);
      artRefAttrTypeToValidArtType.put(AtsAttributeTypes.TeamDefinitionReference, AtsArtifactTypes.TeamDefinition);
      artRefAttrTypeToValidArtType.put(AtsAttributeTypes.ProgramId, AtsArtifactTypes.Program);
      artRefAttrTypeToValidArtType.put(AtsAttributeTypes.RelatedPeerWorkflowDefinitionReference,
         AtsArtifactTypes.TeamDefinition);
      artRefAttrTypeToValidArtType.put(AtsAttributeTypes.RelatedTaskWorkflowDefinitionReference,
         AtsArtifactTypes.TeamDefinition);
      artRefAttrTypeToValidArtType.put(AtsAttributeTypes.TaskToChangedArtifactReference,
         AtsArtifactTypes.TeamDefinition);
      artRefAttributeTypes = artRefAttrTypeToValidArtType.keySet();
   }

   @Override
   public List<XWidgetRendererItem> getDynamicXWidgetLayoutData(ArtifactTypeToken artType,
      AttributeTypeToken attributeType) {
      List<XWidgetRendererItem> layouts = new ArrayList<>();
      if (artRefAttributeTypes.contains(attributeType)) {
         layouts = super.getDynamicXWidgetLayoutData(artType, attributeType);
         XWidgetRendererItem layoutData = layouts.get(0);
         ArtifactTypeToken newArtType = artRefAttrTypeToValidArtType.get(attributeType);
         if (newArtType.isValid()) {
            layoutData.setArtifactType(newArtType);
         }
      }

      if (attributeType.equals(AtsAttributeTypes.BaselineBranchId)) {
         layouts = super.getDynamicXWidgetLayoutData(artType, attributeType);
         XWidgetRendererItem layoutData = layouts.get(0);
         layoutData.setXWidgetName(XBranchSelectWidgetDam.WIDGET_ID);
      } else if (xFlatAttributeTypes.contains(attributeType)) {
         layouts = super.getDynamicXWidgetLayoutData(artType, attributeType);
         XWidgetRendererItem layoutData = layouts.get(0);
         layoutData.setXWidgetName(XTextFlatDam.WIDGET_ID);
      } else if (attributeType.matches(AtsAttributeTypes.TestRunToSourceLocator)) {
         layouts = super.getDynamicXWidgetLayoutData(artType, attributeType);
         XWidgetRendererItem layoutData = layouts.get(0);
         layoutData.getXOptionHandler().add(XOption.FILL_VERTICALLY);
      } else if (attributeType.matches(AtsAttributeTypes.WorkflowDefinitionReference)) {
         layouts = super.getDynamicXWidgetLayoutData(artType, attributeType);
         XWidgetRendererItem layoutData = layouts.get(0);
         layoutData.setXWidgetName(XHyperlinkWorkDefDam.class.getSimpleName());
      } else if (attributeType.matches(AtsAttributeTypes.VersionBranchView)) {
         layouts = super.getDynamicXWidgetLayoutData(artType, attributeType);
         XWidgetRendererItem layoutData = layouts.get(0);
         layoutData.setXWidgetName(XHyperlinkBranchViewSelect.class.getSimpleName());
      }

      return layouts;
   }
}
