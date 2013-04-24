/*
 * Created on Apr 24, 2013
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.orcs.db.internal.sql;

public class WithClause implements AliasEntry {
   public enum WithAlias {
      GAMMA("gamma"),
      ATTRIBUTE("att");

      private final String value;

      private WithAlias(String value) {
         this.value = value;
      }

      @Override
      public String toString() {
         return value;
      }
   }

   private final String withList;
   private final String alias;
   private String generatedAlias;

   public WithClause(String withList, WithAlias alias) {
      this.withList = withList;
      this.alias = alias.toString();
   }

   @Override
   public String getAliasPrefix() {
      return alias;
   }

   @Override
   public String getEntry() {
      return withList;
   }

   public String getGeneratedAlias() {
      return generatedAlias;
   }

   public void setGeneratedAlias(String generatedAlias) {
      this.generatedAlias = generatedAlias;
   }

}
