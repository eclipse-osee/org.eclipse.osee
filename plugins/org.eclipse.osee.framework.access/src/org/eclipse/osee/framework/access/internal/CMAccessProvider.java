/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.access.internal;

import java.util.Collection;
import org.eclipse.osee.framework.access.IAccessProvider;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.AccessData;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;

/**
 * @author Roberto E. Escobar
 */
public class CMAccessProvider implements IAccessProvider {

   private final ConfigurationManagementProvider provider;

   public CMAccessProvider(ConfigurationManagementProvider provider) {
      this.provider = provider;
   }

   @Override
   public void computeAccess(IBasicArtifact<?> userArtifact, Collection<?> objToChecks, AccessData accessData) throws OseeCoreException {
      for (Object objectToCheck : objToChecks) {
         if (objectToCheck instanceof IBasicArtifact<?>) {
            IBasicArtifact<?> artifactToCheck = (IBasicArtifact<?>) objectToCheck;
            ConfigurationManagement management = provider.getCM(artifactToCheck);
            AccessModel accessModel = management.getAccessModel();
            String contextId = management.getContextId(userArtifact, artifactToCheck);
            AccessContext context = accessModel.getContext(contextId);
            PermissionEnum permission = context.getPermission();
            accessData.add(artifactToCheck, permission);
         }
      }
   }

   public static interface ConfigurationManagementProvider {
      ConfigurationManagement getCM(IBasicArtifact<?> artifact) throws OseeCoreException;
   }

   private final class ConfigurationManagementProviderImpl implements ConfigurationManagementProvider {

      @Override
      public ConfigurationManagement getCM(IBasicArtifact<?> artifact) throws OseeCoreException {
         Branch branch = artifact.getBranch();
         IBasicArtifact<?> associatedArtifact = BranchManager.getAssociatedArtifact(branch);
         ConfigurationManagement cm = null; //         associatedArtifact.getCM();
         return cm;
      }
   }

   public static interface ConfigurationManagement {
      String getContextId(IBasicArtifact<?> userArtifact, IBasicArtifact<?> artifactToCheck);

      AccessModel getAccessModel();
   }

   public static interface AccessContext {

      PermissionEnum getPermission();
   }

   public static interface AccessModel {

      AccessContext getContext(String id) throws OseeCoreException;
   }

}
