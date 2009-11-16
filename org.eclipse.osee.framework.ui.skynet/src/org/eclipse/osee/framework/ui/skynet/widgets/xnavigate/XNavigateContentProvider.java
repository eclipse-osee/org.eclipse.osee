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
package org.eclipse.osee.framework.ui.skynet.widgets.xnavigate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;

/**
 * @author Donald G. Dunne
 */
public class XNavigateContentProvider implements ITreeContentProvider {

   public XNavigateContentProvider() {
      super();
   }

   @SuppressWarnings("unchecked")
   public Object[] getChildren(Object parentElement) {
      if (parentElement instanceof Object[]) return (Object[]) parentElement;
      if (parentElement instanceof Collection) return ((Collection) parentElement).toArray();
      if (parentElement instanceof XNavigateItem) {
         List<XNavigateItem> items = new ArrayList<XNavigateItem>();
         items.addAll(((XNavigateItem) parentElement).getDynamicChildren());
         items.addAll(((XNavigateItem) parentElement).getChildren());
         return items.toArray(new Object[items.size()]);
      }
      return new Object[0];
   }

   public Object getParent(Object element) {
      if (element instanceof XNavigateItem) return ((XNavigateItem) element).getParent();
      return null;
   }

   public boolean hasChildren(Object element) {
      if (element instanceof XNavigateItem) {
         try {
            return ((XNavigateItem) element).hasChildren();
         } catch (OseeCoreException ex) {
            OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE, ex);
         }
      }
      return getChildren(element).length > 0;
   }

   public Object[] getElements(Object inputElement) {
      return getChildren(inputElement);
   }

   public void dispose() {
   }

   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
   }

}
