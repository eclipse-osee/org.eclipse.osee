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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author Theron Virgin
 */

public class EmbeddedBooleanEditor {
   Button booleanValue;
   String dialogMessage;

   public EmbeddedBooleanEditor(String dialogMessage) {
      this.dialogMessage = dialogMessage;
   }

   public void createEditor(Composite composite, GridData gd) {

      new Label(composite, SWT.NONE).setText(dialogMessage);
      booleanValue = new Button(composite, SWT.CHECK);
      booleanValue.setText("");
      composite.layout();
   }

   public boolean getEntry() {
      return booleanValue.getSelection();
   }

   public void setEntry(boolean entry) {
      booleanValue.setSelection(entry);
   }

}
