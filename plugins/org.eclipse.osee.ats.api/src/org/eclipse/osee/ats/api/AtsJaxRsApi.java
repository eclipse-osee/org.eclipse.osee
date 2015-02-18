/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api;

import javax.ws.rs.Path;
import org.eclipse.osee.ats.api.agile.AgileEndpointApi;
import org.eclipse.osee.ats.api.config.AtsConfigEndpointApi;
import org.eclipse.osee.ats.api.cpa.AtsCpaEndpointApi;
import org.eclipse.osee.ats.api.notify.AtsNotifyEndpointApi;
import org.eclipse.osee.ats.api.workflow.AtsActionEndpointApi;

/**
 * @author Donald G. Dunne
 */
public interface AtsJaxRsApi {

   @Path("notify")
   public AtsNotifyEndpointApi getNotify();

   @Path("config")
   public AtsConfigEndpointApi getConfig();

   @Path("cpa")
   public AtsCpaEndpointApi getCpa();

   @Path("action")
   public AtsActionEndpointApi getAction();

   @Path("agile")
   public AgileEndpointApi getAgile();

}
