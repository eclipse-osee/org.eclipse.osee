/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.ats.rest.internal.util.health;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.util.health.IAtsHealthCheckProvider;

/**
 * ATS Health Service to collect health checks to be run on server
 *
 * @author Donald G. Dunne
 */
public class AtsHealthCheckProviderService {

   public static List<IAtsHealthCheckProvider> healthCheckProviders = new LinkedList<>();

   public void addHealthCheckProvider(IAtsHealthCheckProvider healthCheck) {
      healthCheckProviders.add(healthCheck);
   }

   public static List<IAtsHealthCheckProvider> getHealthCheckProviders() {
      return healthCheckProviders;
   }

}
