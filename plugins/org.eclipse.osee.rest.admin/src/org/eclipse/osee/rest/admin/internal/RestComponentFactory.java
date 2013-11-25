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
package org.eclipse.osee.rest.admin.internal;

import java.util.Collections;
import java.util.List;
import org.eclipse.osee.logger.Log;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.spi.container.servlet.ServletContainer;

/**
 * @author Roberto E. Escobar
 */
public class RestComponentFactory {
   private final Log logger;
   private List<Object> defaultSingletonResources;

   public RestComponentFactory(Log logger) {
      this.logger = logger;
   }

   public List<Object> getResourceSingletons() {
      if (defaultSingletonResources == null) {
         GenericExceptionMapper exceptionMapper = new GenericExceptionMapper(logger);
         defaultSingletonResources = Collections.<Object> singletonList(exceptionMapper);
      }
      return defaultSingletonResources;
   }

   public RestServletContainer createContainer(Log logger) throws Exception {
      DefaultResourceConfig config = new DefaultResourceConfig();
      ServletContainer container = new ServletContainer(config);
      return new RestServletContainer(logger, container, config);
   }
}
