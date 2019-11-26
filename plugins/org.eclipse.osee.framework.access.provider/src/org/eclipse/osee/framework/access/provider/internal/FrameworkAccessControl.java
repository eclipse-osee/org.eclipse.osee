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
package org.eclipse.osee.framework.access.provider.internal;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.IAccessContextId;
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
   public Collection<? extends IAccessContextId> getContextId(ArtifactToken user, Object object) {
      return roleContextProvider.getContextId(user);
   }
}