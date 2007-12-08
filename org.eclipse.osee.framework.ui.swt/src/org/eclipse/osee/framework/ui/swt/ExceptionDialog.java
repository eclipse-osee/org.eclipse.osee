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
package org.eclipse.osee.framework.ui.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 * This class represents a dialog box that displays an exception that has occurred at runtime
 * 
 * @author Ken J. Aguilar
 */
public class ExceptionDialog {

   private final MessageBox messageBox;

   public ExceptionDialog(final Shell shell, final Throwable e, final String additionalInfo) {
      final String indent = "\n   ";
      messageBox = new MessageBox(shell, SWT.ICON_ERROR);
      messageBox.setText(e.getClass().getName());
      final StringBuilder msg = new StringBuilder(2048);
      msg.append(e.getMessage());
      msg.append("\n\nStack Trace:");
      for (StackTraceElement element : e.getStackTrace()) {
         msg.append(indent);
         msg.append(element.toString());
      }
      msg.append("\n");
      msg.append(additionalInfo);
      messageBox.setMessage(msg.toString());
   }

   public ExceptionDialog(final Shell shell, final Throwable e) {
      this(shell, e, "");
   }

   public void open() {
      messageBox.open();
   }
}
