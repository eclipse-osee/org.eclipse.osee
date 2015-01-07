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
package org.eclipse.osee.framework.ui.skynet.render.word;

import java.net.URI;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import org.eclipse.osee.define.report.api.DataRightInput;
import org.eclipse.osee.define.report.api.DataRightResult;
import org.eclipse.osee.define.report.api.PageOrientation;
import org.eclipse.osee.framework.core.client.OseeClientProperties;
import org.eclipse.osee.framework.ui.skynet.render.DataRightProvider;
import org.eclipse.osee.jaxrs.client.JaxRsClient;
import org.eclipse.osee.jaxrs.client.JaxRsExceptions;
import org.eclipse.osee.jaxrs.client.JaxRsWebTarget;

/**
 * @author Angel Avila
 */
public class DataRightProviderImpl implements DataRightProvider {

   private static final DataRightResult DEFAULT_RESPONSE = newDefaultResponse();

   @Override
   public DataRightResult getDataRights(DataRightInput request) {
      DataRightResult response = DEFAULT_RESPONSE;
      if (request != null && !request.isEmpty()) {
         String appServer = OseeClientProperties.getOseeApplicationServer();
         URI uri = UriBuilder.fromUri(appServer).path("define").path("publish").path("dataRights").build();

         JaxRsWebTarget target = JaxRsClient.newClient().target(uri);
         try {
            response = target.request(MediaType.APPLICATION_JSON).post(Entity.json(request), DataRightResult.class);
         } catch (Exception ex) {
            JaxRsExceptions.asOseeException(ex);
         }
      }
      return response;
   }

   @Override
   public DataRightInput createRequest() {
      return new DataRightInput();
   }

   private static DataRightResult newDefaultResponse() {
      return new DataRightResult() {

         @Override
         public String getContent(String guid, PageOrientation orientation) {
            return "";
         }
      };
   }
}
