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
package org.eclipse.osee.framework.core.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.data.BranchCommitRequest;
import org.eclipse.osee.framework.core.data.BranchCommitResponse;
import org.eclipse.osee.framework.core.data.ChangeItem;
import org.eclipse.osee.framework.core.data.ChangeReportRequest;
import org.eclipse.osee.framework.core.data.ChangeReportResponse;
import org.eclipse.osee.framework.core.data.ChangeVersion;
import org.eclipse.osee.framework.core.data.IBasicArtifact;
import org.eclipse.osee.framework.core.exchange.BasicArtifactTranslator;
import org.eclipse.osee.framework.core.exchange.BranchCommitRequestTranslator;
import org.eclipse.osee.framework.core.exchange.BranchCommitResponseTranslator;
import org.eclipse.osee.framework.core.exchange.BranchTranslator;
import org.eclipse.osee.framework.core.exchange.ChangeItemTranslator;
import org.eclipse.osee.framework.core.exchange.ChangeReportRequestTranslator;
import org.eclipse.osee.framework.core.exchange.ChangeReportResponseTranslator;
import org.eclipse.osee.framework.core.exchange.ChangeVersionTranslator;
import org.eclipse.osee.framework.core.exchange.DataTranslationService;
import org.eclipse.osee.framework.core.model.ArtifactTypeFactory;
import org.eclipse.osee.framework.core.model.AttributeTypeFactory;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.BranchFactory;
import org.eclipse.osee.framework.core.model.OseeEnumTypeFactory;
import org.eclipse.osee.framework.core.model.OseeModelFactoryService;
import org.eclipse.osee.framework.core.model.RelationTypeFactory;
import org.eclipse.osee.framework.core.model.TransactionRecordFactory;
import org.eclipse.osee.framework.core.services.IDataTranslationService;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.services.IOseeCachingServiceProvider;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator, IOseeCachingServiceProvider {
   public static final String PLUGIN_ID = "org.eclipse.osee.framework.core";
   private static Activator instance = null;
   private BundleContext bundleContext;

   private enum TrackerId {
      OSEE_CACHING_SERVICE;
   }

   private final List<ServiceRegistration> services;
   private final Map<TrackerId, ServiceTracker> mappedTrackers;

   public Activator() {
      services = new ArrayList<ServiceRegistration>();
      mappedTrackers = new HashMap<TrackerId, ServiceTracker>();
   }

   public void start(BundleContext context) throws Exception {
      instance = this;
      instance.bundleContext = context;

      IOseeModelFactoryService factories = createFactoryService();
      createService(context, IOseeModelFactoryService.class, factories);

      IDataTranslationService service = createTranslationServer();
      createService(context, IDataTranslationService.class, service);

      createServiceTracker(context, IOseeCachingService.class, TrackerId.OSEE_CACHING_SERVICE);
   }

   private IOseeModelFactoryService createFactoryService() {
      return new OseeModelFactoryService(new BranchFactory(), new TransactionRecordFactory(),
            new ArtifactTypeFactory(), new AttributeTypeFactory(), new RelationTypeFactory(), new OseeEnumTypeFactory());
   }

   private IDataTranslationService createTranslationServer() {
      IDataTranslationService service = new DataTranslationService();

      service.addTranslator(IBasicArtifact.class, new BasicArtifactTranslator());
      service.addTranslator(Branch.class, new BranchTranslator(this));

      service.addTranslator(BranchCommitRequest.class, new BranchCommitRequestTranslator(service));
      service.addTranslator(BranchCommitResponse.class, new BranchCommitResponseTranslator(this));

      service.addTranslator(ChangeVersion.class, new ChangeVersionTranslator());
      service.addTranslator(ChangeItem.class, new ChangeItemTranslator(service));
      service.addTranslator(ChangeReportResponse.class, new ChangeReportResponseTranslator(service));
      service.addTranslator(ChangeReportRequest.class, new ChangeReportRequestTranslator(service));

      return service;
   }

   public void stop(BundleContext context) throws Exception {
      for (ServiceRegistration service : services) {
         service.unregister();
      }

      for (ServiceTracker tracker : mappedTrackers.values()) {
         tracker.close();
      }
      services.clear();
      mappedTrackers.clear();

      instance.bundleContext = null;
      instance = null;
   }

   public static BundleContext getBundleContext() {
      return instance.bundleContext;
   }

   private void createService(BundleContext context, Class<?> serviceInterface, Object serviceImplementation) {
      services.add(context.registerService(serviceInterface.getName(), serviceImplementation, null));
   }

   private void createServiceTracker(BundleContext context, Class<?> clazz, TrackerId trackerId) {
      ServiceTracker tracker = new ServiceTracker(context, clazz.getName(), null);
      tracker.open();
      mappedTrackers.put(trackerId, tracker);
   }

   @Override
   public IOseeCachingService getOseeCachingService() {
      return getTracker(TrackerId.OSEE_CACHING_SERVICE, IOseeCachingService.class);
   }

   private <T> T getTracker(TrackerId trackerId, Class<T> clazz) {
      ServiceTracker tracker = mappedTrackers.get(trackerId);
      Object service = tracker.getService();
      return clazz.cast(service);
   }
}
