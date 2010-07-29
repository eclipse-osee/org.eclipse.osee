/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.dsl.integration.test.mocks;

import org.eclipse.osee.framework.core.dsl.oseeDsl.AccessContext;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslFactory;

/**
 * @author Roberto E. Escobar
 */
public final class MockModel {

   private MockModel() {
   }

   public static AccessContext createAccessContext(String guid, String name) {
      AccessContext context = OseeDslFactory.eINSTANCE.createAccessContext();
      context.setGuid(guid);
      context.setName(name);
      return context;
   }
}
