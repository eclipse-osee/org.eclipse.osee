/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.testscript;

import java.io.InputStream;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.orcs.rest.model.transaction.TransactionBuilderData;
import org.eclipse.osee.testscript.internal.ScriptDefToken;

/**
 * @author Ryan T. Baldwin
 */
public interface TmoImportApi {

   ScriptDefToken getScriptDefinition(InputStream stream);

   TransactionBuilderData getTxBuilderData(BranchId branch, ScriptDefToken scriptDef);

   TransactionBuilderData getTxBuilderData(BranchId branch, TransactionBuilderData data, ScriptDefToken scriptDef);

   TransactionBuilderData getTxBuilderData(BranchId branch, TransactionBuilderData data, ScriptDefToken scriptDef,
      boolean reset);
}
