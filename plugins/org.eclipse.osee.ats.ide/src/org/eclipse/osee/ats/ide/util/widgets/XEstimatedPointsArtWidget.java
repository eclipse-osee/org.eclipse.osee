/*********************************************************************
 * Copyright (c) 2021 Boeing
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

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.ide.column.PointsColumnUI;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XAbstractHyperlinkLabelCmdValueSelArtWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XEstimatedPointsArtWidget extends XAbstractHyperlinkLabelCmdValueSelArtWidget {

   public static final WidgetId ID = WidgetIdAts.XEstimatedPointsArtWidget;

   public float points = 0;
   private final AtsApi atsApi;
   private IAtsWorkItem workItem;
   private AttributeTypeToken pointsAttrType = null;

   public XEstimatedPointsArtWidget() {
      super(ID, "Estimated Points", true, 50);
      atsApi = AtsApiService.get();
   }

   @Override
   public String getCurrentValue() {
      AttributeTypeToken pointsAttrType = getAttributeType();
      if (pointsAttrType.isInvalid()) {
         pointsAttrType = AtsAttributeTypes.PointsNumeric;
      }
      return atsApi.getAttributeResolver().getSoleAttributeValueAsString(workItem, pointsAttrType, "");
   }

   @Override
   public boolean handleSelection() {
      return PointsColumnUI.promptChangePoints(workItem, getPointsAttrType(), atsApi);
   }

   @Override
   public boolean handleClear() {
      IAtsChangeSet changes = atsApi.createChangeSet("Remove Points");
      changes.deleteAttributes(workItem, getPointsAttrType());
      changes.executeIfNeeded();
      refresh();
      return true;
   }

   private AttributeTypeToken getPointsAttrType() {
      if (pointsAttrType == null) {
         pointsAttrType = atsApi.getAgileService().getPointsAttrType(workItem);
      }
      return pointsAttrType;
   }

   @Override
   public void setArtifact(Artifact artifact) {
      super.setArtifact(artifact);
      if (artifact instanceof IAtsWorkItem) {
         workItem = (IAtsWorkItem) artifact;
      }
   }

}
