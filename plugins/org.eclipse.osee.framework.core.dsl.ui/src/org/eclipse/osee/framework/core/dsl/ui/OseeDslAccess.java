/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
