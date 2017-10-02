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
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.IAccessContextId;
import org.eclipse.osee.framework.core.dsl.integration.AccessModelInterpreter;
import org.eclipse.osee.framework.core.dsl.integration.OseeDslAccessModel;
import org.eclipse.osee.framework.core.dsl.integration.OseeDslProvider;
import org.eclipse.osee.framework.core.dsl.integration.RoleContextProvider;
import org.eclipse.osee.framework.core.dsl.ui.integration.operations.OseeDslRoleContextProvider;
import org.eclipse.osee.framework.core.model.access.AccessModel;
import org.eclipse.osee.framework.core.model.access.HasAccessModel;
import org.eclipse.osee.framework.core.services.CmAccessControl;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.event.OseeEventService;
import org.eclipse.osee.framework.skynet.core.event.listener.EventQosType;
import org.eclipse.osee.framework.skynet.core.event.listener.IEventListener;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * @author Roberto E. Escobar
 */
public class AtsCmAccessControlProxy implements CmAccessControl, HasAccessModel {

   private IEventListener listener;
   private AtsBranchAccessManager atsBranchObjectManager;
   private ServiceReference<AccessModelInterpreter> reference;
   private CmAccessControl cmService;
   private AccessModel accessModel;
   private OseeEventService eventService;
   private BundleContext bundleContext;

   private volatile boolean isInitialized = false;

   public void setAccessModelInterpreter(ServiceReference<AccessModelInterpreter> reference) {
      this.reference = reference;
   }

   public void setEventService(OseeEventService eventService) {
      this.eventService = eventService;
   }

   public void start(BundleContext bundleContext) {
      this.bundleContext = bundleContext;
   }

   public void stop() {
      if (listener != null) {
         eventService.removeListener(EventQosType.NORMAL, listener);
         listener = null;
      }

      if (atsBranchObjectManager != null) {
         atsBranchObjectManager = null;
      }
      cmService = null;
      accessModel = null;
      bundleContext = null;
      isInitialized = false;
   }

   private boolean isReady() {
      return reference != null && eventService != null && bundleContext != null;
   }

   private synchronized void ensureInitialized() {
      if (isReady() && !isInitialized) {
         AccessModelInterpreter interpreter = bundleContext.getService(reference);
         OseeDslProvider dslProvider = new AtsAccessOseeDslProvider("ats:/xtext/cm.access.osee");
         accessModel = new OseeDslAccessModel(interpreter, dslProvider);
         RoleContextProvider roleAccessProvider = new OseeDslRoleContextProvider(dslProvider);

         atsBranchObjectManager = new AtsBranchAccessManager(roleAccessProvider);
         cmService = new AtsCmAccessControl(atsBranchObjectManager);

         listener = new AtsDslProviderUpdateListener(dslProvider);
         eventService.addListener(EventQosType.NORMAL, listener);
         isInitialized = true;
      }
   }

   private CmAccessControl getProxiedService() {
      ensureInitialized();
      return cmService;
   }

   @Override
   public boolean isApplicable(ArtifactToken user, Object object) {
      return getProxiedService().isApplicable(user, object);
   }

   @Override
   public Collection<? extends IAccessContextId> getContextId(ArtifactToken user, Object object)  {
      return getProxiedService().getContextId(user, object);
   }

   @Override
   public AccessModel getAccessModel() {
      ensureInitialized();
      return accessModel;
   }

}
