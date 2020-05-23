/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ote.ui.define.panels;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * @author Roberto E. Escobar
 */
public class CommentComposite extends Composite {

   protected Text textArea;
   protected String message;

   public CommentComposite(Composite parent, int style) {
      super(parent, style);
      this.message = "";
      createControls();
   }

   public String getMessage() {
      return message;
   }

   public void setMessage(String message) {
      textArea.setText(message);
   }

   private void createControls() {
      GridLayout layout = new GridLayout();
      layout.marginHeight = 0;
      layout.marginWidth = 0;
      this.setLayout(layout);

      this.textArea = new Text(this, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
      GridData data = new GridData(GridData.FILL_BOTH);
      data.heightHint = 80;
      this.textArea.setLayoutData(data);
      this.textArea.selectAll();

      this.textArea.addModifyListener(new ModifyListener() {
         @Override
         public void modifyText(ModifyEvent e) {
            message = textArea.getText();
         }
      });
   }
}
