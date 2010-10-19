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
package org.eclipse.osee.framework.ui.skynet.internal;

import java.util.Map;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.AbstractTrackingHandler;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.ui.skynet.artifact.IAccessPolicyHandlerService;
import org.eclipse.osee.framework.ui.skynet.artifact.prompt.IPromptFactory;
import org.eclipse.osee.framework.ui.skynet.artifact.prompt.PromptFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class ArtifactPromptServiceRegHandler extends AbstractTrackingHandler {

   //@formatter:off
   private static final Class<?>[] SERVICE_DEPENDENCY = new Class<?>[] {
      IAccessPolicyHandlerService.class,
   };
   //@formatter:on

   private ServiceRegistration serviceRegistration;
   private ArtifactPromptService service;

   @Override
   public Class<?>[] getDependencies() {
      return SERVICE_DEPENDENCY;
   }

   @Override
   public void onActivate(BundleContext context, Map<Class<?>, Object> services) {
      IAccessPolicyHandlerService policyHandlerService = getService(IAccessPolicyHandlerService.class, services);

      IPromptFactory promptFactory = new PromptFactory();
      service = new ArtifactPromptService(promptFactory, policyHandlerService);
      serviceRegistration = context.registerService(ArtifactPromptService.class.getName(), service, null);
   }

   @Override
   public void onDeActivate() {
      if (serviceRegistration != null) {
         serviceRegistration.unregister();
      }
   }

   public ArtifactPromptService getService() throws OseeCoreException {
      Conditions.checkNotNull(service, "ArtifactPromptService", "ArtifactPromptService has not been registered");
      return service;
   }
}
