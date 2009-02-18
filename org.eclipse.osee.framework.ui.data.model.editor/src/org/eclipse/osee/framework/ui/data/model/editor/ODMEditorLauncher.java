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
package org.eclipse.osee.framework.ui.data.model.editor;

import java.util.logging.Level;
import org.eclipse.core.runtime.IPath;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.data.model.editor.core.ODMEditor;
import org.eclipse.osee.framework.ui.data.model.editor.core.ODMEditorInput;
import org.eclipse.ui.IEditorLauncher;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

/**
 * @author Roberto E. Escobar
 */
public class ODMEditorLauncher implements IEditorLauncher {

   /* (non-Javadoc)
    * @see org.eclipse.ui.IEditorLauncher#open(org.eclipse.core.runtime.IPath)
    */
   @Override
   public void open(IPath file) {
      try {
         IWorkbenchPage workbenchPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

         workbenchPage.openEditor(new ODMEditorInput(), ODMEditor.EDITOR_ID);
      } catch (Exception ex) {
         OseeLog.log(ODMEditorActivator.class, Level.WARNING, ex);
      }
   }
}
