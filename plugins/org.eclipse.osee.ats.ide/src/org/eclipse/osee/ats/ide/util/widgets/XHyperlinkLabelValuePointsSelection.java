/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.util.widgets;

import java.util.Collection;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.AtsApiIde;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.EnumToken;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkLabelCmdValueSelection;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredListEnumDialog;

/**
 * @author Donald G. Dunne
 */
public class XHyperlinkLabelValuePointsSelection extends XHyperlinkLabelCmdValueSelection {

   String value = "";
   private final AtsApiIde atsApi;
   private final IAtsTeamDefinition teamDef;

   public XHyperlinkLabelValuePointsSelection(IAtsTeamDefinition teamDef) {
      super("Points", true);
      this.teamDef = teamDef;
      atsApi = AtsApiService.get();
   }

   @Override
   public boolean handleSelection() {
      AttributeTypeToken pointsAttrType = atsApi.getAgileService().getPointsAttrType(teamDef);
      if (pointsAttrType == AtsAttributeTypes.PointsNumeric) {
         EntryDialog dialog = new EntryDialog("Enter Points", "Enter Points");
         if (dialog.open() == Window.OK) {
            String entry = dialog.getEntry();
            if (org.eclipse.osee.framework.jdk.core.util.Strings.isNumeric(entry)) {
               try {
                  double points = Double.valueOf(entry);
                  value = String.valueOf(points);
                  return true;
               } catch (Exception ex) {
                  // do nothing
               }
            }
         }
      } else {
         Collection<EnumToken> enumValues = pointsAttrType.toEnum().getEnumValues();
         FilteredListEnumDialog dialog = new FilteredListEnumDialog("Select Points", "Select Points", enumValues);
         if (dialog.open() == Window.OK) {
            EnumToken selected = dialog.getSelected();
            if (selected != null) {
               value = selected.getName();
               return true;
            }
         }
      }
      return false;
   }

   @Override
   public boolean handleClear() {
      value = "";
      refresh();
      return true;
   }

   @Override
   public String getCurrentValue() {
      return value;
   }

   public String getValue() {
      return value;
   }

   public void setValue(String value) {
      this.value = value;
   }

}
