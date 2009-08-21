/*
 * Created on Aug 12, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.relation.order;

/**
 * @author Andrew M. Finkbeiner
 *
 */
public enum RelationOrderBaseTypes implements RelationOrderId{
   
   USER_DEFINED("AAT0xogoMjMBhARkBZQA", "User Defined"),
   LEXICOGRAPHICAL_ASC("AAT1QW4eVE+YuzsoHFAA", "Lexicographical Ascending"),
   LEXICOGRAPHICAL_DESC("AAmATn6R9m7VCXQQwuQA", "Lexicographical Descending"),
   UNORDERED("AAT1uKZpeDQExlygoIAA", "Unordered");
   
   private String guid;
   private String prettyName;
   
   RelationOrderBaseTypes(String guid, String prettyName){
      this.guid = guid;
      this.prettyName = prettyName;
   }
   
   @Override
   public String getGuid() {
      return guid;
   }
   
   @Override
   public String prettyName(){
      return prettyName;
   }
}
