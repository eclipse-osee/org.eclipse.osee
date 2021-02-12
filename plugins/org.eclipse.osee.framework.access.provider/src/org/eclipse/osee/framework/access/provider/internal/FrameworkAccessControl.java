/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.framework.access.provider.internal;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AccessContextToken;
import org.eclipse.osee.framework.core.dsl.integration.RoleContextProvider;
import org.eclipse.osee.framework.core.services.CmAccessControl;

/**
 * @author John R. Misinco
 */
public class FrameworkAccessControl implements CmAccessControl {
   private final RoleContextProvider roleContextProvider;

   public FrameworkAccessControl(RoleContextProvider roleContextProvider) {
      this.roleContextProvider = roleContextProvider;
   }

   @Override
   public boolean isApplicable(ArtifactToken user, Object object) {
      return true;
   }

   @Override
   public Collection<? extends AccessContextToken> getContextId(ArtifactToken user, Object object) {
      return roleContextProvider.getContextId(user);
   }
}