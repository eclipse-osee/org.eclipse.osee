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
package org.eclipse.osee.framework.core.test.mocks;

import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.services.IOseeCachingServiceProvider;

/**
 * @author Roberto E. Escobar
 */
public class MockOseeCachingServiceProvider implements IOseeCachingServiceProvider {

   private final IOseeCachingService service;

   public MockOseeCachingServiceProvider(IOseeCachingService service) {
      this.service = service;
   }

   @Override
   public IOseeCachingService getOseeCachingService() {
      return service;
   }
}
