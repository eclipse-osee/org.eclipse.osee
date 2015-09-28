/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.account.rest.internal.client;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import org.eclipse.osee.jdbc.JdbcService;

/**
 * @author Donald G. Dunne
 */
@ApplicationPath("/")
public final class ClientApplication extends Application {

   private Set<Object> singletons;
   private JdbcService jdbcService;

   public void setJdbcService(JdbcService jdbcService) {
      this.jdbcService = jdbcService;
   }

   public void start() {
      singletons = new HashSet<>();
      singletons.add(new ClientResource(jdbcService));
   }

   public void stop() {
      singletons.clear();
      singletons = null;
   }

   @Override
   public Set<Object> getSingletons() {
      return singletons;
   }

}
