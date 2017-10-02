/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets;

import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public abstract class XRadionButtonsDam extends XRadioButtons implements IAttributeWidget {

   protected Artifact artifact;
   protected AttributeTypeToken attributeType;

   public XRadionButtonsDam(String displayLabel) {
      super(displayLabel, null);
   }

   @Override
   public Artifact getArtifact()  {
      return artifact;
   }

   @Override
   public void setAttributeType(Artifact artifact, AttributeTypeToken attributeType) {
      this.artifact = artifact;
      this.attributeType = attributeType;
   }

   @Override
   public AttributeTypeToken getAttributeType() {
      return attributeType;
   }

}
