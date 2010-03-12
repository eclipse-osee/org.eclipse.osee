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

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.osee.framework.ui.data.model.editor.core.ODMEditor;
import org.eclipse.osee.framework.ui.data.model.editor.core.ODMEditorInput;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.AbstractSelectionChangedHandler;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

/**
 * @author Roberto E. Escobar
 */
public class OpenODMEditor extends AbstractSelectionChangedHandler {

   public OpenODMEditor() {
      super();
   }

   @Override
   public Object execute(ExecutionEvent event) throws ExecutionException {
      IEditorPart editorPart = null;
      try {
         editorPart =
               PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(new ODMEditorInput(),
                     ODMEditor.EDITOR_ID);
      } catch (Exception ex) {
         throw new ExecutionException("Error opening Branch Editor", ex);
      }
      return editorPart;
   }

   @Override
   public boolean isEnabled() {
      return true;
   }
}
