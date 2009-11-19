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

import org.eclipse.osee.framework.core.IDataTranslationService;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchCommitData;
import org.eclipse.osee.framework.core.data.ChangeItem;
import org.eclipse.osee.framework.core.data.ChangeReportRequestData;
import org.eclipse.osee.framework.core.data.ChangeReportResponseData;
import org.eclipse.osee.framework.core.data.ChangeVersion;
import org.eclipse.osee.framework.core.data.CommitTransactionRecordResponse;
import org.eclipse.osee.framework.core.data.IBasicArtifact;
import org.eclipse.osee.framework.core.exchange.BasicArtifactDataTranslator;
import org.eclipse.osee.framework.core.exchange.BranchCommitDataResponder;
import org.eclipse.osee.framework.core.exchange.BranchCommitDataTranslator;
import org.eclipse.osee.framework.core.exchange.BranchTranslator;
import org.eclipse.osee.framework.core.exchange.ChangeItemTranslator;
import org.eclipse.osee.framework.core.exchange.ChangeReportRequestDataTranslator;
import org.eclipse.osee.framework.core.exchange.ChangeReportResponseDataTranslator;
import org.eclipse.osee.framework.core.exchange.ChangeVersionTranslator;
import org.eclipse.osee.framework.core.exchange.DataTranslationService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {
   public static final String PLUGIN_ID = "org.eclipse.osee.framework.core";
   private static Activator instance = null;
   private BundleContext bundleContext;
   private ServiceRegistration registration;

   public void start(BundleContext context) throws Exception {
      instance = this;
      instance.bundleContext = context;

      DataTranslationService service = new DataTranslationService();

      // TODO perform this using DS - register Data Translators
      service.addTranslator(BranchCommitData.class, new BranchCommitDataTranslator(service));
      service.addTranslator(CommitTransactionRecordResponse.class, new BranchCommitDataResponder(service));
      service.addTranslator(IBasicArtifact.class, new BasicArtifactDataTranslator());
      service.addTranslator(Branch.class, new BranchTranslator(service));
      service.addTranslator(ChangeVersion.class, new ChangeVersionTranslator());
      service.addTranslator(ChangeItem.class, new ChangeItemTranslator(service));
      service.addTranslator(ChangeReportResponseData.class, new ChangeReportResponseDataTranslator(service));
      service.addTranslator(ChangeReportRequestData.class, new ChangeReportRequestDataTranslator(service));

      registration = context.registerService(IDataTranslationService.class.getName(), service, null);
   }

   public void stop(BundleContext context) throws Exception {
      registration.unregister();
      registration = null;
      instance.bundleContext = null;
      instance = null;
   }

   public static BundleContext getBundleContext() {
      return instance.bundleContext;
   }
}
