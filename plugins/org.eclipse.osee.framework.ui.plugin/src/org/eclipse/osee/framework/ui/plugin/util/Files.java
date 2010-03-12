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
package org.eclipse.osee.framework.ui.plugin.util;

import java.io.File;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Robert A. Fisher
 */
public class Files {

   public static File selectFile(Shell shell, int swtType, String... extensions) {
      FileDialog dialog = new FileDialog(shell, swtType | SWT.SINGLE);
      dialog.setFilterExtensions(extensions);
      dialog.setFilterPath(AWorkspace.getWorkspacePath());

      String path = dialog.open();

      if (path != null) {
         return new File(path);
      } else {
         return null;
      }
   }
}
