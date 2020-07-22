/*********************************************************************
 * Copyright (c) 2020 Boeing
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
package org.eclipse.osee.framework.ui.skynet.artifact.editor.tab.attr.action;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.StringAttribute;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactPromptChange;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.tab.attr.ArtEdAttrXViewer;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class EditAttributeAction extends Action {

   private final ArtEdAttrXViewer attrXViewer;

   public EditAttributeAction(ArtEdAttrXViewer attrXViewer) {
      super("Edit Attribute Value");
      this.attrXViewer = attrXViewer;
      setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.EDIT));
      setToolTipText("Edit Attribute Value");
   }

   @SuppressWarnings("unchecked")
   @Override
   public void run() {
      try {
         Collection<Attribute<?>> selected = attrXViewer.getSelectedAttributes();
         if (selected.size() != 1) {
            AWorkbench.popup("Select a single attribute to delete.");
            return;
         }
         Attribute<?> attribute = selected.iterator().next();
         Artifact artifact = attrXViewer.getArtifact();
         if (artifact.isReadOnly()) {
            AWorkbench.popup("Artifact is READ Only");
            return;
         }
         if (attribute.getAttributeType().getMaxOccurrences() == 1) {
            ArtifactPromptChange.promptChangeAttribute(attribute.getAttributeType(), Arrays.asList(artifact), true);
         } else if (attribute instanceof StringAttribute) {
            EntryDialog dialog = new EntryDialog("Change Value", "New Value");
            dialog.setEntry(((Attribute<String>) attribute).getValue());
            dialog.setFillVertically(true);
            if (dialog.open() == Window.OK) {
               SkynetTransaction transaction =
                  TransactionManager.createTransaction(artifact.getBranch(), "ArtEdAttr: Change Attribute");
               ((Attribute<String>) attribute).setValue(dialog.getEntry());
               transaction.addArtifact(artifact);
               transaction.execute();
            }
         } else {
            AWorkbench.popup("Unhandled Attribute Type");
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

}
