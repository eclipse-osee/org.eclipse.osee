/*********************************************************************
 * Copyright (c) 2011 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.integration.tests.ats.workflow.review.role;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.review.UserRole;
import org.eclipse.osee.ats.api.util.IValueProvider;
import org.eclipse.osee.framework.jdk.core.util.AXml;

/**
 * @author Donald G. Dunne
 */
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
      List<String> values = new ArrayList<>();
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
