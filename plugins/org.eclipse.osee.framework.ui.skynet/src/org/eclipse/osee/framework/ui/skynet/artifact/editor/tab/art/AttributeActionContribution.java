/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.skynet.artifact.editor.tab.art;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.DisplayHint;
import org.eclipse.osee.framework.core.data.Multiplicity;
import org.eclipse.osee.framework.core.util.CoreImage;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactPromptChange;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.IActionContributor;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.util.AttributeTypeUtil;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxAttributeTypeDialog;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * Provides Add/Delete Attribute Type toolbar actions for the Artifact Editor Attributes tab.
 *
 * @author Donald G. Dunne
 */
public class AttributeActionContribution implements IActionContributor {

   private final ArtAttrTab tab;

   public AttributeActionContribution(ArtAttrTab tab) {
      this.tab = tab;
   }

   @Override
   public void contributeToToolBar(IToolBarManager manager) {
      manager.add(new OpenAddAttributeTypeDialogAction());
      manager.add(new OpenDeleteAttributeTypeDialogAction());
   }

   private ArtifactEditor getEditor() {
      return tab.getEditor();
   }

   private Artifact getArtifact() {
      return getEditor().getEditorInput().getArtifact();
   }

   //////////////////////////////////////////
   // Add Attribute Type
   //////////////////////////////////////////

   private final class OpenAddAttributeTypeDialogAction extends Action {
      public OpenAddAttributeTypeDialogAction() {
         super();
         setImageDescriptor(ImageManager.getImageDescriptor(CoreImage.ADD_GREEN));
         setToolTipText("Opens a dialog to select which attribute type instances to create on the artifact");
      }

      @Override
      public void run() {
         try {
            Artifact artifact = getArtifact();
            List<AttributeTypeToken> input = AttributeTypeUtil.getEmptyTypes(artifact);
            if (input.isEmpty()) {
               AWorkbench.popup("Add Attribute Types", "No attribute types available to add.");
               return;
            }
            FilteredCheckboxAttributeTypeDialog dialog =
               new FilteredCheckboxAttributeTypeDialog("Add Attribute Types", "Select items to add.",
                  input, new AttributeTypeLabelProvider(), new AttributeTypeLabelProvider.Comparator());
            if (dialog.open() == Window.OK) {
               Collection<AttributeTypeToken> checked = dialog.getChecked();
               boolean added = false;
               for (AttributeTypeToken attributeType : checked) {
                  boolean result = promptAndAdd(artifact, attributeType);
                  if (result) {
                     added = true;
                  }
               }
               if (added) {
                  artifact.persist("Add attribute types");
                  getEditor().reloadEditor();
               }
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }

   private boolean promptAndAdd(Artifact artifact, AttributeTypeToken attributeType) {
      if (attributeType.isString()) {
         EntryDialog dialog = new EntryDialog("Add " + attributeType.getUnqualifiedName(), "Enter value:");
         dialog.setFillVertically(true);
         if (dialog.open() == Window.OK) {
            String value = dialog.getEntry();
            if (Strings.isValid(value)) {
               artifact.addAttributeFromString(attributeType, value);
               return true;
            }
         }
         return false;
      } else if (isSingleValue(artifact, attributeType) && (attributeType.isDate() || attributeType.isDouble() || attributeType.isInteger() || attributeType.isLong() || attributeType.isBoolean())) {
         artifact.addAttribute(attributeType);
         boolean result = ArtifactPromptChange.promptChangeAttribute(attributeType,
            java.util.Collections.singleton(artifact), false, "Add Attribute");
         if (!result) {
            artifact.deleteAttributes(attributeType);
            return false;
         }
         return true;
      } else {
         artifact.addAttribute(attributeType);
         return true;
      }
   }

   private boolean isSingleValue(Artifact artifact, AttributeTypeToken attributeType) {
      Multiplicity mult = artifact.getArtifactType().getMultiplicity(attributeType);
      return mult.is(Multiplicity.ZERO_OR_ONE, Multiplicity.EXACTLY_ONE);
   }

   //////////////////////////////////////////
   // Delete Attribute Type
   //////////////////////////////////////////

   private final class OpenDeleteAttributeTypeDialogAction extends Action {
      public OpenDeleteAttributeTypeDialogAction() {
         super();
         setImageDescriptor(ImageManager.getImageDescriptor(CoreImage.DELETE));
         setToolTipText("Opens a dialog to select which attribute type instances to remove from the artifact");
      }

      @Override
      public void run() {
         try {
            Artifact artifact = getArtifact();
            List<AttributeTypeToken> input = getRemovableTypes(artifact);
            if (input.isEmpty()) {
               AWorkbench.popup("Delete Attribute Types", "No attribute types available to remove.");
               return;
            }
            FilteredCheckboxAttributeTypeDialog dialog =
               new FilteredCheckboxAttributeTypeDialog("Delete Attribute Types", "Select items to remove.",
                  input, new AttributeTypeLabelProvider(), new AttributeTypeLabelProvider.Comparator());
            if (dialog.open() == Window.OK) {
               Collection<AttributeTypeToken> checked = dialog.getChecked();
               if (!checked.isEmpty()) {
                  for (AttributeTypeToken attributeType : checked) {
                     artifact.deleteAttributes(attributeType);
                  }
                  artifact.persist("Delete attribute types");
                  getEditor().reloadEditor();
               }
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }

   private List<AttributeTypeToken> getRemovableTypes(Artifact artifact) {
      List<AttributeTypeToken> input = AttributeTypeUtil.getTypesWithData(artifact);
      Iterator<AttributeTypeToken> iterator = input.iterator();
      while (iterator.hasNext()) {
         AttributeTypeToken type = iterator.next();
         if (type.getDisplayHints().stream().anyMatch(DisplayHint::isReadonly)) {
            iterator.remove();
            continue;
         }
         Multiplicity mult = artifact.getArtifactType().getMultiplicity(type);
         if (mult.is(Multiplicity.EXACTLY_ONE)) {
            iterator.remove();
         }
      }
      return input;
   }
}
