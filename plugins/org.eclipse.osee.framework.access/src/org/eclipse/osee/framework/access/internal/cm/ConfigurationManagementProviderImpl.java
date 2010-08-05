package org.eclipse.osee.framework.access.internal.cm;

import java.util.Collection;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.core.services.ConfigurationManagement;
import org.eclipse.osee.framework.core.services.ConfigurationManagementProvider;
import org.eclipse.osee.framework.core.services.HasConfigurationManagement;

public class ConfigurationManagementProviderImpl implements ConfigurationManagementProvider {

   private final Collection<ConfigurationManagement> cmServices;

   public ConfigurationManagementProviderImpl(Collection<ConfigurationManagement> cmServices) {
      this.cmServices = cmServices;
   }

   @Override
   public ConfigurationManagement getCmService(IBasicArtifact<?> userArtifact, Object object) throws OseeCoreException {
      ConfigurationManagement cmToReturn = null;
      if (object instanceof HasConfigurationManagement) {
         HasConfigurationManagement cmContainer = (HasConfigurationManagement) object;
         cmToReturn = cmContainer.getCM();
      } else {
         for (ConfigurationManagement cmService : cmServices) {
            if (cmService.isApplicable(userArtifact, object)) {
               cmToReturn = cmService;
               break;
            }
         }
         if (cmToReturn == null) {
            cmToReturn = null; //TODO: provide Default
         }
      }
      return cmToReturn;
   }
}