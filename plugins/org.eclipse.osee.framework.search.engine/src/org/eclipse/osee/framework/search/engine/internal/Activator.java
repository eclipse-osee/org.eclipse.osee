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
package org.eclipse.osee.framework.search.engine.internal;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.osee.framework.core.util.ServiceDependencyTracker;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.search.engine.ILanguage;
import org.eclipse.osee.framework.search.engine.internal.services.AttributeTaggerProviderServiceRegHandler;
import org.eclipse.osee.framework.search.engine.internal.services.AttributeTaggingManagerServiceRegHandler;
import org.eclipse.osee.framework.search.engine.internal.services.SearchEngineRegHandler;
import org.eclipse.osee.framework.search.engine.internal.services.SearchEngineTaggerRegHandler;
import org.eclipse.osee.framework.search.engine.language.EnglishLanguage;
import org.eclipse.osee.framework.search.engine.utility.TagProcessor;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

   private final Collection<ServiceDependencyTracker> trackers = new ArrayList<ServiceDependencyTracker>();

   @Override
   public void start(BundleContext context) throws Exception {
      ILanguage language = new EnglishLanguage();
      TagProcessor processor = new TagProcessor(language);

      trackers.add(new ServiceDependencyTracker(context, new AttributeTaggingManagerServiceRegHandler()));
      trackers.add(new ServiceDependencyTracker(context, new AttributeTaggerProviderServiceRegHandler(processor)));
      trackers.add(new ServiceDependencyTracker(context, new SearchEngineRegHandler(processor)));
      trackers.add(new ServiceDependencyTracker(context, new SearchEngineTaggerRegHandler()));

      for (ServiceDependencyTracker tracker : trackers) {
         tracker.open();
      }
   }

   @Override
   public void stop(BundleContext context) throws Exception {
      for (ServiceDependencyTracker tracker : trackers) {
         Lib.close(tracker);
      }
      trackers.clear();
   }
}
