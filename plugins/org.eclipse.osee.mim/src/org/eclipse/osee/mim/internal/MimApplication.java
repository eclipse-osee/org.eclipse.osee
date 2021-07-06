/*********************************************************************
 * Copyright (c) 2021 Boeing
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

package org.eclipse.osee.mim.internal;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import org.eclipse.osee.mim.MimApi;

/**
 * @author Audrey E Denk
 */
@ApplicationPath("mim")
public class MimApplication extends Application {

   private final Set<Object> resources = new HashSet<>();
   private MimApi mimApi;

   public void bindMimApi(MimApi mimApi) {
      this.mimApi = mimApi;
   }

   public void start() {
      int i = 1;
      resources.add(new LogicalTypeEndpointImpl(mimApi));
      resources.add(new BranchAccessor(mimApi));
      resources.add(new EnumEndpointImpl());
   }

   public void stop() {
      resources.clear();
   }

   @Override
   public Set<Object> getSingletons() {
      return resources;
   }
}