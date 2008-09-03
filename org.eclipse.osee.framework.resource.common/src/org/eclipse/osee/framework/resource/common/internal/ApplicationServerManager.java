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
package org.eclipse.osee.framework.resource.common.internal;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.resource.common.IApplicationServerManager;

/**
 * @author Roberto E. Escobar
 */
public class ApplicationServerManager implements IApplicationServerManager {

   private Map<String, Object> registeredServices;

   public ApplicationServerManager() {
      this.registeredServices = new HashMap<String, Object>();
   }

   protected void registerHttpService(String contextName) {
      this.registeredServices.put(contextName, new Object());
   }

   protected void deregisterHttpService(String contextName) {
      this.registeredServices.remove(contextName);
   }

   public boolean areRequestsAllowed(String context, String operation) {
      return true;
   }

   public void shutdown() {

   }

}
