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
import java.util.logging.Level;
import org.eclipse.osee.framework.access.IAccessProvider;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.core.model.access.AccessData;
import org.eclipse.osee.framework.core.services.IAccessControlService;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.logging.OseeLog;

public final class ObjectAccessProviderProxy implements IAccessProvider {

   private IAccessProvider accessProvider;
   private IAccessControlService accessControlService;

   public void setAccessControlService(IAccessControlService accessControlService) {
      this.accessControlService = accessControlService;
   }

   public void start() {
      if (accessControlService instanceof AccessControlServiceProxy) {
         AccessControlServiceProxy proxy = (AccessControlServiceProxy) accessControlService;
         accessProvider = new ObjectAccessProvider(proxy.getProxiedObject());
      } else {
         OseeLog.log(AccessControlHelper.class, Level.SEVERE, "Error initializing ObjectAccessProvider");
      }
   }

   public void stop() {
      accessProvider = null;
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