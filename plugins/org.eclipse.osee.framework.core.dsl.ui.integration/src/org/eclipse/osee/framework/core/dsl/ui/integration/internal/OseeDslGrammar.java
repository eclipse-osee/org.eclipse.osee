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
