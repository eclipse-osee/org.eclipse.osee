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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import org.eclipse.osee.ats.access.IAtsAccessControlService;
import org.eclipse.osee.framework.core.dsl.integration.AccessModelInterpreter;
import org.eclipse.osee.framework.core.dsl.integration.OseeDslAccessModel;
import org.eclipse.osee.framework.core.dsl.integration.OseeDslProvider;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.access.AccessModel;
import org.eclipse.osee.framework.core.services.CmAccessControl;
import org.eclipse.osee.framework.core.util.AbstractTrackingHandler;
import org.eclipse.osee.framework.core.util.ServiceBindType;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.event.IEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.event2.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event2.artifact.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event2.filter.ArtifactTypeEventFilter;
import org.eclipse.osee.framework.skynet.core.event2.filter.BranchGuidEventFilter;
import org.eclipse.osee.framework.skynet.core.event2.filter.IEventFilter;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Roberto E. Escobar
 */
public class AtsCmAccessControlRegHandler extends AbstractTrackingHandler {

   private final Map<Class<?>, ServiceBindType> serviceDeps = new HashMap<Class<?>, ServiceBindType>();

   public AtsCmAccessControlRegHandler() {
      serviceDeps.put(AccessModelInterpreter.class, ServiceBindType.SINGLETON);
      serviceDeps.put(IAtsAccessControlService.class, ServiceBindType.MANY);
   }
   private final Collection<IAtsAccessControlService> atsAccessServices =
      new CopyOnWriteArraySet<IAtsAccessControlService>();

   private ServiceRegistration registration;
   private IEventListener listener;

   @Override
   public Class<?>[] getDependencies() {
      return null;
   }

   @Override
   public Map<Class<?>, ServiceBindType> getConfiguredDependencies() {
      return serviceDeps;
   }

   @Override
   public void onActivate(BundleContext context, Map<Class<?>, Object> services) {
      AccessModelInterpreter interpreter = getService(AccessModelInterpreter.class, services);
      IAtsAccessControlService atsService = getService(IAtsAccessControlService.class, services);
      atsAccessServices.add(atsService);

      OseeDslProvider dslProvider = new AtsAccessOseeDslProvider();
      AccessModel accessModel = new OseeDslAccessModel(interpreter, dslProvider);
      CmAccessControl cmService = new AtsCmAccessControl(accessModel, atsAccessServices);
      registration = context.registerService(CmAccessControl.class.getName(), cmService, null);

      listener = new OseeDslProviderUpdateListener(dslProvider);
      OseeEventManager.addListener(listener);
   }

   @Override
   public void onDeActivate() {
      if (listener != null) {
         OseeEventManager.removeListener(listener);
      }
      atsAccessServices.clear();
      if (registration != null) {
         registration.unregister();
      }
   }

   @Override
   public void onServiceAdded(BundleContext context, Class<?> clazz, Object service) {
      atsAccessServices.add((IAtsAccessControlService) service);
   }

   @Override
   public void onServiceRemoved(BundleContext context, Class<?> clazz, Object service) {
      atsAccessServices.remove(service);
   }

   private static final class OseeDslProviderUpdateListener implements IArtifactEventListener {

      //@formatter:off
      private static final List<? extends IEventFilter> eventFilters = 
         Arrays.asList(
            new ArtifactTypeEventFilter(CoreArtifactTypes.AccessControlModel), 
            new BranchGuidEventFilter(CoreBranches.COMMON)
            );
      //@formatter:on

      private final OseeDslProvider dslProvider;

      public OseeDslProviderUpdateListener(OseeDslProvider dslProvider) {
         this.dslProvider = dslProvider;
      }

      @Override
      public List<? extends IEventFilter> getEventFilters() {
         return eventFilters;
      }

      @Override
      public void handleArtifactEvent(ArtifactEvent artifactEvent, Sender sender) {
         try {
            dslProvider.loadDsl();
         } catch (OseeCoreException ex) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         }
      }
   }
}
