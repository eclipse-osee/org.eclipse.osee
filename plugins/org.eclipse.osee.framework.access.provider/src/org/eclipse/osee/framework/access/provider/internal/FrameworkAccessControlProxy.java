/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.access.provider.internal;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.IAccessContextId;
import org.eclipse.osee.framework.core.dsl.integration.AccessModelInterpreter;
import org.eclipse.osee.framework.core.dsl.integration.RoleContextProvider;
import org.eclipse.osee.framework.core.dsl.ui.integration.operations.OseeDslRoleContextProvider;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.model.access.AccessModel;
import org.eclipse.osee.framework.core.model.access.HasAccessModel;
import org.eclipse.osee.framework.core.services.CmAccessControl;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.event.OseeEventService;
import org.eclipse.osee.framework.skynet.core.event.filter.ArtifactEventFilter;
import org.eclipse.osee.framework.skynet.core.event.filter.ArtifactTypeEventFilter;
import org.eclipse.osee.framework.skynet.core.event.filter.BranchIdEventFilter;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.EventQosType;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event.listener.IEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * @author John R. Misinco
 */
public class FrameworkAccessControlProxy implements CmAccessControl, HasAccessModel {

   private ServiceReference<AccessModelInterpreter> reference;
   private CmAccessControl frameworkAccessControl;
   private AccessModel accessModel;
   private IEventListener listener;
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
      frameworkAccessControl = null;
      accessModel = null;
   }

   private boolean isReady() {
      return reference != null && eventService != null && bundleContext != null;
   }

   private synchronized void ensureInitialized() {
      if (isReady() && !isInitialized) {
         AccessModelInterpreter interpreter = bundleContext.getService(reference);

         FrameworkDslProvider frameworkDslProvider = new FrameworkDslProvider("osee:/xtext/framework.access.osee");
         RoleContextProvider roleProvider = new OseeDslRoleContextProvider(frameworkDslProvider);

         accessModel = new FrameworkAccessModel(interpreter, frameworkDslProvider);
         frameworkAccessControl = new FrameworkAccessControl(roleProvider);

         listener = new DslUpdateListener(frameworkDslProvider);
         eventService.addListener(EventQosType.NORMAL, listener);
         isInitialized = true;
      }
   }

   private void checkInitialized() {
      Conditions.checkNotNull(getAccessControl(), "frameworkAccess",
         "FrameworkAccessControlService not properly initialized");
   }

   private CmAccessControl getAccessControl() {
      ensureInitialized();
      return frameworkAccessControl;
   }

   @Override
   public AccessModel getAccessModel() {
      ensureInitialized();
      return accessModel;
   }

   @Override
   public boolean isApplicable(ArtifactToken user, Object object) {
      return getAccessControl().isApplicable(user, object);
   }

   @Override
   public Collection<? extends IAccessContextId> getContextId(ArtifactToken user, Object object) {
      checkInitialized();
      return getAccessControl().getContextId(user, object);
   }

   private final class DslUpdateListener implements IArtifactEventListener {

      private List<? extends IEventFilter> eventFilters;
      private final FrameworkDslProvider dslProvider;

      public DslUpdateListener(FrameworkDslProvider dslProvider) {
         this.dslProvider = dslProvider;
      }

      @Override
      public synchronized List<? extends IEventFilter> getEventFilters() {
         if (eventFilters == null) {
            Artifact artifact = dslProvider.getStorageArtifact();
            if (artifact != null) {
               eventFilters = Arrays.asList(new ArtifactEventFilter(artifact));
            } else {
               eventFilters = Arrays.asList(new ArtifactTypeEventFilter(CoreArtifactTypes.AccessControlModel),
                  new BranchIdEventFilter(CoreBranches.COMMON));
            }
         }
         return eventFilters;
      }

      @Override
      public void handleArtifactEvent(ArtifactEvent artifactEvent, Sender sender) {
         try {
            dslProvider.loadDsl();
         } catch (OseeCoreException ex) {
            OseeLog.log(DefaultFrameworkAccessConstants.class, Level.SEVERE, ex);
         }
      }
   }
}
