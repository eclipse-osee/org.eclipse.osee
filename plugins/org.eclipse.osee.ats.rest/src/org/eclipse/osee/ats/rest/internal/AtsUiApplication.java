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
package org.eclipse.osee.ats.rest.internal;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.core.Application;
import org.eclipse.osee.ats.impl.resource.AtsResourceTokens;
import org.eclipse.osee.ats.rest.internal.resources.AtsUiResource;
import org.eclipse.osee.ats.rest.internal.util.JaxRsExceptionMapper;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.template.engine.OseeTemplateTokens;

/**
 * @author Donald G. Dunne
 */
public class AtsUiApplication extends Application {

   private static OrcsApi orcsApi;
   private static Log logger;

   public void setOrcsApi(OrcsApi orcsApi) {
      AtsUiApplication.orcsApi = orcsApi;
   }

   public static OrcsApi getOrcsApi() {
      return orcsApi;
   }

   public static Log getLogger() {
      return logger;
   }

   public void setLogger(Log logger) {
      AtsUiApplication.logger = logger;
   }

   @Override
   public Set<Class<?>> getClasses() {
      Set<Class<?>> classes = new HashSet<Class<?>>();
      return classes;
   }

   @Override
   public Set<Object> getSingletons() {
      AtsResourceTokens.register(orcsApi.getResourceRegistry());
      OseeTemplateTokens.register(orcsApi.getResourceRegistry());
      Set<Object> singletons = new HashSet<Object>();
      singletons.add(new JaxRsExceptionMapper(orcsApi.getResourceRegistry()));
      singletons.add(new AtsUiResource(orcsApi));
      return singletons;
   }

}
