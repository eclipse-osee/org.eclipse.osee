/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.coverage.internal;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import org.eclipse.osee.coverage.CoverageApi;

/**
 * @author Stephen J. Molaro
 */
@ApplicationPath("coverage")
public class CoverageApplication extends Application {

   private final Set<Object> resources = new HashSet<>();
   private CoverageApi coverageApi;

   public void bindCoverageApi(CoverageApi coverageApi) {
      this.coverageApi = coverageApi;
   }

   public void start() {
      resources.add(new CoverageEndpointImpl(coverageApi));
   }

   public void stop() {
      resources.clear();
   }

   @Override
   public Set<Object> getSingletons() {
      return resources;
   }
}