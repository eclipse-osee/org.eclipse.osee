/*
 * Created on Jun 28, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.access.internal;

import java.util.Collection;
import org.eclipse.osee.framework.access.IAccessProvider;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.AccessData;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.IBasicArtifact;

public class CMAccessProvider implements IAccessProvider {

   private final ConfigurationManagementProvider provider;

   public CMAccessProvider(ConfigurationManagementProvider provider) {
      this.provider = provider;
   }

   @Override
   public void computeAccess(IBasicArtifact<?> userArtifact, Collection<IBasicArtifact<?>> artsToCheck, AccessData accessData) throws OseeCoreException {

      for (IBasicArtifact<?> artifactToCheck : artsToCheck) {
         ConfigurationManagement management = provider.getCM(artifactToCheck);
         AccessModel accessModel = management.getAccessModel();
         String contextId = management.getContextId(userArtifact, artifactToCheck);
         AccessContext context = accessModel.getContext(contextId);
         PermissionEnum permission = context.getPermission();
         accessData.add(artifactToCheck, permission);
      }
   }

   public static interface ConfigurationManagementProvider {
      ConfigurationManagement getCM(IBasicArtifact<?> artifact) throws OseeCoreException;
   }

   private final class ConfigurationManagementProviderImpl implements ConfigurationManagementProvider {

      @Override
      public ConfigurationManagement getCM(IBasicArtifact<?> artifact) throws OseeCoreException {
         Branch branch = artifact.getBranch();
         IBasicArtifact<?> associatedArtifact = branch.getAssociatedArtifact();
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
