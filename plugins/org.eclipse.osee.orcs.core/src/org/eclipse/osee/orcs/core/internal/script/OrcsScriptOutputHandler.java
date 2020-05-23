/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.orcs.core.internal.script;

import org.eclipse.osee.orcs.core.ds.LoadDataHandlerAdapter;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScript;

/**
 * @author Roberto E. Escobar
 */
public class OrcsScriptOutputHandler extends LoadDataHandlerAdapter {

   public void onEvalStart() {
      //
   }

   public void onCompileStart(OrcsScript model) {
      //
   }

   public void onCompileEnd() {
      //
   }

   public void onExecutionStart(String version) {
      //
   }

   public void onQueryStart(QueryData data) {
      //
   }

   public void onQueryEnd() {
      //
   }

   public void onExecutionEnd() {
      //
   }

   public void onEvalEnd() {
      //
   }

   public void onError(Throwable th) {
      //
   }
}