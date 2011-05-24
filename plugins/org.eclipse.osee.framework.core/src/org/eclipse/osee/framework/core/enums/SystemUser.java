/*
 * Created on May 24, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.enums;

import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.framework.core.data.IUserToken;
import org.eclipse.osee.framework.core.data.TokenFactory;

/**
 * @author Donald G. Dunne
 */
public final class SystemUser {

   // @formatter:off
   public static final IUserToken OseeSystem = TokenFactory.createUserToken("AAABDBYPet4AGJyrc9dY1w", "OSEE System", "", "99999999", false, false, false);
   public static final IUserToken Guest = TokenFactory.createUserToken("AAABDi35uzwAxJLISLBZdA", "Guest", "", "99999998", false, false, false);
   public static final IUserToken BootStrap = TokenFactory.createUserToken("noguid", "Boot Strap", "bootstrap@osee.org", "bootstrap", true, false, false);
   public static final IUserToken UnAssigned = TokenFactory.createUserToken("AAABDi1tMx8Al92YWMjeRw", "UnAssigned", "", "99999997", true, false, false);
   public static List<IUserToken> values = Arrays.asList(OseeSystem, Guest, BootStrap, UnAssigned);
   // @formatter:on

   private SystemUser() {
      // Constants
   }

   public static List<IUserToken> values() {
      return values;
   }

}
