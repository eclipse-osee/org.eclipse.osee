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
package org.eclipse.osee.framework.access.internal.cm;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.osee.framework.access.IAccessProvider;
import org.eclipse.osee.framework.core.data.AccessContextId;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.core.model.access.AccessData;
import org.eclipse.osee.framework.core.model.access.AccessModel;
import org.eclipse.osee.framework.core.model.access.HasAccessModel;
import org.eclipse.osee.framework.core.services.CmAccessControl;
import org.eclipse.osee.framework.core.services.CmAccessControlProvider;
import org.eclipse.osee.framework.jdk.core.type.DoubleKeyHashMap;

/**
 * @author Roberto E. Escobar
 */
public class CmAccessProvider implements IAccessProvider {

   private final CmAccessControlProvider provider;

   public CmAccessProvider(CmAccessControlProvider provider) {
      this.provider = provider;
   }

   @Override
   public void computeAccess(IBasicArtifact<?> userArtifact, Collection<?> objToChecks, AccessData accessData) throws OseeCoreException {
      DoubleKeyHashMap<CmAccessControl, AccessContextId, Collection<Object>> cmToCheckObjects =
         new DoubleKeyHashMap<CmAccessControl, AccessContextId, Collection<Object>>();

      for (Object objectToCheck : objToChecks) {
         CmAccessControl management = provider.getService(userArtifact, objectToCheck);
         if (management instanceof HasAccessModel) {
            Collection<? extends AccessContextId> contextIds = management.getContextId(userArtifact, objectToCheck);
            for (AccessContextId contextId : contextIds) {
               Collection<Object> entries = cmToCheckObjects.get(management, contextId);
               if (entries == null) {
                  entries = new HashSet<Object>();
                  cmToCheckObjects.put(management, contextId, entries);
               }
               entries.add(objectToCheck);
            }
         }
      }

      for (CmAccessControl cm : cmToCheckObjects.getKeySetOne()) {
         AccessModel accessModel = ((HasAccessModel) cm).getAccessModel();
         Map<AccessContextId, Collection<Object>> sub = cmToCheckObjects.getSubHash(cm);
         for (Entry<AccessContextId, Collection<Object>> entry : sub.entrySet()) {
            AccessContextId contextId = entry.getKey();
            Collection<Object> objectsToCheck = entry.getValue();

            accessModel.computeAccess(contextId, objectsToCheck, accessData);
         }
      }
   }
}
