package org.eclipse.osee.framework.ui.skynet.artifact.editor.sections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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
   }

   public static interface Model {
      void doSave();

      boolean isDirty();

      Artifact getArtifact();
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

   public void onAddAttributeType() throws OseeCoreException {
      Artifact artifact = model.getArtifact();
      IAttributeType[] types = AttributeTypeUtil.getEmptyTypes(artifact);
      List<IAttributeType> input = new ArrayList<IAttributeType>(Arrays.asList(types));
      Collection<? extends IAttributeType> selectedItems =
            selectItems(OperationType.ADD_ITEM, "Add Attribute Types", "add", input);
      if (!selectedItems.isEmpty()) {
         handleDirtyEditor();

         for (IAttributeType attributeType : selectedItems) {
            artifact.addAttribute(attributeType);
         }
      }
   }

   public void onRemoveAttributeType() throws OseeCoreException {
      Artifact artifact = model.getArtifact();
      AttributeType[] types = AttributeTypeUtil.getTypesWithData(artifact);
      List<AttributeType> input = new ArrayList<AttributeType>(Arrays.asList(types));
      for (AttributeType type : types) {
         if (artifact.getAttributes(type).size() - 1 < type.getMinOccurrences()) {
            input.remove(type);
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