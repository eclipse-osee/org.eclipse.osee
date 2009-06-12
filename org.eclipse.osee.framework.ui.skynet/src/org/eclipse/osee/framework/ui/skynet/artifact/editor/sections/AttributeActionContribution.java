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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.IActionContributor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;

/**
 * @author Roberto E. Escobar
 */
public class AttributeActionContribution implements IActionContributor {

   private final ArtifactEditor editor;

   public AttributeActionContribution(ArtifactEditor editor) {
      this.editor = editor;
   }

   public void contributeToToolBar(IToolBarManager manager) {
      manager.add(new OpenAddAttributeTypeDialogAction());
      manager.add(new OpenDeleteAttributeTypeDialogAction());
   }

   private CheckedTreeSelectionDialog createDialog(String title, Image image, String message) {
      CheckedTreeSelectionDialog dialog =
            new CheckedTreeSelectionDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(),
                  new LabelProvider(), new ArrayTreeContentProvider());
      dialog.setTitle(title);
      dialog.setImage(image);
      dialog.setMessage(message);
      dialog.setValidator(new ISelectionStatusValidator() {

         @Override
         public IStatus validate(Object[] selection) {
            if (selection.length == 0) {
               return new Status(IStatus.ERROR, SkynetGuiPlugin.PLUGIN_ID,
                     "Select at least one item or click cancel to exit.");
            }
            return Status.OK_STATUS;
         }
      });
      return dialog;
   }

   private void handleAttributeTypeEdits(Artifact artifact, boolean isAdd, String title, Image image) throws OseeCoreException {
      String operation = isAdd ? "add" : "delete";
      AttributeType[] types =
            isAdd ? AttributeTypeUtil.getEmptyTypes(artifact) : AttributeTypeUtil.getTypesWithData(artifact);
      List<AttributeType> input = new ArrayList<AttributeType>(Arrays.asList(types));
      if (!isAdd) {
         for (AttributeType type : types) {
            if (type.getMinOccurrences() > 0 && artifact.getAttributeTypes().contains(type)) {
               input.remove(type);
            }
         }
      }
      if (input.isEmpty()) {
         MessageDialog.openInformation(PlatformUI.getWorkbench().getDisplay().getActiveShell(), title, String.format(
               "No attribute types available to %s.", operation));
      } else {
         CheckedTreeSelectionDialog dialog =
               createDialog(title, image, String.format("Select items to %s.", operation));
         dialog.setInput(input);
         int result = dialog.open();
         if (result == Window.OK) {
            Object[] objects = dialog.getResult();
            if (objects.length > 0) {
               for (Object object : objects) {
                  String attributeTypeName = ((AttributeType) object).getName();
                  if (isAdd) {
                     artifact.addAttributeFromString(attributeTypeName, "");
                  } else {
                     artifact.deleteAttributes(attributeTypeName);
                  }
               }
            }
         }
      }
   }

   private final class OpenAddAttributeTypeDialogAction extends Action {
      public OpenAddAttributeTypeDialogAction() {
         super();
         ImageDescriptor addImage = ImageManager.getImageDescriptor(FrameworkImage.ADD_GREEN);
         setImageDescriptor(addImage);
         setToolTipText("Opens a dialog to select which attribute type instances to create on the artifact");
      }

      @Override
      public void run() {
         try {
            Artifact artifact = editor.getEditorInput().getArtifact();
            handleAttributeTypeEdits(artifact, true, "Add Attribute Types",
                  ImageManager.getImage(FrameworkImage.ADD_GREEN));
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
            Artifact artifact = editor.getEditorInput().getArtifact();
            handleAttributeTypeEdits(artifact, false, "Delete Attribute Types",
                  ImageManager.getImage(FrameworkImage.DELETE));
         } catch (OseeCoreException ex) {
            OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }
}