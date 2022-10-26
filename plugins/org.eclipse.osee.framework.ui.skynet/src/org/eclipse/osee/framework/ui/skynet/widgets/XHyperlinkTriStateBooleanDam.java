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

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XRadioButtonsBooleanTriState.BooleanState;

/**
 * @author Donald G. Dunne
 */
public class XHyperlinkTriStateBooleanDam extends XHyperlinkTriStateBoolean implements AttributeWidget {

   protected AttributeTypeToken attributeType = AttributeTypeToken.SENTINEL;
   protected Artifact artifact;

   @Override
   public Collection<String> getSelectable() {
      return Arrays.asList(BooleanState.Yes.name(), BooleanState.No.name(), BooleanState.UnSet.name());
   }

   @Override
   public AttributeTypeToken getAttributeType() {
      return attributeType;
   }

   @Override
   protected void handleSelectionPersist(BooleanState selected) {
      if (selected.isUnSet()) {
         artifact.deleteAttributes(attributeType);
      } else if (selected.isYes()) {
         artifact.setSoleAttributeValue(attributeType, true);
      } else {
         artifact.setSoleAttributeValue(attributeType, false);
      }
      artifact.persistInThread("Set Value");
   }

   @Override
   public Artifact getArtifact() {
      return artifact;
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
   public void setAttributeType(Artifact artifact, AttributeTypeToken attributeTypeToken) {
      this.artifact = artifact;
      this.attributeType = attributeTypeToken;
      if (attributeTypeToken.isValid()) {
         this.label = this.attributeType.getUnqualifiedName();
         Boolean sel = artifact.getSoleAttributeValue(attributeTypeToken, null);
         if (sel == null) {
            selected = BooleanState.UnSet;
         } else if (sel) {
            selected = BooleanState.Yes;
         } else {
            selected = BooleanState.No;
         }
      }
   }

   @Override
   public void refresh() {
      setAttributeType(artifact, attributeType);
      super.refresh();
   }
}
