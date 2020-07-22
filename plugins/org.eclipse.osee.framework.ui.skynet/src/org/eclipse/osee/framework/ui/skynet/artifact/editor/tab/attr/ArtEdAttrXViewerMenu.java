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

package org.eclipse.osee.framework.ui.skynet.artifact.editor.tab.attr;

import java.util.List;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.tab.attr.action.AddAttributeAction;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.tab.attr.action.DeleteAttributeAction;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.tab.attr.action.EditAttributeAction;

/**
 * @author Donald G. Dunne
 */
public class ArtEdAttrXViewerMenu {

   private Action addAttributeAction, editAttributeAction, deleteAttributeAction;
   private final ArtEdAttrXViewer attrXViewer;
   private final Artifact artifact;

   public ArtEdAttrXViewerMenu(ArtEdAttrXViewer attrXViewer, Artifact artifact) {
      this.attrXViewer = attrXViewer;
      this.artifact = artifact;
   }

   public void createMenuActions() {
      attrXViewer.setColumnMultiEditEnabled(true);
      MenuManager mm = attrXViewer.getMenuManager();
      mm.createContextMenu(attrXViewer.getControl());

      addAttributeAction = new AddAttributeAction(attrXViewer);
      editAttributeAction = new EditAttributeAction(attrXViewer);
      deleteAttributeAction = new DeleteAttributeAction(attrXViewer);

   }

   public void updateEditMenuActions() {
      MenuManager mm = attrXViewer.getMenuManager();
      // EDIT MENU BLOCK
      mm.insertBefore(XViewer.MENU_GROUP_PRE, addAttributeAction);
      addAttributeAction.setEnabled(true);
      mm.insertBefore(XViewer.MENU_GROUP_PRE, editAttributeAction);
      editAttributeAction.setEnabled(isEditable());
      mm.insertBefore(XViewer.MENU_GROUP_PRE, deleteAttributeAction);
      deleteAttributeAction.setEnabled(isEditable());
   }

   private boolean isEditable() {
      List<Attribute<?>> attributes = attrXViewer.getSelectedAttributes();
      return !artifact.isReadOnly() && (attributes.size() == 1);
   }

}
