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
package org.eclipse.osee.framework.ui.jdk.swing;

import java.awt.Component;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.swing.JOptionPane;

public class DialogSupport {

   public static void popupExceptionDialog(Component parent, String dlgTitle, Exception ex) {
      PrintWriter ps = new PrintWriter(new StringWriter());
      ex.printStackTrace(ps);
      JOptionPane.showMessageDialog(parent, ex.getMessage() + "\n" + ps, dlgTitle, JOptionPane.ERROR_MESSAGE);
   }
}
