/*********************************************************************
 * Copyright (c) 2022 Boeing
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
package org.eclipse.osee.orcs.rest.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * @author Christopher Rebuck
 */

public final class UserWithContexts extends NamedIdBase {

   private List<UserContexts> usersContexts;

   public UserWithContexts(UserToken user, ArrayList<ArtifactReadable> contexts) {
      super(user.getId(), user.getName());

      this.setUsersContexts(
         contexts.stream().filter(cntxt -> cntxt.isValid()).map(context -> new UserContexts(context)).collect(
            Collectors.toList()));
   }

   public void setUsersContexts(List<UserContexts> usersContexts) {
      this.usersContexts = usersContexts;
   }

   public List<UserContexts> getUsersContexts() {
      return usersContexts;
   }
}
