/*
 * Created on Jun 25, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workdef.api;

public interface IAtsWorkDefinitionStore {

   public abstract String loadWorkDefinitionString(String workDefId);

   public abstract IAttributeResolver getAttributeResolver();

   public abstract IUserResolver getUserResolver();
}
