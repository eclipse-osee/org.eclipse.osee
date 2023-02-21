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

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.tab.attr.ArtEdAttrXViewer;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxAttributeTypeDialog;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class AddAttributeAction extends Action {

   private final ArtEdAttrXViewer attrXViewer;

   public AddAttributeAction(ArtEdAttrXViewer attrXViewer) {
      super("Add Attribute");
      this.attrXViewer = attrXViewer;
      setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.ADD_GREEN));
      setToolTipText("Add Attribute");
   }

   @Override
   public void run() {
      try {
         Artifact artifact = attrXViewer.getArtifact();
         Collection<AttributeTypeToken> selectableTypes = new ArrayList<>();
         for (AttributeTypeToken attrType : artifact.getAttributeTypes()) {
            if (artifact.getRemainingAttributeCount(attrType) > 0) {
               selectableTypes.add(attrType);
            }
         }
         FilteredCheckboxAttributeTypeDialog dialog =
            new FilteredCheckboxAttributeTypeDialog("Select Attribute Types", "Select attribute types to display.");
         dialog.setSelectable(selectableTypes);
         if (dialog.open() == Window.OK) {
            Collection<AttributeTypeToken> checked = dialog.getChecked();
            if (!checked.isEmpty()) {
               AttributeTypeToken attrType = checked.iterator().next();
               EntryDialog dialog2 =
                  new EntryDialog("Add Attribute", String.format("Enter %s attribute value", attrType.getName()));
               if (dialog2.open() == Window.OK) {
                  SkynetTransaction transaction =
                     TransactionManager.createTransaction(artifact.getBranch(), "ArtEdAttr: Add Attribute");
                  artifact.addAttributeFromString(attrType, dialog2.getEntry());
                  transaction.addArtifact(artifact);
                  transaction.execute();
               }
            }
         }

      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

   }
}
