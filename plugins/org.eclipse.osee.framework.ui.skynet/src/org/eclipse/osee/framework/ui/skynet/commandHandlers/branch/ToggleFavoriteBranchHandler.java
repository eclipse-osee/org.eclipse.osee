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
package org.eclipse.osee.framework.ui.skynet.commandHandlers.branch;

import java.util.List;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEvent;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEventType;
import org.eclipse.osee.framework.ui.plugin.util.CommandHandler;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;

/**
 * @author Jeff C. Phillips
 */
public class ToggleFavoriteBranchHandler extends CommandHandler {
   public static final String COMMAND_ID =
      "org.eclipse.osee.framework.ui.skynet.commandHandlers.branch.ToggleFavoriteBranchHandler";

   @Override
   public Object executeWithException(ExecutionEvent event, IStructuredSelection selection) throws OseeCoreException {
      BranchId selectedBranch = Handlers.getBranchesFromStructuredSelection(selection).iterator().next();

      User user = UserManager.getUser();
      // Make sure we have latest artifact
      user.reloadAttributesAndRelations();
      user.toggleFavoriteBranch(selectedBranch);
      OseeEventManager.kickBranchEvent(this, new BranchEvent(BranchEventType.FavoritesUpdated, selectedBranch));

      return null;
   }

   @Override
   public boolean isEnabledWithException(IStructuredSelection structuredSelection) {
      List<? extends BranchId> branches = Handlers.getBranchesFromStructuredSelection(structuredSelection);
      return branches.size() == 1;
   }
}