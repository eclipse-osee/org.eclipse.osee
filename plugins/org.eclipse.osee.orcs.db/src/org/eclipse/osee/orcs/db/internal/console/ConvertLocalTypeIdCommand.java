/*
 * Created on Oct 23, 2013
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.orcs.db.internal.console;

import java.util.Collection;
import java.util.concurrent.Callable;
import org.eclipse.osee.console.admin.Console;
import org.eclipse.osee.console.admin.ConsoleParameters;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.db.internal.callable.AbstractDatastoreTxCallable;

public class ConvertLocalTypeIdCommand extends AbstractDatastoreConsoleCommand {

   @Override
   public String getName() {
      return "convert_type_ids";
   }

   @Override
   public String getDescription() {
      return "Converts local type IDs in artifact, relation, attribute tables based on osee_type_id_map values";
   }

   @Override
   public String getUsage() {
      StringBuilder sb = new StringBuilder();
      sb.append("Usage: convert_type_ids -[P|U]\n");
      sb.append("Synopsis:\n");
      sb.append("This command will convert local type ids to remote type ids and vice versa (if needed to go back)\n");
      sb.append("When ran without the -P or -U option, it only prints the numbers of rows in each table.\n");
      sb.append("When ran with the -P option, it converts the database putting remote_ids in for local_ids\n");
      sb.append("When ran with the -U (undo) option, it converts the database putting back local_ids in for remotes\n");
      sb.append("Options:\n");
      sb.append("\tP: Convert the database\n");
      sb.append("\tU: Unconvert the database\n");
      return sb.toString();
   }

   @Override
   public Callable<?> createCallable(Console console, ConsoleParameters params) {
      Collection<String> options = params.getOptions();
      boolean runConversion = options.contains("P");
      boolean undoConversion = options.contains("U");

      return new ConvertTypeIdsCallable(runConversion, undoConversion, console, getJdbcClient(), getLogger(),
         getSession());
   }

   private class ConvertTypeIdsCallable extends AbstractDatastoreTxCallable<Void> {

      private final String[] tables = {"osee_artifact", "osee_attribute", "osee_relation_link"};
      private final String[] columns = {"art_type_id", "attr_type_id", "rel_link_type_id"};
      private final boolean runConversion;
      private final boolean undoConversion;
      private final Console console;
      private final JdbcClient jdbcClient;

      public ConvertTypeIdsCallable(boolean runConversion, boolean undoConversion, Console console, JdbcClient jdbcClient, Log logger, OrcsSession session) {
         super(logger, session, jdbcClient);
         this.runConversion = runConversion;
         this.undoConversion = undoConversion;
         this.console = console;
         this.jdbcClient = jdbcClient;
      }

      private void updateTableIds(JdbcConnection connection) throws OseeCoreException {
         for (int i = 0; i < tables.length; i++) {
            String table = tables[i];
            String column = columns[i];
            String idCol1 = runConversion ? "remote_id" : "local_id";
            String idCol2 = runConversion ? "local_id" : "remote_id";
            String sql =
               String.format("update %s set %s = (select %s from osee_type_id_map where %s = %s)", table, column,
                  idCol1, idCol2, column);
            int rowsUpdated = jdbcClient.runPreparedUpdate(connection, sql);
            console.writeln("[%s] had %d rows updated.", table, rowsUpdated);
         }
      }

      private void updateOseeInfo(JdbcConnection connection) throws OseeCoreException {
         String oseeInfoKey = "use.long.type.ids";
         String value = runConversion ? "true" : "false";
         jdbcClient.runPreparedUpdate(connection, "DELETE FROM osee_info WHERE OSEE_KEY = ?", oseeInfoKey);
         jdbcClient.runPreparedUpdate(connection, "INSERT INTO osee_info (OSEE_KEY, OSEE_VALUE) VALUES (?, ?)",
            oseeInfoKey, value);
      }

      private void performRowCounts() throws OseeCoreException {
         for (String table : tables) {
            JdbcStatement stmt = jdbcClient.getStatement();
            try {
               String query = String.format("select count(1) from %s", table);
               stmt.runPreparedQuery(query);
               if (stmt.next()) {
                  int count = stmt.getInt(1);
                  console.writeln("[%s] has %d rows", table, count);
               }
            } finally {
               stmt.close();
            }
         }
      }

      @Override
      protected Void handleTxWork(JdbcConnection connection) throws OseeCoreException {
         if (runConversion && undoConversion) {
            console.writeln("Options U and P cannot be specified together");
            return null;
         }

         if (runConversion || undoConversion) {
            String op = runConversion ? "Converting" : "Unconverting";
            console.writeln("%s the database...", op);
            updateTableIds(connection);
            updateOseeInfo(connection);
         } else {
            performRowCounts();
         }
         return null;
      }
   }

}
