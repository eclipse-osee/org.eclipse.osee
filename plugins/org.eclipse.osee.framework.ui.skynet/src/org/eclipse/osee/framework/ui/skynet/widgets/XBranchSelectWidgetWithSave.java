/*********************************************************************
 * Copyright (c) 2012 Boeing
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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Jeff C. Phillips
 */
public class XBranchSelectWidgetWithSave extends XBranchSelectWidget implements AttributeWidget {

   private Artifact artifact;
   private AttributeTypeToken attributeType;

   public XBranchSelectWidgetWithSave(String label) {
      super(label);
      addXModifiedListener(new DirtyListener());
   }

   public List<BranchToken> getStored() {
      return artifact.getAttributeValues(attributeType);
   }

   @Override
   public Artifact getArtifact() {
      return artifact;
   }

   @Override
   public void saveToArtifact() {
      artifact.setAttributeFromValues(attributeType, Arrays.asList(getSelection()));
   }

   @Override
   public void revert() {
      setAttributeType(getArtifact(), getAttributeType());
   }

   @Override
   public Result isDirty() {
      if (isEditable()) {
         try {
            Collection<BranchToken> storedValues = getStored();
            Collection<BranchToken> widgetInput = Arrays.asList(getSelection());
            if (!Collections.isEqual(widgetInput, storedValues)) {
               return new Result(true, getAttributeType() + " is dirty");
            }
         } catch (OseeCoreException ex) {
            // Do nothing
         }
      }
      return Result.FalseResult;
   }

   @Override
   public void reSet() {
      setAttributeType(artifact, attributeType);
   }

   @Override
   public void setAttributeType(Artifact artifact, AttributeTypeToken attributeType) {
      this.artifact = artifact;
      this.attributeType = attributeType;
      List<BranchToken> storedBranchReference = getStored();
      if (!storedBranchReference.isEmpty()) {
         setSelection(storedBranchReference.get(0));
      }
   }

   @Override
   public AttributeTypeToken getAttributeType() {
      return attributeType;
   }

   private class DirtyListener implements XModifiedListener {
      @Override
      public void widgetModified(XWidget widget) {
         isDirty();
      }
   }

}
