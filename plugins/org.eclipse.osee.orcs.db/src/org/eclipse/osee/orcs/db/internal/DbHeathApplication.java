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
package org.eclipse.osee.orcs.db.internal;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.orcs.db.internal.health.DatabaseHealthResource;

/**
 * @author Ryan D. Brooks
 */
@ApplicationPath("db")
public final class DbHeathApplication extends Application {

   private JdbcService jdbcService;

   private final Set<Object> singletons = new HashSet<>();

   public void setJdbcService(JdbcService jdbcService) {
      this.jdbcService = jdbcService;
   }

   @Override
   public Set<Object> getSingletons() {
      return singletons;
   }

   public void start(Map<String, Object> properties) {
      singletons.add(new DatabaseHealthResource(properties, jdbcService));
   }

   public void stop() {
      singletons.clear();
   }
}