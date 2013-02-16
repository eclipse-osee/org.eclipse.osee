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
package org.eclipse.osee.ats.core.config;

import org.eclipse.osee.ats.api.version.IAtsVersionService;

public class AtsVersionService {

   private static AtsVersionService instance;
   private IAtsVersionService service;

   public static IAtsVersionService get() {
      if (instance == null) {
         throw new IllegalStateException("ATS Version Service has not been activated");
      }
      return instance.service;
   }

   public void setVersionService(IAtsVersionService service) {
      this.service = service;
   }

   public void start() {
      instance = this;
   }

}
