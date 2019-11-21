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
package org.eclipse.osee.ats.ide.util.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
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
   private static final Map<AttributeTypeId, ArtifactTypeId> artRefAttrTypeToValidArtType =
      new HashMap<AttributeTypeId, ArtifactTypeId>();
   private static final Collection<AttributeTypeId> artRefAttributeTypes;

   static {
      xFlatAttributeTypes.add(CoreAttributeTypes.WorkTransition);
      xFlatAttributeTypes.add(CoreAttributeTypes.WorkData);
      xFlatAttributeTypes.add(AtsAttributeTypes.State);
      artRefAttrTypeToValidArtType.put(AtsAttributeTypes.ActionableItemReference, AtsArtifactTypes.ActionableItem);
      artRefAttrTypeToValidArtType.put(AtsAttributeTypes.TeamDefinitionReference, AtsArtifactTypes.TeamDefinition);
      artRefAttrTypeToValidArtType.put(AtsAttributeTypes.WorkflowDefinitionReference, ArtifactTypeId.SENTINEL);
      artRefAttrTypeToValidArtType.put(AtsAttributeTypes.WorkPackageReference, AtsArtifactTypes.WorkPackage);
      artRefAttrTypeToValidArtType.put(AtsAttributeTypes.RelatedPeerWorkflowDefinitionReference,
         AtsArtifactTypes.TeamDefinition);
      artRefAttrTypeToValidArtType.put(AtsAttributeTypes.RelatedTaskWorkflowDefinitionReference,
         AtsArtifactTypes.TeamDefinition);
      artRefAttrTypeToValidArtType.put(AtsAttributeTypes.TaskToChangedArtifactReference,
         AtsArtifactTypes.TeamDefinition);
      artRefAttributeTypes = artRefAttrTypeToValidArtType.keySet();
   }

   @Override
   public List<XWidgetRendererItem> getDynamicXWidgetLayoutData(AttributeTypeToken attributeType) {
      List<XWidgetRendererItem> layouts = new ArrayList<>();
      if (attributeType.equals(AtsAttributeTypes.BaselineBranchId)) {
         layouts = super.getDynamicXWidgetLayoutData(attributeType);
         XWidgetRendererItem layoutData = layouts.get(0);
         layoutData.setXWidgetName(XBranchSelectWidgetDam.WIDGET_ID);
      } else if (xFlatAttributeTypes.contains(attributeType)) {
         layouts = super.getDynamicXWidgetLayoutData(attributeType);
         XWidgetRendererItem layoutData = layouts.get(0);
         layoutData.setXWidgetName(XTextFlatDam.WIDGET_ID);
      } else if (attributeType.matches(AtsAttributeTypes.DslSheet, AtsAttributeTypes.TestRunToSourceLocator)) {
         layouts = super.getDynamicXWidgetLayoutData(attributeType);
         XWidgetRendererItem layoutData = layouts.get(0);
         layoutData.getXOptionHandler().add(XOption.FILL_VERTICALLY);
      } else if (attributeType.equals(AtsAttributeTypes.ProgramId)) {
         layouts = super.getDynamicXWidgetLayoutData(attributeType);
         XWidgetRendererItem layoutData = layouts.get(0);
         layoutData.setXWidgetName(XProgramSelectionWidget.WIDGET_ID);
      } else if (artRefAttributeTypes.contains(attributeType)) {
         layouts = super.getDynamicXWidgetLayoutData(attributeType);
         XWidgetRendererItem layoutData = layouts.get(0);
         ArtifactTypeId artType = artRefAttrTypeToValidArtType.get(attributeType);
         if (artType.isValid()) {
            layoutData.setArtifactType(artType);
         }
         layoutData.setXWidgetName(XArtifactReferencedAtsObjectAttributeWidget.WIDGET_ID);
      }
      return layouts;
   }
}
