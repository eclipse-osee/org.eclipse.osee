/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.artifact.editor.sections;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.IActionContributor;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Roberto E. Escobar
 */
public class AttributeActionContribution implements IActionContributor {

   private final AttributeTypeEditPresenter attributeTypeEditor;

   public AttributeActionContribution(AttributesFormSection attributesForm) {
      AttributeTypeEditPresenter.Display view = new AttributeTypeEditDisplay(attributesForm);
      ArtifactEditor editor = (attributesForm.getEditor());
      attributeTypeEditor = new AttributeTypeEditPresenter(new Model(editor), view);
   }

   private static final class Model implements AttributeTypeEditPresenter.Model {
      private final ArtifactEditor editor;

      public Model(ArtifactEditor editor) {
         this.editor = editor;
      }

      @Override
      public void doSave() {
         editor.doSave(new NullProgressMonitor());
      }

      @Override
      public boolean isDirty() {
         return editor.isDirty();
      }

      @Override
      public Artifact getArtifact() {
         return editor.getEditorInput().getArtifact();
      }

      @Override
      public void refreshDirtyArtifact() {
         editor.refreshDirtyArtifact();
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
            OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
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
            OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }
}