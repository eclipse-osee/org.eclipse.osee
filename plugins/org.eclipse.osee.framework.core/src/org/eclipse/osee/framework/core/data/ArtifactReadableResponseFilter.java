/*********************************************************************
 * Copyright (c) 2022 Boeing
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
package org.eclipse.osee.framework.core.data;

import java.util.List;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

/**
 * @author Christopher Rebuck
 */
@Provider
public class ArtifactReadableResponseFilter implements ContainerResponseFilter {
   @Override
   public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
      Object entity = responseContext.getEntity();
      if (entity instanceof List<?>) {
         List<?> responseList = (List<?>) entity;
         if (!responseList.isEmpty() && responseList.get(0) instanceof ArtifactReadable) {
            Integer hashedEntity = System.identityHashCode(responseList.get(responseList.size() - 1));
            ArtifactReadableSerializer.saveHashedEntity(hashedEntity);
         }
      }
   }
}
