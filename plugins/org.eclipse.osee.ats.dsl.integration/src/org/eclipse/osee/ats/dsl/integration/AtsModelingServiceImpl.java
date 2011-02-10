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
package org.eclipse.osee.ats.dsl.integration;

import org.eclipse.osee.ats.dsl.atsDsl.AtsDslFactory;
import org.eclipse.osee.ats.workdef.IAtsModelingService;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.services.IOseeCachingServiceFactory;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryService;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition;

/**
 * @author Donald G. Dunne
 */
public class AtsModelingServiceImpl implements IAtsModelingService {

   private final IOseeModelFactoryService modelFactoryService;
   private final IOseeCachingService systemCachingService;
   private final IOseeCachingServiceFactory cachingFactoryService;
   private final AtsDslFactory modelFactory;

   public AtsModelingServiceImpl(IOseeModelFactoryService modelFactoryService, IOseeCachingService systemCachingService, IOseeCachingServiceFactory cachingFactoryService, AtsDslFactory dslFactory) {
      this.modelFactoryService = modelFactoryService;
      this.systemCachingService = systemCachingService;
      this.cachingFactoryService = cachingFactoryService;
      this.modelFactory = dslFactory;

   }

   @Override
   public WorkFlowDefinition getWorkFlowDefinition(String id) {
      return null;
   }

}
