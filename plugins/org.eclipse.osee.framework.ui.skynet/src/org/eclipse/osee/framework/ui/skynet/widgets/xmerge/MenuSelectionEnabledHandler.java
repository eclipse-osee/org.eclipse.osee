/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.framework.ui.skynet.widgets.xmerge;

import java.util.List;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.conflict.AttributeConflict;
import org.eclipse.osee.framework.skynet.core.conflict.Conflict;
import org.eclipse.osee.framework.ui.plugin.util.AbstractSelectionEnabledHandler;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;

abstract class MenuSelectionEnabledHandler extends AbstractSelectionEnabledHandler {

   public MenuSelectionEnabledHandler(MenuManager menuManager) {
      super(menuManager);
   }

   protected MenuSelectionEnabledHandler() {
      // for test only
      // since we don't want to have to setup the menuManager in the test
   }

   @Override
   protected Object executeWithException(ExecutionEvent event, IStructuredSelection structuredSelection)  {
      AttributeConflict attributeConflict = getConflictFromSelection(structuredSelection);
      if (attributeConflict != null) {
         executeWithException(attributeConflict);
      }
      return null;
   }

   /**
    * This method will always be invoked with a single AttributeConflict that isEditable
    * 
    * @param conflict
    * @return
    * 
    */
   abstract void executeWithException(AttributeConflict attributeConflict) ;

   @Override
   public boolean isEnabledWithException(IStructuredSelection structuredSelection) {
      return getConflictFromSelection(structuredSelection) != null;
   }

   AttributeConflict getConflictFromSelection(IStructuredSelection structuredSelection) {
      AttributeConflict attributeConflict = null;
      List<Conflict> conflicts = Handlers.getConflictsFromStructuredSelection(structuredSelection);
      // we only want to enable the menu if the selection size is one
      if (conflicts.size() == 1) {
         Conflict conflict = conflicts.iterator().next();
         if (conflict.getStatus().isEditable()) {
            if (conflict instanceof AttributeConflict) {
               attributeConflict = (AttributeConflict) conflict;
            }
         }
      }
      return attributeConflict;
   }
}
