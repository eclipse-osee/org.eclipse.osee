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
package org.eclipse.osee.jdbc.internal;

import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcClientConfig;
import org.eclipse.osee.jdbc.JdbcConstants.JdbcDriverType;
import org.eclipse.osee.jdbc.JdbcException;
import org.eclipse.osee.jdbc.JdbcMigrationOptions;
import org.eclipse.osee.jdbc.JdbcMigrationResource;
import org.flywaydb.core.Flyway;

/**
 * @author John Misinco
 */
public class JdbcMigration {

   private static final String DB_BIGINT = "db.bigint";
   private static final String DB_CLOB = "db.clob";
   private static final String DB_BLOB = "db.blob";

   private static final String LOCATION_TEMPLATE = "filesystem:%s";
   private boolean baselineOnMigrate = false;
   private final JdbcClient jdbcClient;

   public JdbcMigration(JdbcClient jdbcClient) {
      this.jdbcClient = jdbcClient;
   }

   public void migrate(JdbcMigrationOptions options, Iterable<JdbcMigrationResource> migrations) {
      try {
         ArrayList<String> allPaths = new ArrayList<String>();
         if (options.isBaselineOnMigration()) {
            baselineOnMigrate();
         }

         Flyway fly = newFlyway();
         Map<String, String> placeholders = fly.getPlaceholders();
         for (JdbcMigrationResource migration : migrations) {
            migration.addPlaceholders(placeholders);
            URL location = migration.getLocation();
            try {
               location = FileLocator.toFileURL(location);
            } catch (Exception ex) {
               // do nothing
            }
            String formated = String.format(LOCATION_TEMPLATE, location.getFile());
            allPaths.add(formated);
         }
         fly.setLocations(allPaths.toArray(new String[allPaths.size()]));
         before(placeholders);
         if (options.isClean()) {
            fly.clean();
         }
         fly.migrate();
         after();
      } catch (Exception ex) {
         throw JdbcException.newJdbcException(ex);
      }
   }

   public void baseline() {
      Flyway fly = newFlyway();
      fly.baseline();
   }

   public void clean() {
      Flyway fly = newFlyway();
      fly.clean();
   }

   public void baselineOnMigrate() {
      baselineOnMigrate = true;
   }

   private void before(Map<String, String> placeholders) {
      setDbTransactionControl("LOCKS");
      JdbcClientConfig config = jdbcClient.getConfig();
      String driver = config.getDbDriver();

      placeholders.put(DB_BLOB, "blob");
      placeholders.put(DB_CLOB, "clob");
      placeholders.put(DB_BIGINT, "bigint");

      if (JdbcDriverType.postgresql.getDriver().equals(driver)) {
         placeholders.put(DB_BLOB, "bytea");
         placeholders.put(DB_CLOB, "text");
      } else if (JdbcDriverType.oracle_thin.getDriver().equals(driver)) {
         placeholders.put(DB_BIGINT, "number");
      }

   }

   private void after() {
      setDbTransactionControl("MVCC");
   }

   private void setDbTransactionControl(String mode) {
      JdbcClientConfig config = jdbcClient.getConfig();
      if (JdbcDriverType.hsql.getDriver().equals(config.getDbDriver())) {
         jdbcClient.runPreparedUpdate("SET DATABASE TRANSACTION CONTROL " + mode);
      }
   }

   private Flyway newFlyway() {
      JdbcClientConfig config = jdbcClient.getConfig();
      Flyway fly = new Flyway();
      fly.setTable("OSEE_SCHEMA_VERSION");
      fly.setClassLoader(getClass().getClassLoader());
      fly.setDataSource(config.getDbUri(), config.getDbUsername(), config.getDbPassword());
      fly.setBaselineOnMigrate(baselineOnMigrate);
      return fly;
   }

}
