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

package org.eclipse.osee.framework.core.dsl.ui.integration.internal;

import org.eclipse.osee.framework.core.dsl.ui.OseeDslAccess;
import org.eclipse.osee.framework.ui.skynet.DslGrammar;
import org.eclipse.osee.framework.ui.skynet.DslGrammarStorageAdapter;

/**
 * @author Roberto E. Escobar
 */
public class OseeDslGrammar implements DslGrammar {

   @Override
   public String getExtension() {
      return "osee";
   }

   @Override
   public String getGrammarId() {
      return OseeDslAccess.getGrammarId();
   }

   @Override
   public <T> T getObject(Class<? extends T> clazz) {
      return OseeDslAccess.getInjector().getProvider(clazz).get();
   }

   @Override
   public DslGrammarStorageAdapter getStorageAdapter() {
      return null;
   }

}
