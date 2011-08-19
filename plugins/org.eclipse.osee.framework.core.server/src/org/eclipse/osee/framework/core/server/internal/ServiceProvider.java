/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.server.internal;

import org.eclipse.osee.framework.core.services.IOseeCachingService;

/**
 * @author Ryan D. Brooks
 */
public class ServiceProvider {
   private IOseeCachingService service;
   private static ServiceProvider instance;

   public void setCachingService(IOseeCachingService service) {
      this.service = service;
   }

   public static IOseeCachingService getCachingService() {
      return instance.service;
   }

   public void start() {
      instance = this;
   }

   public void stop() {
      instance = null;
   }
}