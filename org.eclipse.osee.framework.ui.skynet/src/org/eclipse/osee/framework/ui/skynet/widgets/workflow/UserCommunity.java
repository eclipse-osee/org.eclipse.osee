/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets.workflow;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;

/**
 * @author Donald G. Dunne
 */
public class UserCommunity {
   private static final BranchPersistenceManager branchManager = BranchPersistenceManager.getInstance();
   private static UserCommunity instance = new UserCommunity();

   private UserCommunity() {
      super();
   }

   public static UserCommunity getInstance() {
      return instance;
   }

   public Set<String> getUserCommunityNames() {
      Set<String> communities;
      try {
         communities =
               ConfigurationPersistenceManager.getInstance().getValidEnumerationAttributeValues("ats.User Community",
                     branchManager.getCommonBranch());
      } catch (SQLException ex) {
         communities = new HashSet<String>();
         communities.add(ex.getLocalizedMessage());
      }
      return communities;
   }

   public String getUserCommunityCommaDelim() {
      StringBuffer sb = new StringBuffer();
      for (String name : getUserCommunityNames())
         sb.append(name + ",");
      return sb.toString().replaceFirst(",$", "");
   }

}
