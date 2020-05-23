/*********************************************************************
 * Copyright (c) 2015 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.orcs.db.internal.health;

import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.orcs.db.internal.callable.PurgeUnusedBackingDataAndTransactions;

/**
 * @author Ryan D. Brooks
 */
@Path("/health")
public final class DatabaseHealthResource {
   private final JdbcClient jdbcClient;

   public DatabaseHealthResource(Map<String, Object> properties, JdbcService jdbcService) {
      this.jdbcClient = jdbcService.getClient();
   }

   @Path("purgeunused")
   @GET
   @Produces(MediaType.TEXT_HTML)
   public String deleteObsoleteData() {
      PurgeUnusedBackingDataAndTransactions app = new PurgeUnusedBackingDataAndTransactions(jdbcClient);
      int[] counts = app.purge();

      StringBuilder strB = new StringBuilder();
      for (int count : counts) {
         strB.append(count);
         strB.append("<br />");
      }
      return strB.toString();
   }

}