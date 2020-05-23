/*********************************************************************
 * Copyright (c) 2010 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.action;

import java.util.List;
import org.eclipse.osee.framework.core.data.TransactionId;

public interface ITransactionRecordSelectionProvider {

   public List<TransactionId> getSelectedTransactionRecords();

   public void refreshUI(List<TransactionId> records);
}
