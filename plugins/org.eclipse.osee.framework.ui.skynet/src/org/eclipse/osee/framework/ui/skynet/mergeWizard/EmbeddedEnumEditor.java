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
package org.eclipse.osee.framework.ui.skynet.mergeWizard;


import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author Theron Virgin
 */
public class EmbeddedEnumEditor {
  
   private String dialogMessage;
   private Combo comboBox;
   public final static String NO_SELECTION = "";
    
   public EmbeddedEnumEditor(String dialogMessage) {
      this.dialogMessage = dialogMessage;
   }

   public boolean createEditor(Composite composite) {
      new Label(composite, SWT.NONE).setText(dialogMessage);
      
      comboBox = new Combo(composite, SWT.READ_ONLY);
      comboBox.setBounds(50, 50, 650, 65);

      comboBox.add(NO_SELECTION);
      
      return true;
   }
   
   public void addSelectionChoice(String choice){
      if (choice != null)
         comboBox.add(choice);
   }

   public String getSelected() {
      int index = comboBox.getSelectionIndex();
      if (index == -1)
         return NO_SELECTION;
      else
         return comboBox.getItem(index);
   }
   
   public void setSelected(String selection){
      int index = comboBox.indexOf(selection);
      if (index == -1)
         AWorkbench.popup("ERROR","Attempting to set Enumeration to invalid value " + selection);
      comboBox.select(index);
   }
   


}
