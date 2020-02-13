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
package org.eclipse.osee.framework.ui.plugin.xnavigate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.internal.UiPluginConstants;

/**
 * @author Donald G. Dunne
 */
public class XNavigateContentProvider implements ITreeContentProvider {

   @Override
   public Object[] getChildren(Object parentElement) {
      if (parentElement instanceof Object[]) {
         return (Object[]) parentElement;
      }
      if (parentElement instanceof Collection) {
         return ((Collection<?>) parentElement).toArray();
      }
      if (parentElement instanceof XNavigateItem) {
         List<XNavigateItem> items = new ArrayList<>();
         items.addAll(((XNavigateItem) parentElement).getDynamicChildren());
         items.addAll(((XNavigateItem) parentElement).getChildren());
         return items.toArray(new Object[items.size()]);
      }
      return new Object[0];
   }

   @Override
   public Object getParent(Object element) {
      if (element instanceof XNavigateItem) {
         return ((XNavigateItem) element).getParent();
      }
      return null;
   }

   @Override
   public boolean hasChildren(Object element) {
      if (element instanceof XNavigateItem) {
         try {
            boolean hasChildren = ((XNavigateItem) element).hasChildren();
            return hasChildren;
         } catch (Exception ex) {
            OseeLog.log(UiPluginConstants.class, Level.SEVERE, ex);
         }
      }
      return getChildren(element).length > 0;
   }

   @Override
   public Object[] getElements(Object inputElement) {
      return getChildren(inputElement);
   }

   @Override
   public void dispose() {
      // do nothing
   }

   @Override
   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
      // do nothing
   }

}
