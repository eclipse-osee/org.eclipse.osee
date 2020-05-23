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

package org.eclipse.osee.orcs.script.dsl.ui;

import com.google.inject.Injector;
import org.eclipse.osee.orcs.script.dsl.ui.internal.OrcsScriptDslActivator;

/**
 * @author Roberto E. Escobar
 */
public final class OrcsScriptDslAccess {

   private OrcsScriptDslAccess() {
      // utility
   }

   public static String getGrammarId() {
      return OrcsScriptDslActivator.ORG_ECLIPSE_OSEE_ORCS_SCRIPT_DSL_ORCSSCRIPTDSL;
   }

   public static Injector getInjector() {
      return OrcsScriptDslActivator.getInstance().getInjector(getGrammarId());
   }
}
