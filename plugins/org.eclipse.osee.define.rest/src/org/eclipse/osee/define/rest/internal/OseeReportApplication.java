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
package org.eclipse.osee.define.rest.internal;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import org.eclipse.osee.app.OseeAppResourceTokens;
import org.eclipse.osee.define.api.DefineApi;
import org.eclipse.osee.define.rest.DataRightsEndpointImpl;
import org.eclipse.osee.define.rest.MSWordEndpointImpl;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.framework.jdk.core.type.ResourceRegistry;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Ryan D. Brooks
 */
@ApplicationPath("define")
public final class OseeReportApplication extends Application {
   private final Set<Object> singletons = new HashSet<>();
   private OrcsApi orcsApi;
   private DefineApi defineApi;
   private Log logger;

   public void setDefineApi(DefineApi defineApi) {
      this.defineApi = defineApi;
   }

   public void setOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void start(Map<String, Object> properties) {
      IResourceRegistry resourceRegistry = new ResourceRegistry();
      OseeAppResourceTokens.register(resourceRegistry);
      logger.debug(">>>>> registered Requirement resource");
      singletons.add(new SystemSafetyResource(logger, resourceRegistry, orcsApi));
      logger.debug(">>>>> registered Safety resource");
      singletons.add(new TraceabilityResource(logger, resourceRegistry, orcsApi, defineApi));
      logger.debug(">>>>> registered Low/High Trace resource");
      singletons.add(new DataRightsSwReqAndCodeResource(logger, resourceRegistry, orcsApi));
      logger.debug(">>>>> registered DataRightsSwReqAndCodeResource");
      singletons.add(new DataRightsEndpointImpl(defineApi));
      logger.debug(">>>>> registered DataRightsEndpoint");
      singletons.add(new MSWordEndpointImpl(defineApi));
      logger.debug(">>>>> registered WordUpdateEndpoint");
   }

   @Override
   public Set<Object> getSingletons() {
      return singletons;
   }
}
