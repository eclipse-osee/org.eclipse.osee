/*
 * Created on Jun 13, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.review.role;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.core.validator.IValueProvider;
import org.eclipse.osee.framework.jdk.core.util.AXml;

public class MockUserRoleValueProvider implements IValueProvider {

   private final List<UserRole> roles;

   public MockUserRoleValueProvider(List<UserRole> roles) {
      this.roles = roles;
   }

   @Override
   public String getName() {
      return "Roles";
   }

   @Override
   public boolean isEmpty() {
      return roles.isEmpty();
   }

   @Override
   public Collection<String> getValues() {
      List<String> values = new ArrayList<String>();
      for (UserRole item : roles) {
         values.add(AXml.addTagData("Role", item.toXml()));
      }
      return values;
   }

   @Override
   public Collection<Date> getDateValues() {
      return null;
   }

}
