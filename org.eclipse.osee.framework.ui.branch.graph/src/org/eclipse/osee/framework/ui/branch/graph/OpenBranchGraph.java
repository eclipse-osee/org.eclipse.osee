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
package org.eclipse.osee.framework.ui.branch.graph;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.osee.framework.ui.branch.graph.core.BranchGraphEditor;
import org.eclipse.osee.framework.ui.branch.graph.core.BranchGraphEditorInput;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.AbstractSelectionChangedHandler;
import org.eclipse.ui.PlatformUI;

/**
 * @author Roberto E. Escobar
 */
public class OpenBranchGraph extends AbstractSelectionChangedHandler {

   public OpenBranchGraph() {
      super();
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.
    * ExecutionEvent)
    */
   @Override
   public Object execute(ExecutionEvent event) throws ExecutionException {
      try {
         PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(new BranchGraphEditorInput(),
               BranchGraphEditor.EDITOR_ID);
      } catch (Exception ex) {
         throw new ExecutionException("Error opening Branch Graph Editor", ex);
      }
      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.core.commands.AbstractHandler#isEnabled()
    */
   @Override
   public boolean isEnabled() {
      return true;
   }
}
