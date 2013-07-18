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
package org.eclipse.osee.ats.dsl.ui.internal;

import org.eclipse.osee.framework.ui.skynet.DslGrammar;
import org.eclipse.osee.framework.ui.skynet.DslGrammarStorageAdapter;

/**
 * @author Roberto E. Escobar
 */
public class AtsDslGrammar implements DslGrammar {

   @Override
   public String getExtension() {
      return "ats";
   }

   @Override
   public String getGrammarId() {
      return AtsDslActivator.ORG_ECLIPSE_OSEE_ATS_DSL_ATSDSL;
   }

   @Override
   public <T> T getObject(Class<? extends T> clazz) {
      return AtsDslActivator.getInstance().getInjector(getGrammarId()).getProvider(clazz).get();
   }

   @Override
   public DslGrammarStorageAdapter getStorageAdapter() {
      return null;
   }

}
