/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.framework.skynet.core.httpRequests;

import java.io.InputStream;
import java.util.Set;
import javax.ws.rs.core.Response.Status;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.eclipse.osee.define.api.MsWordPreviewRequestData;
import org.eclipse.osee.define.api.WordTemplateContentData;
import org.eclipse.osee.define.api.WordUpdateChange;
import org.eclipse.osee.define.api.WordUpdateData;
import org.eclipse.osee.define.api.publishing.PublishingEndpoint;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;
import org.eclipse.osee.jaxrs.OseeWebApplicationException;

/**
 * Wrapper class to simplify Renderer Endpoint REST API calls.
 *
 * @author David W. Miller
 * @author Loren K. Ashley
 */

public class PublishingRequestHandler {

   /**
    * Saves a singleton instance of the {@link PublishingRequestHandler} class;
    */

   private static PublishingRequestHandler instance;

   /*
    * Creates the singleton instance, thread safe
    */

   static {
      PublishingRequestHandler.instance = new PublishingRequestHandler();
   }

   /**
    * Caches the {@link PublishingEndpoint}.
    */

   private final PublishingEndpoint publishingEndpoint;

   /**
    * Creates the singleton instance and caches the {@link PublishingEndpoint}.
    */

   private PublishingRequestHandler() {
      this.publishingEndpoint = ServiceUtil.getOseeClient().getPublishingEndpoint();
   }

   /**
    * Calls the REST API PublishingEndpoint method {@link msWordPreview}.
    *
    * @throws OseeWebApplicationException when the REST API call fails.
    */

   public static Attachment msWordPreview(MsWordPreviewRequestData msWordPreviewRequestData) {
      try {
         return PublishingRequestHandler.instance.publishingEndpoint.msWordPreview(msWordPreviewRequestData);
      } catch (Exception e) {
         throw new OseeWebApplicationException(e, Status.INTERNAL_SERVER_ERROR,
            "Exception in \"msWordPreview\" request.");
      }
   }

   /**
    * Calls the REST API PublishingEndpoint method {@link PublishingEndpoint#renderWordTemplateContent}.
    *
    * @throws OseeWebApplicationException when the REST API call fails.
    */

   public static Pair<String, Set<String>> renderWordTemplateContent(WordTemplateContentData data) {
      try {
         return PublishingRequestHandler.instance.publishingEndpoint.renderWordTemplateContent(data);
      } catch (Exception e) {
         throw new OseeWebApplicationException(e, Status.INTERNAL_SERVER_ERROR,
            "Exception in \"renderWordTemplateContent\" request.");
      }
   }

   /**
    * Calls the REST API PublishingEndpoint method {@link PublishingEndpoint#updateWordArtifacts}.
    *
    * @throws OseeWebApplicationException when the REST API call fails.
    */

   public static WordUpdateChange updateWordArtifacts(WordUpdateData wordUpdateData) {
      try {
         return PublishingRequestHandler.instance.publishingEndpoint.updateWordArtifacts(wordUpdateData);
      } catch (Exception e) {
         throw new OseeWebApplicationException(e, Status.INTERNAL_SERVER_ERROR,
            "Exception in \"updateWordArtifacts\" request.");
      }
   }

}

/* EOF */
