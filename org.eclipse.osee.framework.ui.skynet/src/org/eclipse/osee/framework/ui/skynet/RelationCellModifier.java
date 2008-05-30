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

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.access.PermissionList;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.swt.widgets.Item;

/**
 * @author Ryan D. Brooks
 */
public class RelationCellModifier implements ICellModifier {
   private TreeViewer treeViewer;

   private PermissionList pList;

   /**
    * 
    */
   public RelationCellModifier(TreeViewer treeViewer) {
      super();
      this.treeViewer = treeViewer;
      pList = new PermissionList();
      //      pList.addPermission(Permission.PermPermissionEnum.EDITREQUIREMENT);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.viewers.ICellModifier#canModify(java.lang.Object, java.lang.String)
    */
   public boolean canModify(Object element, String property) {
      try {
         SkynetGuiPlugin.securityManager.checkPermission(SkynetAuthentication.getUser(), pList);
      } catch (SecurityException ex) {
         ex.printStackTrace();
         return false;
      }
      return element instanceof RelationLink;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.viewers.ICellModifier#getValue(java.lang.Object, java.lang.String)
    */
   public Object getValue(Object element, String property) {
      RelationLink relLink = (RelationLink) element;
      return relLink.getRationale();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.viewers.ICellModifier#modify(java.lang.Object, java.lang.String,
    *      java.lang.Object)
    */
   public void modify(Object element, String property, Object value) {
      // Note that it is possible for an SWT Item to be passed instead of the model element.
      if (element instanceof Item) {
         element = ((Item) element).getData();
      }
      RelationLink relLink = (RelationLink) element;
      relLink.setRationale((String) value, true);
      treeViewer.update(element, null);
   }
}
