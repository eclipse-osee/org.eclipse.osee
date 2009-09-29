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
package org.eclipse.osee.framework.ui.skynet.widgets.dialog;

import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Donald G. Dunne
 */
public class ArtifactTypeFilteredTreeEntryDialog extends ArtifactTypeFilteredTreeDialog {

   private String entryValue = null;
   private final String entryName;
   private XText xText = null;

   /**
    * @param title
    * @param message
    * @param artifactTypes
    */
   public ArtifactTypeFilteredTreeEntryDialog(String title, String message, String entryName) {
      super(title, message);
      this.entryName = entryName;
   }

   @Override
   protected void createPreCustomArea(Composite parent) {
      super.createPreCustomArea(parent);
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
   }

   /**
    * @return the entryValue
    */
   public String getEntryValue() {
      return entryValue;
   }

   /**
    * @param entryValue the entryValue to set
    */
   public void setEntryValue(String entryValue) {
      this.entryValue = entryValue;
   }

   @Override
   protected Result isComplete() {
      if (entryValue == null || entryValue.equals("")) {
         return new Result("Must enter Artifact name.");
      }
      return super.isComplete();
   }

}
