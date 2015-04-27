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
package org.eclipse.osee.define.report.internal;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import org.eclipse.osee.app.OseeAppResourceTokens;
import org.eclipse.osee.ats.impl.IAtsServer;
import org.eclipse.osee.define.report.WordUpdateEndpointImpl;
import org.eclipse.osee.define.report.api.DefineApi;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.framework.jdk.core.type.ResourceRegistry;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Ryan D. Brooks
 */
@ApplicationPath("define")
public final class OseeReportApplication extends Application {
   private final Set<Object> singletons = new HashSet<Object>();
   private OrcsApi orcsApi;
   private Log logger;
   private DefineApi defineApi;
   private IAtsServer atsServer;

   public void setAtsServer(IAtsServer atsServer) {
      this.atsServer = atsServer;
   }

   public void setOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setDefineApi(DefineApi defineApi) {
      this.defineApi = defineApi;
   }

   public void start(Map<String, Object> properties) {

      IResourceRegistry resourceRegistry = new ResourceRegistry();
      OseeAppResourceTokens.register(resourceRegistry);
      logger.debug(">>>>> registered Requirement resource");
      singletons.add(new SystemSafetyResource(logger, resourceRegistry, orcsApi));
      logger.debug(">>>>> registered Safety resource");
      singletons.add(new PublishLowHighReqTraceabilityResource(logger, resourceRegistry, orcsApi));
      logger.debug(">>>>> registered Low/High Trace resource");
      singletons.add(new DataRightsResource(defineApi));
      logger.debug(">>>>> registered Data Rights resource");
      singletons.add(new WordUpdateEndpointImpl(logger, orcsApi, atsServer));
      logger.debug(">>>>> registered WordUpdateEndpointImpl");
   }

   @Override
   public Set<Object> getSingletons() {
      return singletons;
   }
}
