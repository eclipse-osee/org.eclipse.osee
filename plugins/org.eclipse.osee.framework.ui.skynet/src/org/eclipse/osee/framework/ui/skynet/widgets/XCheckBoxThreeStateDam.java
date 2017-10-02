/*******************************************************************************
 * Copyright (c) 2016 Boeing.
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
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class XCheckBoxThreeStateDam extends XCheckBoxThreeState implements IAttributeWidget {

   private Artifact artifact;
   private AttributeTypeToken attributeType;

   public XCheckBoxThreeStateDam(String displayLabel) {
      super(displayLabel);
   }

   @Override
   public Artifact getArtifact()  {
      return artifact;
   }

   @Override
   public void saveToArtifact()  {
      CheckState state = getCheckState();
      if (state == CheckState.UnSet) {
         artifact.deleteAttributes(attributeType);
      } else if (state == CheckState.Checked) {
         artifact.setSoleAttributeValue(attributeType, true);
      } else if (state == CheckState.UnChecked) {
         artifact.setSoleAttributeValue(attributeType, true);
      } else {
         throw new OseeStateException("UnExpected CheckState " + state.name());
      }
   }

   @Override
   public void revert()  {
      setAttributeType(artifact, attributeType);
   }

   @Override
   public Result isDirty()  {
      if (isEditable()) {
         CheckState storedCheckState = getStoredCheckState();
         CheckState checkState = getCheckState();
         if (storedCheckState != checkState) {
            new Result(true, getAttributeType().toString());
         }
      }
      return Result.FalseResult;
   }

   @Override
   public void setAttributeType(Artifact artifact, AttributeTypeToken attributeType)  {
      this.artifact = artifact;
      this.attributeType = attributeType;
      checkState = getStoredCheckState();
      updateCheckWidget();
   }

   private CheckState getStoredCheckState() {
      Boolean set = artifact.getSoleAttributeValue(this.attributeType, null);
      if (set == null) {
         return CheckState.UnSet;
      } else if (set) {
         return CheckState.Checked;
      } else {
         return CheckState.UnChecked;
      }
   }

   @Override
   public AttributeTypeToken getAttributeType() {
      return attributeType;
   }

}
