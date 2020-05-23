/*********************************************************************
 * Copyright (c) 2014 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.template.engine.internal;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * @author Roberto E. Escobar
 */
@ApplicationPath("jaxrs-admin")
public class JaxRsTemplateApplication extends Application {

   private Set<Object> singletons;
   private TemplateRegistry registry;

   public void setTemplateRegistry(TemplateRegistry registry) {
      this.registry = registry;
   }

   public void start() {
      singletons = new HashSet<>();
      singletons.add(new TemplateContributions(registry));
   }

   public void stop() {
      singletons = null;
   }

   @Override
   public Set<Object> getSingletons() {
      return singletons;
   }
}