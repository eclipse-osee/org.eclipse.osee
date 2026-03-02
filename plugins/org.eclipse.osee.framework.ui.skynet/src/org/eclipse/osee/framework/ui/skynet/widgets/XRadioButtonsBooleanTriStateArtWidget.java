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
package org.eclipse.osee.framework.ui.skynet.widgets;

import org.eclipse.osee.framework.core.util.BooleanState;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XRadioButtonsBooleanTriStateArtWidget extends XRadioButtonsBooleanTriStateWidget {

   public static final WidgetId ID = WidgetId.XRadioButtonsBooleanTriStateArtWidget;

   public XRadioButtonsBooleanTriStateArtWidget() {
      super(ID, "");
   }

   @Override
   protected void handleSelection(XRadioButtonWidget button) {
      super.handleSelection(button);
      // Even though refresh will do this, setting here gives user immediate feedback
      button.setSelected(true);
      if (selected.isUnSet()) {
         getArtifact().deleteAttributes(getAttributeType());
      } else if (selected.isYes()) {
         getArtifact().setSoleAttributeValue(getAttributeType(), true);
      } else {
         getArtifact().setSoleAttributeValue(getAttributeType(), false);
      }
      getArtifact().persist("BooleanTriStateDam Auto-Save");
   }

   public String getStoredString() {
      String str = getArtifact().getSoleAttributeValueAsString(getAttributeType(), "");
      if ("true".equals(str)) {
         return BooleanState.Yes.name();
      } else if ("false".equals(str)) {
         return BooleanState.No.name();
      } else {
         return BooleanState.UnSet.name();
      }
   }

   @Override
   public void refresh() {
      String storedArt = getStoredString();
      if (storedArt != null) {
         setSelected(storedArt);
      }
      validate();
   }

   @Override
   public void setArtifact(Artifact artifact) {
      super.setArtifact(artifact);
      String str = getArtifact().getSoleAttributeValueAsString(getAttributeType(), "");
      if ("true".equals(str)) {
         selected = BooleanState.Yes;
      } else if ("false".equals(str)) {
         selected = BooleanState.No;
      } else {
         selected = BooleanState.UnSet;
      }
   }

}
