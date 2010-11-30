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
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
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

      Collection<IAttributeType> getSelections(OperationType operationType, String title, String message, List<IAttributeType> input);

      void addWidgetFor(Collection<IAttributeType> attributeTypes) throws OseeCoreException;

      void removeWidgetFor(Collection<IAttributeType> attributeTypes) throws OseeCoreException;
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

   public void onAddAttributeType() throws OseeCoreException {
      Artifact artifact = model.getArtifact();
      List<IAttributeType> input = AttributeTypeUtil.getEmptyTypes(artifact);
      Collection<IAttributeType> selectedItems =
         selectItems(OperationType.ADD_ITEM, "Add Attribute Types", "add", input);
      if (!selectedItems.isEmpty()) {
         for (IAttributeType attributeType : selectedItems) {
            artifact.addAttribute(attributeType);
         }
         display.addWidgetFor(selectedItems);
         model.dirtyStateChanged();
      }
   }

   public void onRemoveAttributeType() throws OseeCoreException {
      Artifact artifact = model.getArtifact();
      Collection<IAttributeType> validTypesPerBranch = artifact.getAttributeTypes();
      List<IAttributeType> input = AttributeTypeUtil.getTypesWithData(artifact);

      Iterator<IAttributeType> iterator = input.iterator();
      while (iterator.hasNext()) {
         IAttributeType type = iterator.next();
         if (validTypesPerBranch.contains(type)) {
            int occurrencesAfterRemoval = artifact.getAttributes(type).size() - 1;
            if (occurrencesAfterRemoval < AttributeTypeManager.getMinOccurrences(type)) {
               iterator.remove();
            }
         }
      }
      Collection<IAttributeType> selectedItems =
         selectItems(OperationType.REMOVE_ITEM, "Delete Attribute Types", "remove", input);
      if (!selectedItems.isEmpty()) {
         for (IAttributeType attributeType : selectedItems) {
            artifact.deleteAttributes(attributeType);
         }
         display.removeWidgetFor(selectedItems);
         model.dirtyStateChanged();
      }
   }

   private Collection<IAttributeType> selectItems(OperationType operationType, String title, String operationName, List<IAttributeType> input) {
      Collection<IAttributeType> selectedItems = Collections.emptyList();
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