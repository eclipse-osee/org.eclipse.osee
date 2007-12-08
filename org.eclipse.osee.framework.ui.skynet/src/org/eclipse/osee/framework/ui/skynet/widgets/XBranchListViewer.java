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
package org.eclipse.osee.framework.ui.skynet.widgets;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;

/**
 * @author Jeff C. Phillips
 */
public class XBranchListViewer extends XTypeListViewer {

   private static final String NAME = "XBranchListViewer";

   /**
    * @param name
    */
   public XBranchListViewer() {
      super(NAME);

      setContentProvider(new ContentProvider());

      ArrayList<Object> input = new ArrayList<Object>(1);
      input.add(BranchPersistenceManager.getInstance());

      setInput(input);
      setMultiSelect(false);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XListViewer#getData()
    */
   @Override
   public Object getData() {
      Object object = null;
      ArrayList<Object> datas = new ArrayList<Object>();
      datas.addAll(getSelected());

      if (!datas.isEmpty()) {
         object = datas.iterator().next();
      }
      return object;
   }

   public class ContentProvider implements IStructuredContentProvider {

      /**
       * Returns the elements in the input, which must be either an array or a <code>Collection</code>.
       */
      @SuppressWarnings("unchecked")
      public Object[] getElements(Object inputElement) {
         if (inputElement instanceof Collection) {
            Collection<Object> collection = (Collection<Object>) inputElement;

            if (!collection.isEmpty()) {
               Object object = collection.iterator().next();

               if (object instanceof BranchPersistenceManager) {
                  try {
                     return ((BranchPersistenceManager) object).getBranches().toArray();
                  } catch (SQLException ex) {
                  }
               }
            }
         }
         return new Object[0];
      }

      /**
       * This implementation does nothing.
       */
      public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
         // do nothing.
      }

      /**
       * This implementation does nothing.
       */
      public void dispose() {
         // do nothing.
      }
   }
}
