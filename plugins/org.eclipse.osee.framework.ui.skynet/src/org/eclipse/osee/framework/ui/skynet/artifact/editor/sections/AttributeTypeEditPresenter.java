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
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.sections.AttributeTypeEditPresenter.Display.OperationType;

public class AttributeTypeEditPresenter {

   public static interface Display {

      public static enum OperationType {
         ADD_ITEM,
         REMOVE_ITEM,
      }

      void showInformation(String title, String message);

      Collection<? extends IAttributeType> getSelections(OperationType operationType, String title, String message, List<? extends IAttributeType> input);

      void addWidgetFor(Collection<? extends IAttributeType> attributeTypes) throws OseeCoreException;

      void removeWidgetFor(Collection<? extends IAttributeType> attributeTypes) throws OseeCoreException;
   }

   public static interface Model {
      void doSave();

      boolean isDirty();

      Artifact getArtifact();

      void refreshDirtyArtifact();
   }

   private final Display display;
   private final Model model;

   public AttributeTypeEditPresenter(Model model, Display display) {
      this.display = display;
      this.model = model;
   }

   public void handleDirtyEditor() {
      if (model.isDirty()) {
         model.doSave();
      }
   }

   public void refreshDirtyArtifact() {
      model.refreshDirtyArtifact();
   }

   public void onAddAttributeType() throws OseeCoreException {
      Artifact artifact = model.getArtifact();
      List<? extends IAttributeType> input = AttributeTypeUtil.getEmptyTypes(artifact);
      Collection<? extends IAttributeType> selectedItems =
         selectItems(OperationType.ADD_ITEM, "Add Attribute Types", "add", input);
      if (!selectedItems.isEmpty()) {
         handleDirtyEditor();

         for (IAttributeType attributeType : selectedItems) {
            artifact.addAttribute(attributeType);
         }
         display.addWidgetFor(selectedItems);
      }
   }

   public void onRemoveAttributeType() throws OseeCoreException {
      Artifact artifact = model.getArtifact();
      Collection<AttributeType> validTypesPerBranch = artifact.getAttributeTypes();
      List<AttributeType> input = AttributeTypeUtil.getTypesWithData(artifact);
      AttributeType type = null;

      for (Iterator<AttributeType> iterator = input.iterator(); iterator.hasNext(); type = iterator.next()) {
         if (validTypesPerBranch.contains(type) && artifact.getAttributes(type).size() - 1 < type.getMinOccurrences()) {
            iterator.remove();
         }
      }
      Collection<? extends IAttributeType> selectedItems =
         selectItems(OperationType.REMOVE_ITEM, "Delete Attribute Types", "remove", input);
      if (!selectedItems.isEmpty()) {
         handleDirtyEditor();

         for (IAttributeType attributeType : selectedItems) {
            artifact.deleteAttributes(attributeType);
         }
      }
      display.removeWidgetFor(selectedItems);
   }

   private Collection<? extends IAttributeType> selectItems(OperationType operationType, String title, String operationName, List<? extends IAttributeType> input) {
      Collection<? extends IAttributeType> selectedItems = Collections.emptyList();
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