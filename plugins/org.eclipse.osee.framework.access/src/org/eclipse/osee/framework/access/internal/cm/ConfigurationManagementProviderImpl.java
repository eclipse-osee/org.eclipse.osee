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
package org.eclipse.osee.framework.access.internal.cm;

import java.util.Collection;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.core.services.ConfigurationManagement;
import org.eclipse.osee.framework.core.services.ConfigurationManagementProvider;
import org.eclipse.osee.framework.core.services.HasConfigurationManagement;

/**
 * @author Roberto E. Escobar
 */
public class ConfigurationManagementProviderImpl implements ConfigurationManagementProvider {

   private final Collection<ConfigurationManagement> cmServices;
   private final ConfigurationManagement defaultCM;

   public ConfigurationManagementProviderImpl(ConfigurationManagement defaultCM, Collection<ConfigurationManagement> cmServices) {
      this.cmServices = cmServices;
      this.defaultCM = defaultCM;
   }

   @Override
   public ConfigurationManagement getCmService(IBasicArtifact<?> userArtifact, Object object) throws OseeCoreException {
      ConfigurationManagement cmToReturn = null;
      if (object instanceof HasConfigurationManagement) {
         HasConfigurationManagement cmContainer = (HasConfigurationManagement) object;
         cmToReturn = cmContainer.getCM();
      } else {
         for (ConfigurationManagement cmService : cmServices) {
            if (!cmService.equals(defaultCM)) {
               if (cmService.isApplicable(userArtifact, object)) {
                  cmToReturn = cmService;
                  break;
               }
            }
         }
         if (cmToReturn == null) {
            cmToReturn = defaultCM;
         }
      }
      return cmToReturn;
   }

}