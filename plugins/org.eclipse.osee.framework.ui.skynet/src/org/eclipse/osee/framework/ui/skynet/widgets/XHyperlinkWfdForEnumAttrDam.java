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
public class XHyperlinkWfdForEnumAttrDam extends XHyperlinkWfdForEnumAttr implements AttributeWidget {

   private AttributeTypeToken attributeTypeToken = AttributeTypeToken.SENTINEL;
   private Artifact artifact;

   @Override
   public AttributeTypeToken getAttributeType() {
      return attributeTypeToken;
   }

   @Override
   protected void handleSelectionPersist(String selected) {
      artifact.setSoleAttributeValue(attributeTypeToken, selected);
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
      this.attributeTypeToken = attributeTypeToken;
      if (attributeTypeToken.isValid()) {
         this.label = this.attributeTypeToken.getUnqualifiedName();
      }
   }

   @Override
   public String getCurrentValue() {
      return artifact.getSoleAttributeValue(attributeTypeToken, "");
   }

}
