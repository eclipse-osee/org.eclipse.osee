/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.artifact.editor.sections;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.sections.AttributeTypeEditPresenter.Display.OperationType;

public class AttributeTypeEditPresenter {

   public static interface Display {

      public static enum OperationType {
         ADD_ITEM,
         REMOVE_ITEM,
      }

      void showInformation(String title, String message);

      Collection<AttributeTypeToken> getSelections(OperationType operationType, String title, String message, List<AttributeTypeToken> input);

      void addWidgetFor(Collection<AttributeTypeToken> attributeTypes);

      void removeWidgetFor(Collection<AttributeTypeToken> attributeTypes);
   }

   public static interface Model {

      void dirtyStateChanged();

      Artifact getArtifact();

      void refreshDirtyArtifact();
   }

   private final Display display;
   private final Model model;

   public AttributeTypeEditPresenter(Model model, Display display) {
      this.display = display;
      this.model = model;
   }

   public void refreshDirtyArtifact() {
      model.refreshDirtyArtifact();
   }

   public void onAddAttributeType() {
      Artifact artifact = model.getArtifact();
      List<AttributeTypeToken> input = AttributeTypeUtil.getEmptyTypes(artifact);
      Collection<AttributeTypeToken> selectedItems =
         selectItems(OperationType.ADD_ITEM, "Add Attribute Types", "add", input);
      if (!selectedItems.isEmpty()) {
         for (AttributeTypeToken attributeType : selectedItems) {
            artifact.addAttribute(attributeType);
         }
         display.addWidgetFor(selectedItems);
         model.dirtyStateChanged();
      }
   }

   @SuppressWarnings("deprecation")
   public void onRemoveAttributeType() {
      Artifact artifact = model.getArtifact();
      Collection<AttributeTypeToken> validTypesPerBranch = artifact.getAttributeTypes();
      List<AttributeTypeToken> input = AttributeTypeUtil.getTypesWithData(artifact);

      Iterator<AttributeTypeToken> iterator = input.iterator();
      while (iterator.hasNext()) {
         AttributeTypeToken type = iterator.next();
         if (validTypesPerBranch.contains(type)) {
            int occurrencesAfterRemoval = artifact.getAttributes(type).size() - 1;
            if (occurrencesAfterRemoval < AttributeTypeManager.getMinOccurrences(type)) {
               iterator.remove();
            }
         }
      }
      Collection<AttributeTypeToken> selectedItems =
         selectItems(OperationType.REMOVE_ITEM, "Delete Attribute Types", "remove", input);
      if (!selectedItems.isEmpty()) {
         for (AttributeTypeToken attributeType : selectedItems) {
            artifact.deleteAttributes(attributeType);
         }
         display.removeWidgetFor(selectedItems);
         model.dirtyStateChanged();
      }
   }

   private Collection<AttributeTypeToken> selectItems(OperationType operationType, String title, String operationName, List<AttributeTypeToken> input) {
      Collection<AttributeTypeToken> selectedItems = Collections.emptyList();
      if (input.isEmpty()) {
         String message = String.format("No attribute types available to %s.", operationName);
         display.showInformation(title, message);
      } else {
         String message = String.format("Select items to %s.", operationName);
         selectedItems = display.getSelections(operationType, title, message, input);
      }
      return selectedItems;
   }
}