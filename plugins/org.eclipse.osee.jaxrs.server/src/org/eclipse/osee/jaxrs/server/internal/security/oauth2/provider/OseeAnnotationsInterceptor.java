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
package org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.apache.cxf.interceptor.security.SecureAnnotationsInterceptor;
import org.apache.cxf.message.Message;

/**
 * @author Angel Avila
 */

public class OseeAnnotationsInterceptor extends SecureAnnotationsInterceptor {

   @Override
   public void handleMessage(Message message) {
      Method method = (Method) message.get("org.apache.cxf.resource.method");
      Class<?> declaringClass = method.getDeclaringClass();
      if (declaringClass != null) {
         initRoles(declaringClass);
      } else {
         // Set Default roles here if needed
      }
      super.handleMessage(message);
   }

   private void initRoles(Class<?> clazz) {
      Map<String, String> rolesMap = new HashMap<>();
      findRoles(clazz, rolesMap);
      super.setMethodRolesMap(rolesMap);
   }

}
