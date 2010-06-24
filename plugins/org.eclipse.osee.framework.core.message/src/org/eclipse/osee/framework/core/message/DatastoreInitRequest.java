/*
 * Created on Jun 23, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.message;

public class DatastoreInitRequest {

   private final String tableDataSpace;
   private final String indexDataSpace;
   private final boolean useFileSpecifiedSchemas;

   public DatastoreInitRequest(String tableDataSpace, String indexDataSpace, boolean useFileSpecifiedSchemas) {
      super();
      this.tableDataSpace = tableDataSpace;
      this.indexDataSpace = indexDataSpace;
      this.useFileSpecifiedSchemas = useFileSpecifiedSchemas;
   }

   public String getTableDataSpace() {
      return tableDataSpace;
   }

   public String getIndexDataSpace() {
      return indexDataSpace;
   }

   public boolean isUseFileSpecifiedSchemas() {
      return useFileSpecifiedSchemas;
   }
}
