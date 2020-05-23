/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.framework.core.dsl.ui;

import com.google.inject.Injector;
import org.eclipse.osee.framework.core.dsl.ui.internal.OseeDslActivator;

/**
 * @author Roberto E. Escobar
 */
public final class OseeDslAccess {

   private OseeDslAccess() {
      // utility
   }

   public static String getGrammarId() {
      return OseeDslActivator.ORG_ECLIPSE_OSEE_FRAMEWORK_CORE_DSL_OSEEDSL;
   }

   public static Injector getInjector() {
      return OseeDslActivator.getInstance().getInjector(getGrammarId());
   }
}
