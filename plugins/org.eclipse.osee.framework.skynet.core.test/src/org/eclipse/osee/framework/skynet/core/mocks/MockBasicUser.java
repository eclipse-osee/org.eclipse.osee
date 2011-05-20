/*
 * Created on Jun 13, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.mocks;

import org.eclipse.osee.framework.core.model.IBasicUser;
import org.eclipse.osee.framework.jdk.core.util.GUID;

public class MockBasicUser extends MockIArtifact implements IBasicUser {

   private final String userId;

   public MockBasicUser(String name, String userId) {
      super(234, name, GUID.create(), null, null);
      this.userId = userId;
   }

   @Override
   public String getUserId() {
      return userId;
   }

   @Override
   public boolean isActive() {
      return true;
   }

}
