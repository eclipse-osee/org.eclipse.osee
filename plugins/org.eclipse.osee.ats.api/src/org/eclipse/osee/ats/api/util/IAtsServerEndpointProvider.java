/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.util;

import org.eclipse.osee.ats.api.agile.AgileEndpointApi;
import org.eclipse.osee.ats.api.config.AtsConfigEndpointApi;
import org.eclipse.osee.ats.api.ev.AtsWorkPackageEndpointApi;
import org.eclipse.osee.ats.api.notify.AtsNotifyEndpointApi;
import org.eclipse.osee.ats.api.task.AtsTaskEndpointApi;
import org.eclipse.osee.ats.api.workflow.AtsActionEndpointApi;
import org.eclipse.osee.ats.api.workflow.AtsWorldEndpointApi;

/**
 * Client provider for server endpoints.
 *
 * @author Donald G. Dunne
 */
public interface IAtsServerEndpointProvider {

   public AtsNotifyEndpointApi getNotifyEndpoint();

   public AtsTaskEndpointApi getTaskEp();

   /**
    * This should not be used unless configurations are being updated. Use AtsApi.getConfigurations
    */
   public AtsConfigEndpointApi getConfigEndpoint();

   public AgileEndpointApi getAgileEndpoint();

   public AtsWorkPackageEndpointApi getWorkPackageEndpoint();

   public AtsActionEndpointApi getActionEndpoint();

   AtsWorldEndpointApi getWorldEndpoint();

}
