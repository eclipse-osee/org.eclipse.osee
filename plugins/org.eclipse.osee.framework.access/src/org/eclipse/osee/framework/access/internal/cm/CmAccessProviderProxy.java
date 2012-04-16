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
import java.util.concurrent.CopyOnWriteArraySet;
import org.eclipse.osee.framework.access.IAccessProvider;
import org.eclipse.osee.framework.access.internal.AccessControlServiceProxy;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.core.model.access.AccessData;
import org.eclipse.osee.framework.core.services.CmAccessControl;
import org.eclipse.osee.framework.core.services.CmAccessControlProvider;
import org.eclipse.osee.framework.core.services.IAccessControlService;
import org.eclipse.osee.framework.core.util.Conditions;

/**
 * @author Roberto E. Escobar
 */
public final class CmAccessProviderProxy implements IAccessProvider {

   private final Collection<CmAccessControl> cmServices = new CopyOnWriteArraySet<CmAccessControl>();
   private IAccessProvider accessProvider;
   private IAccessControlService accessControlService;

   public void setAccessControlService(IAccessControlService accessControlService) {
      this.accessControlService = accessControlService;
   }

   public void addCmAccessControl(CmAccessControl cmAccessControl) {
      cmServices.add(cmAccessControl);
      refreshCache();
   }

   public void removeCmAccessControl(CmAccessControl cmAccessControl) {
      cmServices.remove(cmAccessControl);
      refreshCache();
   }

   public void start() {
      CmAccessControlProvider cmProvider = new CmAccessControlProviderImpl(cmServices);
      accessProvider = new CmAccessProvider(cmProvider);
   }

   public void stop() {
      accessProvider = null;
      cmServices.clear();
   }

   public void refreshCache() {
      if (accessControlService instanceof AccessControlServiceProxy) {
         ((AccessControlServiceProxy) accessControlService).clearCache();
      }
   }

   public IAccessProvider getAccessProvider() {
      return accessProvider;
   }

   private void checkInitialized() throws OseeCoreException {
      Conditions.checkNotNull(getAccessProvider(), "object access provider",
         "Object Access Provider not properly initialized");
   }

   @Override
   public void computeAccess(IBasicArtifact<?> userArtifact, Collection<?> objToCheck, AccessData accessData) throws OseeCoreException {
      checkInitialized();
      getAccessProvider().computeAccess(userArtifact, objToCheck, accessData);
   }
}