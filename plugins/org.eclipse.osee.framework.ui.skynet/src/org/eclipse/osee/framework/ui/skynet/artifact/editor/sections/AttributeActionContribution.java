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

package org.eclipse.osee.framework.ui.skynet.artifact.editor.sections;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.IActionContributor;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Roberto E. Escobar
 */
public class AttributeActionContribution implements IActionContributor {

   private final AttributeTypeEditPresenter attributeTypeEditor;

   public AttributeActionContribution(AttributesFormSection attributesForm) {
      AttributeTypeEditPresenter.Display view = new AttributeTypeEditDisplay(attributesForm);
      ArtifactEditor editor = attributesForm.getEditor();
      attributeTypeEditor = new AttributeTypeEditPresenter(new Model(editor), view);
   }

   private static final class Model implements AttributeTypeEditPresenter.Model {
      private final ArtifactEditor editor;

      public Model(ArtifactEditor editor) {
         this.editor = editor;
      }

      @Override
      public Artifact getArtifact() {
         return editor.getEditorInput().getArtifact();
      }

      @Override
      public void refreshDirtyArtifact() {
         editor.refreshDirtyArtifact();
      }

      @Override
      public void dirtyStateChanged() {
         editor.editorDirtyStateChanged();
      }
   }

   @Override
   public void contributeToToolBar(IToolBarManager manager) {
      manager.add(new OpenAddAttributeTypeDialogAction());
      manager.add(new OpenDeleteAttributeTypeDialogAction());
   }

   private final class OpenAddAttributeTypeDialogAction extends Action {
      public OpenAddAttributeTypeDialogAction() {
         super();
         setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.ADD_GREEN));
         setToolTipText("Opens a dialog to select which attribute type instances to create on the artifact");
      }

      @Override
      public void run() {
         try {
            attributeTypeEditor.onAddAttributeType();
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }

   private final class OpenDeleteAttributeTypeDialogAction extends Action {
      public OpenDeleteAttributeTypeDialogAction() {
         super();
         setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.DELETE));
         setToolTipText("Opens a dialog to select which attribute type instances to remove from the artifact");
      }

      @Override
      public void run() {
         try {
            attributeTypeEditor.onRemoveAttributeType();
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }
}