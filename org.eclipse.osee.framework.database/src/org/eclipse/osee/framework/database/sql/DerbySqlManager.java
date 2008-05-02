/*
 * Created on May 1, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.database.sql;

import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.database.data.ConstraintElement;
import org.eclipse.osee.framework.database.data.ForeignKey;
import org.eclipse.osee.framework.database.data.ReferenceClause;
import org.eclipse.osee.framework.database.data.ReferenceClause.OnDeleteEnum;
import org.eclipse.osee.framework.database.data.ReferenceClause.OnUpdateEnum;
import org.eclipse.osee.framework.database.sql.datatype.SqlDataType;

/**
 * @author Roberto E. Escobar
 */
public class DerbySqlManager extends SqlManagerImpl {

   /**
    * @param logger
    * @param sqlDataType
    */
   public DerbySqlManager(SqlDataType sqlDataType) {
      super(sqlDataType);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.database.sql.SqlManager#constraintDataToSQL(org.eclipse.osee.framework.database.data.ConstraintElement, java.lang.String)
    */
   @Override
   public String constraintDataToSQL(ConstraintElement constraint, String tableID) {
      StringBuilder toReturn = new StringBuilder();
      String id = formatQuotedString(constraint.getId(), "\\.");
      String type = constraint.getConstraintType().toString();
      String appliesTo = formatQuotedString(constraint.getCommaSeparatedColumnsList(), ",");

      if (id != null && !id.equals("") && appliesTo != null && !appliesTo.equals("")) {
         toReturn.append("CONSTRAINT " + id + " " + type + " (" + appliesTo + ")");

         if (constraint instanceof ForeignKey) {
            ForeignKey fk = (ForeignKey) constraint;
            List<ReferenceClause> refs = fk.getReferences();

            for (ReferenceClause ref : refs) {
               String refTable = formatQuotedString(ref.getFullyQualifiedTableName(), "\\.");
               String refColumns = formatQuotedString(ref.getCommaSeparatedColumnsList(), ",");

               String onUpdate = "";
               if (!ref.getOnUpdateAction().equals(OnUpdateEnum.UNSPECIFIED)) {
                  onUpdate = "ON UPDATE " + ref.getOnUpdateAction().toString();
               }

               String onDelete = "";
               if (!ref.getOnDeleteAction().equals(OnDeleteEnum.UNSPECIFIED)) {
                  onDelete = "ON DELETE " + ref.getOnDeleteAction().toString();
               }

               if (refTable != null && refColumns != null && !refTable.equals("") && !refColumns.equals("")) {
                  toReturn.append(" REFERENCES " + refTable + " (" + refColumns + ")");
                  if (!onUpdate.equals("")) {
                     toReturn.append(" " + onUpdate);
                  }

                  if (!onDelete.equals("")) {
                     toReturn.append(" " + onDelete);
                  }

                  // Not Supported in Derby ?
                  //                  if (constraint.isDeferrable()) {
                  //                     toReturn.append(" DEFERRABLE");
                  //                  }
               }

               else {
                  logger.log(Level.WARNING, "Skipping CONSTRAINT at Table: " + tableID + "\n\t " + fk.toString());
               }

            }
         }
      } else {
         logger.log(Level.WARNING, "Skipping CONSTRAINT at Table: " + tableID + "\n\t " + constraint.toString());
      }
      return toReturn.toString();
   }
}
