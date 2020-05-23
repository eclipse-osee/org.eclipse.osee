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

import java.util.Set;
import javax.ws.rs.core.Response.Status;
import org.eclipse.osee.define.api.RenderEndpoint;
import org.eclipse.osee.define.api.WordTemplateContentData;
import org.eclipse.osee.define.api.WordUpdateChange;
import org.eclipse.osee.define.api.WordUpdateData;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;
import org.eclipse.osee.jaxrs.OseeWebApplicationException;

/**
 * @author David W. Miller
 */
public class HttpWordUpdateRequest {
   public static WordUpdateChange updateWordArtifacts(WordUpdateData wud) {
      RenderEndpoint endpoint = ServiceUtil.getOseeClient().getRenderEndpoint();
      try {
         return endpoint.updateWordArtifacts(wud);
      } catch (Exception ex) {
         throw new OseeWebApplicationException(ex, Status.INTERNAL_SERVER_ERROR, "Exception in WordUpdateRequest");
      }
   }

   public static Pair<String, Set<String>> renderWordTemplateContent(WordTemplateContentData data) {
      RenderEndpoint endpoint = ServiceUtil.getOseeClient().getRenderEndpoint();
      return endpoint.renderWordTemplateContent(data);
   }

}
