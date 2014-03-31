/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.internal.search.artifact;

import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

/**
 * @author John R. Misinco
 * @author Roberto E. Escobar
 */
public abstract class ArtifactSearch {

   private final UriInfo uriInfo;
   private final Request request;
   private final String branchUuid;

   public ArtifactSearch(UriInfo uriInfo, Request request, String branchUuid) {
      this.uriInfo = uriInfo;
      this.request = request;
      this.branchUuid = branchUuid;
   }

   public UriInfo getUriInfo() {
      return uriInfo;
   }

   public Request getRequest() {
      return request;
   }

   public String getBranchUuid() {
      return branchUuid;
   }

}
