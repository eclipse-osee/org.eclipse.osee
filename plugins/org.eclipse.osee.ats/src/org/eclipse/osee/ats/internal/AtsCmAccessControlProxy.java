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
package org.eclipse.osee.ats.internal;

import java.util.Collection;
import org.eclipse.osee.ats.access.AtsBranchAccessManager;
import org.eclipse.osee.framework.core.data.IAccessContextId;
import org.eclipse.osee.framework.core.dsl.integration.AccessModelInterpreter;
import org.eclipse.osee.framework.core.dsl.integration.OseeDslAccessModel;
import org.eclipse.osee.framework.core.dsl.integration.OseeDslProvider;
import org.eclipse.osee.framework.core.dsl.integration.RoleContextProvider;
import org.eclipse.osee.framework.core.dsl.ui.integration.operations.OseeDslRoleContextProvider;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.core.model.access.AccessModel;
import org.eclipse.osee.framework.core.model.access.HasAccessModel;
import org.eclipse.osee.framework.core.services.CmAccessControl;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.listener.IEventListener;

/**
 * @author Roberto E. Escobar
 */
public class AtsCmAccessControlProxy implements CmAccessControl, HasAccessModel {

   private IEventListener listener;
   private AtsBranchAccessManager atsBranchObjectManager;
   private AccessModelInterpreter accessModelInterpreter;
   private CmAccessControl cmService;
   private AccessModel accessModel;

   public void setAccessModelInterpreter(AccessModelInterpreter accessModelInterpreter) {
      this.accessModelInterpreter = accessModelInterpreter;
   }

   public void start() {
      OseeDslProvider dslProvider = new AtsAccessOseeDslProvider("ats:/xtext/cm.access.osee");
      accessModel = new OseeDslAccessModel(accessModelInterpreter, dslProvider);
      RoleContextProvider roleAccessProvider = new OseeDslRoleContextProvider(dslProvider);

      atsBranchObjectManager = new AtsBranchAccessManager(roleAccessProvider);
      cmService = new AtsCmAccessControl(atsBranchObjectManager);

      listener = new AtsDslProviderUpdateListener(dslProvider);
      OseeEventManager.addListener(listener);
   }

   public void stop() {
      if (listener != null) {
         OseeEventManager.removeListener(listener);
         listener = null;
      }

      if (atsBranchObjectManager != null) {
         atsBranchObjectManager.dispose();
         atsBranchObjectManager = null;
      }
      cmService = null;
      accessModel = null;
   }

   private CmAccessControl getProxiedService() {
      return cmService;
   }

   @Override
   public boolean isApplicable(IBasicArtifact<?> user, Object object) {
      return getProxiedService().isApplicable(user, object);
   }

   @Override
   public Collection<? extends IAccessContextId> getContextId(IBasicArtifact<?> user, Object object) throws OseeCoreException {
      return getProxiedService().getContextId(user, object);
   }

   @Override
   public AccessModel getAccessModel() {
      return accessModel;
   }

}
