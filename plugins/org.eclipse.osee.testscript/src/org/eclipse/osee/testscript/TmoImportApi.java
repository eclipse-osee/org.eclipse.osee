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

import java.io.File;
import java.io.InputStream;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionResult;
import org.eclipse.osee.orcs.rest.model.transaction.TransactionBuilderData;
import org.eclipse.osee.testscript.internal.ScriptDefToken;

/**
 * @author Ryan T. Baldwin
 */
public interface TmoImportApi {

   ScriptDefToken getScriptDefinition(InputStream stream, ArtifactId ciSetId);

   ScriptDefToken getScriptDefinition(File file, ArtifactId ciSetId);

   TransactionBuilderData getTxBuilderData(BranchId branch, ScriptDefToken scriptDef);

   TransactionBuilderData getTxBuilderData(BranchId branch, ScriptDefToken scriptDef, boolean resetKey);

   TransactionResult importFile(InputStream stream, BranchId branch, ArtifactId ciSetId);

   TransactionResult importBatch(InputStream stream, BranchId branch, ArtifactId ciSetId);
}
