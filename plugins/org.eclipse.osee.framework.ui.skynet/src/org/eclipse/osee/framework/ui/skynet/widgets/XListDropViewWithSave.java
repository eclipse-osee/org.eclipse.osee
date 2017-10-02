/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * XListDropViewer with save artifacts/state
 */
public class XListDropViewWithSave extends XListDropViewer implements IAttributeWidget {

   private Artifact artifact;
   private AttributeTypeToken attributeType;

   public XListDropViewWithSave(String displayLabel) {
      super(displayLabel);
      addXModifiedListener(dirtyListener);
      singleItemMode = true;
   }

   public List<Artifact> getStored() {
      return artifact.getAttributeValues(attributeType);
   }

   @Override
   public Artifact getArtifact() {
      return artifact;
   }

   @Override
   public void setAttributeType(Artifact artifact, AttributeTypeToken attributeTypeName) {
      this.artifact = artifact;
      this.attributeType = attributeTypeName;
      List<Artifact> storedArtifacts = getStored();
      if (!storedArtifacts.isEmpty()) {
         setSelected(storedArtifacts);
         setInput(storedArtifacts);
      }
   }

   @Override
   public void saveToArtifact() {
      List<Artifact> artifacts = getArtifacts();
      Collection<Artifact> saveItems = null;
      if (!artifacts.isEmpty()) {
         saveItems = Arrays.asList(artifacts.get(0));
      } else {
         saveItems = java.util.Collections.emptyList();
      }
      artifact.setAttributeFromValues(CoreAttributeTypes.ArtifactReference, saveItems);
   }

   @Override
   public void revert() {
      setAttributeType(getArtifact(), getAttributeType());
   }

   @Override
   public AttributeTypeToken getAttributeType() {
      return attributeType;
   }

   @Override
   public Result isDirty() {
      if (isEditable()) {
         try {
            List<Artifact> enteredValues = new ArrayList<>();
            Collections.flatten(getArtifacts(), enteredValues);
            Collection<Artifact> storedValues = getStored();
            if (!Collections.isEqual(enteredValues, storedValues)) {
               return new Result(true, getAttributeType() + " is dirty");
            }
         } catch (OseeCoreException ex) {
            // Do nothing
         }
      }
      return Result.FalseResult;
   }
   private final XModifiedListener dirtyListener = new XModifiedListener() {
      @Override
      public void widgetModified(XWidget widget) {
         isDirty();
      }
   };
}
