/*
 * Created on Jun 25, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.api.workdef;

public interface IUserResolver {

   boolean isUserIdValid(String userId);

   boolean isUserNameValid(String name);

   String getUserIdByName(String name);

}
