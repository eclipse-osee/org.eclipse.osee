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
package org.eclipse.osee.framework.ui.skynet;

import java.util.logging.Level;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeSideSorter;
import org.eclipse.swt.widgets.Item;

/**
 * @author Ryan D. Brooks
 */
public class RelationCellModifier implements ICellModifier {
   private final TreeViewer treeViewer;

   /**
    * 
    */
   public RelationCellModifier(TreeViewer treeViewer) {
      super();
      this.treeViewer = treeViewer;
      //      pList.addPermission(Permission.PermPermissionEnum.EDITREQUIREMENT);
   }

   public boolean canModify(Object element, String property) {
      boolean isModifiable = true;

      if (element instanceof RelationTypeSideSorter) {
         try {
            isModifiable = !((RelationTypeSideSorter) element).getArtifact().isReadOnly();
         } catch (OseeCoreException ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         }
      }
      return isModifiable;
   }

   public Object getValue(Object element, String property) {
      WrapperForRelationLink relLink = (WrapperForRelationLink) element;
      String rationale = "";
      try {
         rationale =
               RelationManager.getRelationRationale(relLink.getArtifactA(), relLink.getArtifactB(),
                     relLink.getRelationType());
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
      return rationale;
   }

   public void modify(Object element, String property, Object value) {
      // Note that it is possible for an SWT Item to be passed instead of the model element.
      if (element instanceof Item) {
         element = ((Item) element).getData();
      }
      WrapperForRelationLink relLink = (WrapperForRelationLink) element;
      try {
         RelationManager.setRelationRationale(relLink.getArtifactA(), relLink.getArtifactB(),
               relLink.getRelationType(), value.toString());
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
      treeViewer.update(element, null);
   }
}
