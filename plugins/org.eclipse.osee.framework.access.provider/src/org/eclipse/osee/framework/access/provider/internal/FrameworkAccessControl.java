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
import java.util.Collections;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.IAccessContextId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.dsl.integration.RoleContextProvider;
import org.eclipse.osee.framework.core.services.CmAccessControl;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;

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
      try {
         Conditions.checkExpressionFailOnTrue(!isAssocitedArtifactValid(object), "Associated artifact");
      } catch (OseeCoreException ex) {
         OseeLog.log(DefaultFrameworkAccessConstants.class, Level.INFO,
            String.format("Unable to determine associated artifact for [%s]", object));
         return Collections.singletonList(DefaultFrameworkAccessConstants.INVALID_ASSOC_ART_ID);
      }
      return roleContextProvider.getContextId(user);
   }

   private boolean isAssocitedArtifactValid(Object object)  {
      BranchId branch = null;
      if (object instanceof Artifact) {
         branch = ((Artifact) object).getBranch();
      } else if (object instanceof BranchId) {
         branch = (BranchId) object;
      }
      if (branch != null) {
         return BranchManager.getAssociatedArtifact(branch) != null;
      }
      return false;
   }

}
