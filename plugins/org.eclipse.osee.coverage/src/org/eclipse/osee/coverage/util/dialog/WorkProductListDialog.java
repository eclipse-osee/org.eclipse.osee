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
package org.eclipse.osee.coverage.util.dialog;

import java.util.Collection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public class WorkProductListDialog extends org.eclipse.ui.dialogs.ListDialog {

   public WorkProductListDialog(String title, String message) {
      super(Displays.getActiveShell());
      setTitle(title);
      setMessage(message);
      setContentProvider(new IStructuredContentProvider() {
         @Override
         public Object[] getElements(Object arg0) {
            return ((Collection<?>) arg0).toArray();
         }

         @Override
         public void dispose() {
            // do nothing
         }

         @Override
         public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
            // do nothing
         }
      });
      setLabelProvider(new LabelProvider() {
         @Override
         public String getText(Object element) {
            return element.toString();
         }
      });

   }
}
