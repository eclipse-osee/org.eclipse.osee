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
import org.eclipse.osee.framework.jdk.core.util.Conditions;
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
   private static final String DB_ORGANIZATION_INDEX = "db.organization_index";
   private static final String DB_ORGANIZATION_INDEX_3 = "db.organization_index_3";
   private static final String DB_ORGANIZATION_INDEX_KEY_VALUE = "db.organization_index_key_value";
   private static final String DB_SYNONYM_2 = "db.synonym2";
   private static final String DB_GRANT_2 = "db.grant2";
   private static final String DB_SYNONYM_3 = "db.synonym3";
   private static final String DB_GRANT_3 = "db.grant3";
   private static final String DB_SYNONYM_4 = "db.synonym4";
   private static final String DB_GRANT_4 = "db.grant4";
   private static final String DB_SYNONYM_KEY_VALUE = "db.synonym_key_value";
   private static final String DB_GRANT_KEY_VALUE = "db.grant_key_value";
   private static final String DB_TABLESPACE = "db.tablespace";
   private static final String DB_PCTTHRESHOLD = "db.pctthreshold";
   private static final String DB_OVERFLOW = "db.overflow";
   private static final String DB_DEFERRABLE = "db.deferrable";

   private static final String LOCATION_TEMPLATE = "filesystem:%s";
   private boolean baselineOnMigrate = false;
   private final JdbcClient jdbcClient;

   public JdbcMigration(JdbcClient jdbcClient) {
      this.jdbcClient = jdbcClient;
   }

   public void migrate(JdbcMigrationOptions options, Iterable<JdbcMigrationResource> migrations) {
      try {
         ArrayList<String> allPaths = new ArrayList<>();
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
               String formated = String.format(LOCATION_TEMPLATE, location.getFile());
               allPaths.add(formated);
            } catch (Exception ex) {
               // do nothing
            }
         }
         fly.setLocations(allPaths.toArray(new String[allPaths.size()]));
         before(placeholders);
         if (options.isClean()) {
            Conditions.checkExpressionFailOnTrue(jdbcClient.getConfig().isProduction(),
               "Error - attempting to clean a production datastore.");
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
      placeholders.put(DB_ORGANIZATION_INDEX, "");
      placeholders.put(DB_ORGANIZATION_INDEX_3, "");
      placeholders.put(DB_ORGANIZATION_INDEX_KEY_VALUE, "");
      placeholders.put(DB_SYNONYM_2, "");
      placeholders.put(DB_GRANT_2, "");
      placeholders.put(DB_SYNONYM_3, "");
      placeholders.put(DB_GRANT_3, "");
      placeholders.put(DB_SYNONYM_4, "");
      placeholders.put(DB_GRANT_4, "");
      placeholders.put(DB_SYNONYM_KEY_VALUE, "");
      placeholders.put(DB_GRANT_KEY_VALUE, "");
      placeholders.put(DB_TABLESPACE, "");
      placeholders.put(DB_PCTTHRESHOLD, "");
      placeholders.put(DB_OVERFLOW, "");
      placeholders.put(DB_DEFERRABLE, "");

      if (JdbcDriverType.postgresql.getDriver().equals(driver)) {
         placeholders.put(DB_BLOB, "bytea");
         placeholders.put(DB_CLOB, "text");
         placeholders.put(DB_PCTTHRESHOLD, "");
         placeholders.put(DB_OVERFLOW, "");
         placeholders.put(DB_DEFERRABLE, "DEFERRABLE INITIALLY DEFERRED");

      } else if (JdbcDriverType.oracle_thin.getDriver().equals(driver)) {
         placeholders.put(DB_BIGINT, "number");
         placeholders.put(DB_ORGANIZATION_INDEX, "ORGANIZATION INDEX COMPRESS 2");
         placeholders.put(DB_ORGANIZATION_INDEX_3, "ORGANIZATION INDEX COMPRESS 3");
         placeholders.put(DB_ORGANIZATION_INDEX_KEY_VALUE, "ORGANIZATION INDEX");
         placeholders.put(DB_TABLESPACE, "TABLESPACE osee_data");
         placeholders.put(DB_PCTTHRESHOLD, "PCTTHRESHOLD 20");
         placeholders.put(DB_OVERFLOW, "OVERFLOW TABLESPACE osee_data");
         placeholders.put(DB_DEFERRABLE, "DEFERRABLE INITIALLY DEFERRED");
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
