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

import java.util.Collection;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.EnumToken;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.ListSelectionDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.ArtifactWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkLabelCmdValueSelection;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public class XPointsWidget extends XHyperlinkLabelCmdValueSelection implements ArtifactWidget {

   public static final Object WIDGET_ID = XPointsWidget.class.getSimpleName();
   public float points = 0;
   private final AtsApi atsApi;
   private IAtsWorkItem workItem;
   private AttributeTypeToken pointsAttrType = AttributeTypeToken.SENTINEL;

   public XPointsWidget() {
      super("Points", true, 50);
      atsApi = AtsApiService.get();
   }

   @Override
   public String getCurrentValue() {
      AttributeTypeToken pointsAttrType = getPointsAttrType();
      if (pointsAttrType == null) {
         pointsAttrType = AtsAttributeTypes.PointsNumeric;
      }
      return atsApi.getAttributeResolver().getSoleAttributeValueAsString(workItem, pointsAttrType, "");
   }

   @Override
   public boolean handleSelection() {
      AttributeTypeToken pointsAttrType = getPointsAttrType();
      if (pointsAttrType == AtsAttributeTypes.PointsNumeric) {
         EntryDialog dialog = new EntryDialog("Enter Points", "Enter Points");
         if (dialog.open() == Window.OK) {
            String entry = dialog.getEntry();
            if (org.eclipse.osee.framework.jdk.core.util.Strings.isNumeric(entry)) {
               try {
                  double points = Double.valueOf(entry);
                  IAtsChangeSet changes = atsApi.createChangeSet("Set Points");
                  changes.setSoleAttributeValue(workItem, pointsAttrType, points);
                  changes.executeIfNeeded();
                  return true;
               } catch (Exception ex) {
                  // do nothing
               }
            }
         }
      } else {
         Collection<EnumToken> enumValues = pointsAttrType.toEnum().getEnumValues();
         Object[] values = enumValues.toArray(new Object[enumValues.size()]);
         ListSelectionDialog dialog = new ListSelectionDialog(values, Displays.getActiveShell(), "Enter Points", null,
            "Enter Points", 3, new String[] {"OK", "Cancel"}, 0);
         if (dialog.open() == Window.OK) {
            EnumToken entry = (EnumToken) values[dialog.getSelection()];
            try {
               IAtsChangeSet changes = atsApi.createChangeSet("Set Points");
               changes.setSoleAttributeValue(workItem, pointsAttrType, entry.getName());
               changes.executeIfNeeded();
               return true;
            } catch (Exception ex) {
               // do nothing
            }
         }
      }
      return false;
   }

   @Override
   public boolean handleClear() {
      IAtsChangeSet changes = atsApi.createChangeSet("Remove Points");
      changes.deleteAttributes(workItem, getPointsAttrType());
      changes.executeIfNeeded();
      return true;
   }

   public AttributeTypeToken getPointsAttrType() {
      if (pointsAttrType.isInvalid()) {
         IAtsTeamWorkflow teamWf = workItem.getParentTeamWorkflow();
         if (teamWf != null) {
            IAgileTeam agileTeam = atsApi.getAgileService().getAgileTeam(teamWf);
            if (agileTeam != null) {
               pointsAttrType = atsApi.getAgileService().getAgileTeamPointsAttributeType(agileTeam);
            }
         }
         if (pointsAttrType.isInvalid()) {
            pointsAttrType = AtsAttributeTypes.PointsNumeric;
         }
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
