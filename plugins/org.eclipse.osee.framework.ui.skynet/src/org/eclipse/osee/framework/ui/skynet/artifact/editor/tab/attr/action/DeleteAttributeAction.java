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

import java.util.Collection;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.tab.attr.ArtEdAttrXViewer;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class DeleteAttributeAction extends Action {

   private final ArtEdAttrXViewer attrXViewer;

   public DeleteAttributeAction(ArtEdAttrXViewer attrXViewer) {
      super("Delete Attribute");
      this.attrXViewer = attrXViewer;
      setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.X_RED));
      setToolTipText("Delete Attribute");
   }

   @Override
   public void run() {
      try {
         Collection<Attribute<?>> selected = attrXViewer.getSelectedAttributes();
         if (selected.size() != 1) {
            AWorkbench.popup("Select a single attribute to delete.");
            return;
         }
         Attribute<?> attribute = selected.iterator().next();
         if (!attribute.canDelete()) {
            AWorkbench.popup("Lower limit met, can not delete. Change value instead.");
            return;
         }

         if (MessageDialog.openConfirm(Displays.getActiveShell(), "Delete Attribute", String.format(
            "Delete Attribute [%s] value [%s]", attribute.getAttributeType().getName(), attribute.getValue()))) {
            Artifact artifact = attrXViewer.getArtifact();
            artifact.deleteAttribute(AttributeId.valueOf(selected.iterator().next().getId()));
            artifact.persist("ArtEdAttr: Delete Attribute");
         }

      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

   }
}
