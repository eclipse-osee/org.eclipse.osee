/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class XListDam extends XList implements IAttributeWidget {

   private Artifact artifact;
   private AttributeTypeToken attributeType;

   @Override
   public Artifact getArtifact() {
      return artifact;
   }

   public XListDam(String displayLabel) {
      super(displayLabel);
   }

   @Override
   public AttributeTypeToken getAttributeType() {
      return attributeType;
   }

   @Override
   public void setAttributeType(Artifact artifact, AttributeTypeToken attrName) {
      this.artifact = artifact;
      this.attributeType = attrName;
      super.setSelected(getStoredStrs());
   }

   @Override
   public void saveToArtifact() {
      getArtifact().setAttributeValues(getAttributeType(), getSelectedStrs());
   }

   public Collection<String> getStoredStrs() {
      return getArtifact().getAttributesToStringList(getAttributeType());
   }

   @Override
   public Result isDirty() {
      if (isEditable()) {
         try {
            Collection<String> enteredValues = getSelectedStrs();
            Collection<String> storedValues = getStoredStrs();
            if (!Collections.isEqual(enteredValues, storedValues)) {
               return new Result(true, getAttributeType() + " is dirty");
            }
         } catch (NumberFormatException ex) {
            // do nothing
         }
      }
      return Result.FalseResult;
   }

   @Override
   public void revert() {
      setAttributeType(getArtifact(), getAttributeType());
   }

}
