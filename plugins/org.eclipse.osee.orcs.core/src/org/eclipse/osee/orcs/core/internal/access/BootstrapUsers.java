/*******************************************************************************
 * Copyright (c) 2022 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.access;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.core.data.BootstrapUserProvider;
import org.eclipse.osee.framework.core.data.UserToken;

/**
 * @author Donald G. Dunne
 */
public class BootstrapUsers {

   public static Set<UserToken> users = new HashSet<>();

   public BootstrapUsers() {
      // for jax-rs
   }

   public void addUserProvider(BootstrapUserProvider provider) {
      users.addAll(provider.getBootsrapUsers());
   }

   public static Set<UserToken> getBoostrapUsers() {
      return users;
   }
}
