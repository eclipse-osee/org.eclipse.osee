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
package org.eclipse.osee.framework.manager.servlet.internal;

import org.eclipse.osee.orcs.ApplicationContext;

public class ApplicationContextFactory {

   private ApplicationContextFactory() {
      // TODO Improve session management
   }

   public static ApplicationContext createContext(final String sessionId) {
      return new ApplicationContext() {

         @Override
         public String getSessionId() {
            return sessionId;
         }
      };
   }
}
