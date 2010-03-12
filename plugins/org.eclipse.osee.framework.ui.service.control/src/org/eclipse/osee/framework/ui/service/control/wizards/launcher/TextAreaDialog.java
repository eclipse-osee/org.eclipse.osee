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
package org.eclipse.osee.framework.ui.service.control.wizards.launcher;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

public class TextAreaDialog extends MessageDialog {

   private StyledText cmdText;
   private String groupTitle;
   private String content;

   public TextAreaDialog(String groupTitle, String content, Shell parentShell, String dialogTitle, Image dialogTitleImage, String dialogMessage, int dialogImageType, String[] dialogButtonLabels, int defaultIndex) {
      super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, dialogButtonLabels,
            defaultIndex);
      this.groupTitle = groupTitle;
      this.content = content;
   }

   public static void open(String groupTitle, String content, Shell parent, String title, String message) {
      TextAreaDialog dialog = new TextAreaDialog(groupTitle, content, parent, title, null, // accept
            // the
            // default
            // window
            // icon
            message, INFORMATION, new String[] {IDialogConstants.OK_LABEL}, 0);
      // ok is the default
      dialog.open();
      return;
   }

   @Override
   protected Control createCustomArea(Composite parent) {

      Group userinfo = new Group(parent, SWT.NONE);
      userinfo.setText(groupTitle);

      GridLayout gridLayout = new GridLayout();
      gridLayout.numColumns = 2;
      userinfo.setLayout(gridLayout);

      GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
      gridData.horizontalSpan = 1;
      gridData.grabExcessHorizontalSpace = true;
      userinfo.setLayoutData(gridData);

      cmdText = new StyledText(userinfo, SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI | SWT.BORDER | SWT.WRAP);
      cmdText.setEditable(false);
      GridData gd = new GridData();
      gd.grabExcessHorizontalSpace = true;
      gd.horizontalAlignment = GridData.FILL;
      gd.horizontalSpan = 2;
      gd.heightHint = 150;
      cmdText.setLayoutData(gd);
      cmdText.setText(content);

      return super.createCustomArea(parent);
   }

}
