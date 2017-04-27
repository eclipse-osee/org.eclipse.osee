/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.client.demo.internal;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.database.init.IDbInitializationTask;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.SystemGroup;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * @author Roberto E. Escobar
 */
public class AddDemoUsers implements IDbInitializationTask {

   @Override
   public void run() throws OseeCoreException {
      List<User> admins = new ArrayList<>();

      SkynetTransaction transaction = TransactionManager.createTransaction(CoreBranches.COMMON, "Add Dev Users");
      for (UserToken userEnum : DemoUsers.values()) {
         User user = UserManager.createUser(userEnum, transaction);
         if (userEnum.isAdmin()) {
            admins.add(user);
         }
      }

      transaction.execute();

      SkynetTransaction transaction1 = TransactionManager.createTransaction(CoreBranches.COMMON, "Configure OSEEAdmin");
      SystemGroup.OseeAdmin.getArtifact().persist(transaction1);
      transaction1.execute();
   }
}
