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

import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class XRadioButtonsBooleanTriStateDam extends XRadioButtonsBooleanTriState implements AttributeWidget {

   public static final Object WIDGET_ID = XRadioButtonsBooleanTriStateDam.class.getSimpleName();
   protected Artifact artifact;
   protected AttributeTypeToken attributeType;

   public XRadioButtonsBooleanTriStateDam(String displayLabel) {
      super(displayLabel);
   }

   @Override
   protected void handleSelection(XRadioButton button) {
      super.handleSelection(button);
      if (selected.isUnSet()) {
         artifact.deleteAttributes(attributeType);
      } else if (selected.isYes()) {
         artifact.setSoleAttributeValue(attributeType, true);
      } else {
         artifact.setSoleAttributeValue(attributeType, false);
      }
      artifact.persist("Auto-Save");
   }

   public String getStoredString() {
      String str = artifact.getSoleAttributeValueAsString(attributeType, "");
      if ("true".equals(str)) {
         return BooleanState.Yes.name();
      } else if ("false".equals(str)) {
         return BooleanState.No.name();
      } else {
         return BooleanState.UnSet.name();
      }
   }

   @Override
   public Artifact getArtifact() {
      return artifact;
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
   public void setAttributeType(Artifact artifact, AttributeTypeToken attributeType) {
      this.artifact = artifact;
      this.attributeType = attributeType;
      refresh();
   }

   @Override
   public AttributeTypeToken getAttributeType() {
      return attributeType;
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

}
