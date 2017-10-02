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
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.ui.plugin.util.CommandHandler;
import org.eclipse.osee.framework.ui.skynet.action.PurgeTransactionAction;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;

/**
 * @author Jeff C. Phillips
 */
public class PurgeTransactionHandler extends CommandHandler {

   @Override
   public Object executeWithException(ExecutionEvent event, IStructuredSelection selection)  {
      List<TransactionToken> transactions = Handlers.getTransactionsFromStructuredSelection(selection);

      PurgeTransactionAction action = new PurgeTransactionAction(transactions);
      action.run();

      return null;
   }

   @Override
   public boolean isEnabledWithException(IStructuredSelection structuredSelection)  {
      List<TransactionToken> transactions = Handlers.getTransactionsFromStructuredSelection(structuredSelection);
      return transactions.size() > 0 && AccessControlManager.isOseeAdmin();
   }
}