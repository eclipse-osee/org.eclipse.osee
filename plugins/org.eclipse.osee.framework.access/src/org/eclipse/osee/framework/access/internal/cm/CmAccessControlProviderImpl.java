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

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.core.services.CmAccessControl;
import org.eclipse.osee.framework.core.services.CmAccessControlProvider;
import org.eclipse.osee.framework.core.services.HasCmAccessControl;

/**
 * @author Roberto E. Escobar
 */
public class CmAccessControlProviderImpl implements CmAccessControlProvider {

   private final Collection<CmAccessControl> cmServices;

   public CmAccessControlProviderImpl(Collection<CmAccessControl> cmServices) {
      this.cmServices = cmServices;
   }

   @Override
   public CmAccessControl getService(IBasicArtifact<?> userArtifact, Object object) throws OseeCoreException {
      if (object instanceof HasCmAccessControl) {
         HasCmAccessControl cmContainer = (HasCmAccessControl) object;
         return cmContainer.getAccessControl();
      } else {
         CmAccessControl cmToReturn;
         Collection<CmAccessControl> applicableCms = new ArrayList<CmAccessControl>();
         for (CmAccessControl cmService : cmServices) {
            if (cmService.isApplicable(userArtifact, object)) {
               applicableCms.add(cmService);
            }
         }
         if (applicableCms.isEmpty()) {
            cmToReturn = null;
         } else if (applicableCms.size() == 1) {
            cmToReturn = applicableCms.iterator().next();
         } else {
            throw new OseeStateException("Multiple Configuration Management Systems managing: [%s] cms:%s", object,
               applicableCms);
         }
         return cmToReturn;
      }
   }
}