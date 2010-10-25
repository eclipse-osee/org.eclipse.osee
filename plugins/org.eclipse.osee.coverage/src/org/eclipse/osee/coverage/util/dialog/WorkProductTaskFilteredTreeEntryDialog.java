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
package org.eclipse.osee.coverage.util.dialog;

import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Donald G. Dunne
 */
public class WorkProductTaskFilteredTreeEntryDialog extends WorkProductTaskFilteredTreeDialog {

   private String entryValue = null;
   private final String entryName;
   private XText xText = null;

   public WorkProductTaskFilteredTreeEntryDialog(String title) {
      super(title, "Enter a NEW task name OR select an existing task using filter.");
      this.entryName = "New Task Name";
   }

   @Override
   protected void createPostCustomArea(Composite parent) {
      xText = new XText(entryName);
      if (entryValue != null) {
         xText.setText(entryValue);
      }
      xText.addXModifiedListener(new XModifiedListener() {
         @Override
         public void widgetModified(XWidget widget) {
            entryValue = xText.get();
            updateStatusLabel();
         }
      });
      xText.createWidgets(parent, 2);

      super.createPreCustomArea(parent);
   }

   public String getEntryValue() {
      return entryValue;
   }

   public void setEntryValue(String entryValue) {
      this.entryValue = entryValue;
   }

   @Override
   protected Result isComplete() {
      if (super.isComplete().isFalse() && !Strings.isValid(entryValue)) {
         return new Result("Must either select task or enter new task title.");
      }
      return Result.TrueResult;
   }

}
