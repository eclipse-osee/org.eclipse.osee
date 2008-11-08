/*
 * Created on Nov 7, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.data;

/**
 * @author Ryan D. Brooks
 */
public class OseeUser implements IOseeUser {
   private final String userName;
   private final String userId;
   private final String userEmail;
   private final boolean isActive;

   public OseeUser(String userName, String userId, String userEmail, boolean isActive) {
      this.userName = userName;
      this.userId = userId;
      this.userEmail = userEmail;
      this.isActive = isActive;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.core.data.IOseeUser#getEmail()
    */
   @Override
   public String getEmail() {
      return userEmail;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.core.data.IOseeUser#getName()
    */
   @Override
   public String getName() {
      return userName;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.core.data.IOseeUser#getUserID()
    */
   @Override
   public String getUserID() {
      return userId;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.core.data.IOseeUser#isActive()
    */
   @Override
   public boolean isActive() {
      return isActive;
   }
}