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
import org.eclipse.osee.ats.ide.column.PointsColumn;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.ArtifactWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkLabelCmdValueSelDam;

/**
 * @author Donald G. Dunne
 */
public class XEstimatedPointsWidget extends XHyperlinkLabelCmdValueSelDam implements ArtifactWidget {

   public static final Object WIDGET_ID = XEstimatedPointsWidget.class.getSimpleName();
   public float points = 0;
   private final AtsApi atsApi;
   private IAtsWorkItem workItem;
   private AttributeTypeToken pointsAttrType = null;

   public XEstimatedPointsWidget() {
      super("Estimated Points", true, 50);
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
      return PointsColumn.promptChangePoints(workItem, getPointsAttrType(), atsApi);
   }

   @Override
   public boolean handleClear() {
      IAtsChangeSet changes = atsApi.createChangeSet("Remove Points");
      changes.deleteAttributes(workItem, getAttributeType());
      changes.executeIfNeeded();
      return true;
   }

   private AttributeTypeToken getPointsAttrType() {
      if (pointsAttrType == null) {
         pointsAttrType = atsApi.getAgileService().getPointsAttrType(workItem);
      }
      return pointsAttrType;
   }

   @Override
   public Artifact getArtifact() {
      return (Artifact) workItem.getStoreObject();
   }

   @Override
   public void saveToArtifact() {
      // do nothing
   }

   @Override
   public void revert() {
      // do nothing
   }

   @Override
   public Result isDirty() {
      return Result.FalseResult;
   }

   @Override
   public void setArtifact(Artifact artifact) {
      if (artifact instanceof IAtsWorkItem) {
         workItem = (IAtsWorkItem) artifact;
      }
   }

}
