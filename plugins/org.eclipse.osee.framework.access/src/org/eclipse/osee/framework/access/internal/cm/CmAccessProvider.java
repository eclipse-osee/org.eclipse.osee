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
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.IAccessContextId;
import org.eclipse.osee.framework.core.model.access.AccessData;
import org.eclipse.osee.framework.core.model.access.AccessModel;
import org.eclipse.osee.framework.core.model.access.HasAccessModel;
import org.eclipse.osee.framework.core.services.CmAccessControl;
import org.eclipse.osee.framework.core.services.CmAccessControlProvider;
import org.eclipse.osee.framework.jdk.core.type.DoubleKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public class CmAccessProvider implements IAccessProvider {

   private final CmAccessControlProvider provider;

   public CmAccessProvider(CmAccessControlProvider provider) {
      this.provider = provider;
   }

   @Override
   public void computeAccess(ArtifactToken userArtifact, Collection<?> objToChecks, AccessData accessData)  {
      DoubleKeyHashMap<CmAccessControl, IAccessContextId, Collection<Object>> cmToCheckObjects =
         new DoubleKeyHashMap<CmAccessControl, IAccessContextId, Collection<Object>>();

      for (Object objectToCheck : objToChecks) {
         CmAccessControl management = provider.getService(userArtifact, objectToCheck);
         if (management instanceof HasAccessModel) {
            Collection<? extends IAccessContextId> contextIds = management.getContextId(userArtifact, objectToCheck);
            if (contextIds != null) {
               for (IAccessContextId contextId : contextIds) {
                  Collection<Object> entries = cmToCheckObjects.get(management, contextId);
                  if (entries == null) {
                     entries = new HashSet<>();
                     cmToCheckObjects.put(management, contextId, entries);
                  }
                  entries.add(objectToCheck);
               }
            }
         }
      }

      for (CmAccessControl cm : cmToCheckObjects.getKeySetOne()) {
         AccessModel accessModel = ((HasAccessModel) cm).getAccessModel();
         Map<IAccessContextId, Collection<Object>> sub = cmToCheckObjects.getSubHash(cm);
         for (Entry<IAccessContextId, Collection<Object>> entry : sub.entrySet()) {
            IAccessContextId contextId = entry.getKey();
            Collection<Object> objectsToCheck = entry.getValue();

            accessModel.computeAccess(contextId, objectsToCheck, accessData);
         }
      }
   }
}
