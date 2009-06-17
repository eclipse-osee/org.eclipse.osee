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
package org.eclipse.osee.ote.ui.test.manager.util;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ote.ui.test.manager.core.TestManagerEditor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.ListSelectionDialog;

/**
 * @author Roberto E. Escobar
 */
public class TestManagerSelectDialog {
   private final static String MESSAGE = "Select A Test Manager";
   private ListSelectionDialog listSelectionDialog;
   private LabelProvider labelProvider;

   private TestManagerSelectDialog() {
      this.labelProvider = new LabelProvider() {
         @Override
         public String getText(Object element) {
            if (element instanceof TestManagerEditor) {
               return ((TestManagerEditor) element).getPartName();
            }
            return "Unknown element type";
         }
      };
      TestManagerEditor[] input = PluginUtil.getTestManagers();
      this.listSelectionDialog =
            new ListSelectionDialog(Display.getCurrent().getActiveShell(), input, new ArrayContentProvider(),
                  labelProvider, MESSAGE);
      this.listSelectionDialog.setTitle(MESSAGE);
      if (input.length > 0) {
         this.listSelectionDialog.setInitialSelections(new Object[] {input[0]});
      }
   }

   public int open() {
      return listSelectionDialog.open();
   }

   public TestManagerEditor[] getResult() {
      List<TestManagerEditor> tmes = new ArrayList<TestManagerEditor>();
      for (Object object : listSelectionDialog.getResult()) {
         tmes.add((TestManagerEditor) object);
      }
      return tmes.toArray(new TestManagerEditor[tmes.size()]);
   }

   public static TestManagerEditor[] getTestManagerFromUser() {
      TestManagerEditor[] toReturn = null;
      TestManagerSelectDialog dialog = new TestManagerSelectDialog();
      int result = dialog.open();
      if (result == Window.OK) {
         toReturn = dialog.getResult();
      }
      return toReturn != null ? toReturn : new TestManagerEditor[0];
   }
}
