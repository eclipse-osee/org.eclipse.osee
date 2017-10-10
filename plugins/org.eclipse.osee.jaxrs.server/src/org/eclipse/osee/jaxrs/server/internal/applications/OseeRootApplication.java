/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.jaxrs.server.internal.applications;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import org.eclipse.osee.logger.Log;

/**
 * @author Angel Avila
 */
@ApplicationPath("/")
public class OseeRootApplication extends Application {

   private final Set<Object> singletons = new HashSet<Object>();

   @SuppressWarnings("unused")
   private Log logger;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void start() {
      singletons.add(new OseeRootResource());
   }

   public void stop() {
      singletons.clear();
   }

   @Override
   public Set<Object> getSingletons() {
      return singletons;
   }

}
