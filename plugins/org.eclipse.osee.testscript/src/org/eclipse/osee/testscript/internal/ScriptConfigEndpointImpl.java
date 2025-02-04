/*********************************************************************
 * Copyright (c) 2025 Boeing
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

import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionResult;
import org.eclipse.osee.testscript.ScriptConfigApi;
import org.eclipse.osee.testscript.ScriptConfigEndpoint;
import org.eclipse.osee.testscript.ScriptConfigToken;

public class ScriptConfigEndpointImpl implements ScriptConfigEndpoint {

   private final ScriptConfigApi scriptConfigApi;

   public ScriptConfigEndpointImpl(ScriptConfigApi scriptConfigApi) {
      this.scriptConfigApi = scriptConfigApi;
   }

   @Override
   public ScriptConfigToken getConfig(BranchId branchId) {
      return this.scriptConfigApi.get(branchId);
   }

   @Override
   public TransactionResult createScriptConfiguration(BranchId branchId) {
      return this.scriptConfigApi.createScriptConfiguration(branchId);
   }

}
