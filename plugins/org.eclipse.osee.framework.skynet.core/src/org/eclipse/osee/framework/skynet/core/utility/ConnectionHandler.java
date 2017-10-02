/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.utility;

import com.google.common.collect.Iterables;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

/**
 * Handles connection recovery in the event of database connection being lost
 *
 * @author Jeff C. Phillips
 */
public final class ConnectionHandler {

   private ConnectionHandler() {
      // Utility class
   }

   private static JdbcService jdbcServiceInstance;

   public static JdbcClient getJdbcClient() {
      if (jdbcServiceInstance == null) {
         Bundle bundle = FrameworkUtil.getBundle(ConnectionHandler.class);
         BundleContext context = bundle.getBundleContext();
         jdbcServiceInstance = findJdbcService(context);
      }
      return jdbcServiceInstance != null ? jdbcServiceInstance.getClient() : null;
   }

   private static JdbcService findJdbcService(BundleContext context) {
      JdbcService toReturn = null;
      try {
         Collection<ServiceReference<JdbcService>> references =
            context.getServiceReferences(JdbcService.class, "(osgi.binding=skynet.jdbc.service)");
         ServiceReference<JdbcService> reference = Iterables.getFirst(references, null);
         if (reference != null) {
            toReturn = context.getService(reference);
         }
      } catch (InvalidSyntaxException ex) {
         throw new OseeCoreException(ex, "Error finding JdbcService reference with osgi.binding=skynet.jdbc.service");
      }
      return toReturn;
   }

   public static JdbcStatement getStatement() throws OseeDataStoreException {
      return getJdbcClient().getStatement();
   }

   /**
    * This method should only be used when not contained in a DB transaction
    *
    * @return number of records updated
    */
   public static int runPreparedUpdate(String query, Object... data) {
      return getJdbcClient().runPreparedUpdate(query, data);
   }

   /**
    * This method should only be used when not contained in a DB transaction
    *
    * @return number of records updated
    */
   public static int runBatchUpdate(String query, List<Object[]> dataList) {
      return getJdbcClient().runBatchUpdate(query, dataList);
   }

   /**
    * This method should only be used when contained in a DB transaction
    *
    * @return number of records updated
    */
   public static int runPreparedUpdate(JdbcConnection connection, String query, Object... data) {
      return getJdbcClient().runPreparedUpdate(connection, query, data);
   }

   public static long getNextSequence(String sequenceName, boolean aggressiveFetch) {
      return getJdbcClient().getNextSequence(sequenceName, aggressiveFetch);
   }
}