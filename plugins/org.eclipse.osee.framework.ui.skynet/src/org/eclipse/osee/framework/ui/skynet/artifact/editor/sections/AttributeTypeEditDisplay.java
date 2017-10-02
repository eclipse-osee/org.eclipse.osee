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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.KeyedImage;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;

public final class AttributeTypeEditDisplay implements AttributeTypeEditPresenter.Display {
   private final AttributesFormSection attributesForm;

   public AttributeTypeEditDisplay(AttributesFormSection attributesForm) {
      this.attributesForm = attributesForm;
   }

   @Override
   public void showInformation(String title, String message) {
      MessageDialog.openInformation(AWorkbench.getActiveShell(), title, message);
   }

   private CheckedTreeSelectionDialog createDialog(String title, String message, KeyedImage keyedImage) {
      CheckedTreeSelectionDialog dialog = new CheckedTreeSelectionDialog(AWorkbench.getActiveShell(),
         new LabelProvider(), new ArrayTreeContentProvider());
      dialog.setTitle(title);
      Image image = ImageManager.getImage(keyedImage);
      dialog.setImage(image);
      dialog.setMessage(message);
      dialog.setValidator(new ISelectionStatusValidator() {

         @Override
         public IStatus validate(Object[] selection) {
            if (selection.length == 0) {
               return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
                  "Select at least one item or click cancel to exit.");
            }
            return Status.OK_STATUS;
         }
      });
      return dialog;
   }

   @Override
   public Collection<AttributeTypeToken> getSelections(OperationType operationType, String title, String message, List<AttributeTypeToken> input) {
      Collection<AttributeTypeToken> toReturn = Collections.emptyList();
      CheckedTreeSelectionDialog dialog = createDialog(title, message, getImage(operationType));
      dialog.setInput(input);
      int result = dialog.open();
      if (result == Window.OK) {
         toReturn = new ArrayList<>();
         for (Object object : dialog.getResult()) {
            if (object instanceof AttributeTypeToken) {
               toReturn.add((AttributeTypeToken) object);
            }
         }
      }
      return toReturn;
   }

   private KeyedImage getImage(OperationType operationType) {
      KeyedImage toReturn = null;
      switch (operationType) {
         case ADD_ITEM:
            toReturn = FrameworkImage.ADD_GREEN;
            break;
         case REMOVE_ITEM:
            toReturn = FrameworkImage.DELETE;
            break;
         default:
            break;
      }
      return toReturn;
   }

   @Override
   public void addWidgetFor(Collection<AttributeTypeToken> attributeTypes)  {
      attributesForm.getAttributeFormPart().addWidgetForAttributeType(attributeTypes);
   }

   @Override
   public void removeWidgetFor(Collection<AttributeTypeToken> attributeTypes) {
      attributesForm.getAttributeFormPart().removeWidgetForAttributeType(attributeTypes);
   }
}