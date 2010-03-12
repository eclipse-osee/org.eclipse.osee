/*
 * Created on Jan 18, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.plugin.core.IWorkbenchUser;
import org.eclipse.osee.framework.plugin.core.IWorkbenchUserService;

/**
 * @author Roberto E. Escobar
 */
public class WorkbenchUserService implements IWorkbenchUserService {

   @Override
   public IWorkbenchUser getUser() throws OseeCoreException {
      return new WorkbenchUserAdaptor(UserManager.getUser());
   }

   private static final class WorkbenchUserAdaptor implements IWorkbenchUser {

      private final User user;

      public WorkbenchUserAdaptor(User user) {
         this.user = user;
      }

      @Override
      public String getEmail() throws OseeCoreException {
         return user.getEmail();
      }

      @Override
      public String getName() throws OseeCoreException {
         return user.getName();
      }

      @Override
      public String getUserID() throws OseeCoreException {
         return user.getUserId();
      }

      @Override
      public boolean isActive() throws OseeCoreException {
         return user.isActive();
      }
   }
}
