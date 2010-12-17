/*
 * Created on Dec 15, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workdef;

public class RuleDefinition extends AbstractWorkDefItem {

   public RuleDefinition(String id) {
      super(id);
   }

   @Override
   public String toString() {
      return String.format("[%s]", getName());
   }

}
