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
package org.eclipse.osee.framework.ui.skynet.test.cases;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Compare;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.sections.AttributeTypeEditPresenter;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.sections.AttributeTypeEditPresenter.Display.OperationType;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test Case for {@link AttributeTypeEditPresenter}
 * 
 * @author Roberto E. Escobar
 */
public class AttributeTypeEditPresenterTest {
   private static final IAttributeType[] selectableTypes = new IAttributeType[] {CoreAttributeTypes.RELATION_ORDER,
      CoreAttributeTypes.Annotation, CoreAttributeTypes.STATIC_ID};

   private static AttributeTypeEditPresenter controller;
   private static MockDisplay display;
   private static Artifact artifact;
   private static MockEditor editor;

   @BeforeClass
   public static void setUp() throws OseeCoreException {
      display = new MockDisplay();
      editor = new MockEditor();
      Branch branch = BranchManager.getCommonBranch();
      artifact = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Artifact, branch, "test attribute types");
      editor.setArtifact(artifact);
      controller = new AttributeTypeEditPresenter(editor, display);
   }

   @AfterClass
   public static void tearDown() throws OseeCoreException {
      artifact.purgeFromBranch();
   }

   @Test
   public void testAddItems() throws OseeCoreException {
      String expectedTitle = "Add Attribute Types";
      String expectedOpMessage = "Select items to add.";
      String expectedNoneMessage = "No attribute types available to add.";
      OperationType expectedType = OperationType.ADD_ITEM;

      testOperation(expectedType, expectedTitle, expectedOpMessage, expectedNoneMessage);
   }

   @Test
   public void testRemoveItems() throws OseeCoreException {
      String expectedTitle = "Delete Attribute Types";
      String expectedOpMessage = "Select items to remove.";
      String expectedNoneMessage = "No attribute types available to remove.";
      OperationType expectedType = OperationType.REMOVE_ITEM;

      testOperation(expectedType, expectedTitle, expectedOpMessage, expectedNoneMessage);
   }

   private static void performOp(AttributeTypeEditPresenter controller, OperationType operationType) throws OseeCoreException {
      switch (operationType) {
         case ADD_ITEM:
            controller.onAddAttributeType();
            break;
         case REMOVE_ITEM:
            controller.onRemoveAttributeType();
            break;
         default:
            throw new UnsupportedOperationException();
      }
   }

   private void testOperation(OperationType operationType, String expectedTitle, String expectedOpMessage, String expectedNoneMessage) throws OseeCoreException {
      editor.setDirty(true);
      editor.setWasSaved(false);

      // None Selected
      display.setSelected();
      List<IAttributeType> selectable = new ArrayList<IAttributeType>(Arrays.asList(selectableTypes));
      performOp(controller, operationType);

      // Editor not saved unless artifact change
      Assert.assertFalse(editor.wasSaved());
      Assert.assertTrue(editor.isDirty());
      checkDisplay(display, operationType, expectedTitle, expectedOpMessage, selectable);

      // Add one at a time
      for (IAttributeType itemToSelect : selectableTypes) {

         display.setSelected(itemToSelect);
         if (OperationType.ADD_ITEM == operationType) {
            Assert.assertTrue(artifact.getAttributes(itemToSelect).isEmpty());
         } else if (OperationType.REMOVE_ITEM == operationType) {
            Assert.assertFalse(artifact.getAttributes(itemToSelect).isEmpty());
         }
         performOp(controller, operationType);

         // Editor saved before adding types
         Assert.assertTrue(editor.wasSaved());
         Assert.assertFalse(editor.isDirty());

         checkDisplay(display, operationType, expectedTitle, expectedOpMessage, selectable);

         if (OperationType.ADD_ITEM == operationType) {
            Assert.assertFalse(artifact.getAttributes(itemToSelect).isEmpty());
         } else if (OperationType.REMOVE_ITEM == operationType) {
            Assert.assertTrue(artifact.getAttributes(itemToSelect).isEmpty());
         }
         selectable.remove(itemToSelect);
      }

      // None Selectable
      display.setSelected();
      performOp(controller, operationType);
      Pair<String, String> info = display.getShowInfo();
      Assert.assertEquals(expectedTitle, info.getFirst());
      Assert.assertEquals(expectedNoneMessage, info.getSecond());
   }

   private static void checkDisplay(MockDisplay display, OperationType expectedType, String title, String message, List<IAttributeType> expectedSelectable) {
      List<? extends IAttributeType> selectableItems = display.getInput();

      Assert.assertFalse(
         String.format("Selectable Types - expected:[%s] actual:[%s]", expectedSelectable, selectableItems),
         Compare.isDifferent(expectedSelectable, selectableItems));
      Assert.assertEquals(expectedType, display.getOperationType());

      Pair<String, String> selectionInfo = display.getSelectionInfo();
      Assert.assertEquals(title, selectionInfo.getFirst());
      Assert.assertEquals(message, selectionInfo.getSecond());
      Assert.assertNull(display.getShowInfo());
   }

   private final static class MockEditor implements AttributeTypeEditPresenter.Model {
      private boolean wasSaved;
      private boolean dirty;
      private Artifact artifact;

      public MockEditor() {

      }

      public void setDirty(boolean dirty) {
         this.dirty = dirty;
      }

      public void setWasSaved(boolean wasSaved) {
         this.wasSaved = wasSaved;
      }

      public boolean wasSaved() {
         return wasSaved;
      }

      @Override
      public void doSave() {
         setWasSaved(true);
         setDirty(false);
      }

      @Override
      public boolean isDirty() {
         return dirty;
      }

      @Override
      public Artifact getArtifact() {
         return artifact;
      }

      public void setArtifact(Artifact artifact) {
         this.artifact = artifact;
      }

   }

   private final static class MockDisplay implements AttributeTypeEditPresenter.Display {

      private Pair<String, String> showInfo;
      private Pair<String, String> selectionInfo;
      private OperationType operationType;
      private List<? extends IAttributeType> input;
      private Collection<? extends IAttributeType> selected;

      private MockDisplay() {
         this.selected = Collections.emptyList();
      }

      @Override
      public Collection<? extends IAttributeType> getSelections(OperationType operationType, String title, String message, List<? extends IAttributeType> input) {
         setSelectionInfo(new Pair<String, String>(title, message));
         setInput(input);
         setOperationType(operationType);
         setShowInfo(null);
         return selected;
      }

      @Override
      public void showInformation(String title, String message) {
         showInfo = new Pair<String, String>(title, message);
      }

      public Pair<String, String> getShowInfo() {
         return showInfo;
      }

      public Pair<String, String> getSelectionInfo() {
         return selectionInfo;
      }

      public OperationType getOperationType() {
         return operationType;
      }

      public List<? extends IAttributeType> getInput() {
         return input;
      }

      public void setSelected(IAttributeType... selected) {
         this.selected = Arrays.asList(selected);
      }

      public void setShowInfo(Pair<String, String> showInfo) {
         this.showInfo = showInfo;
      }

      public void setSelectionInfo(Pair<String, String> selectionInfo) {
         this.selectionInfo = selectionInfo;
      }

      public void setOperationType(OperationType operationType) {
         this.operationType = operationType;
      }

      public void setInput(List<? extends IAttributeType> input) {
         this.input = input;
      }
   }
}
