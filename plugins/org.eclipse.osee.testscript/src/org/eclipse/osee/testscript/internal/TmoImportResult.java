/*********************************************************************
 * Copyright (c) 2024 Boeing
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
package org.eclipse.osee.testscript.internal;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.TransactionResult;

public class TmoImportResult {

   private final TransactionResult txResult;
   private final List<String> workflows;

   public TmoImportResult(TransactionResult txResult) {
      this.txResult = txResult;
      this.workflows = new LinkedList<>();
   }

   public TransactionResult getTxResult() {
      return txResult;
   }

   public List<String> getWorkflows() {
      return workflows;
   }

}
