/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.framework.ui.skynet.widgets;

import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public abstract class XRadioButtonsDam extends XRadioButtons implements AttributeWidget {

   protected Artifact artifact;
   protected AttributeTypeToken attributeType;

   public XRadioButtonsDam(String displayLabel) {
      super(displayLabel, null);
   }

   public String getStoredString() {
      return artifact.getSoleAttributeValueAsString(attributeType, null);
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

}
