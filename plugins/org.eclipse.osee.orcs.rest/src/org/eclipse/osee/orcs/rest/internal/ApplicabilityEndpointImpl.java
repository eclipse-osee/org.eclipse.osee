/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.internal;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.rest.model.Applicabilities;
import org.eclipse.osee.orcs.rest.model.Applicability;
import org.eclipse.osee.orcs.rest.model.ApplicabilityEndpoint;
import org.eclipse.osee.orcs.rest.model.ApplicabilityId;
import org.eclipse.osee.orcs.rest.model.ApplicabilityIds;
import org.eclipse.osee.orcs.rest.model.ArtifactIds;

/**
 * @author Donald G. Dunne
 */
public class ApplicabilityEndpointImpl implements ApplicabilityEndpoint {

   private final OrcsApi orcsApi;

   @Context
   private UriInfo uriInfo;

   @Context
   private HttpHeaders httpHeaders;

   public ApplicabilityEndpointImpl(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   @Override
   public ApplicabilityIds getApplicabilityIds() {
      // TBD - Replace with call to IApplicabilityService calls once implemented
      ApplicabilityIds ids = new ApplicabilityIds();
      ids.getApplicabilityIds().add(new ApplicabilityId(345L, "ARC-210"));
      ids.getApplicabilityIds().add(new ApplicabilityId(366L, "COMM"));
      ids.getApplicabilityIds().add(new ApplicabilityId(376L, "ASM"));
      ids.getApplicabilityIds().add(new ApplicabilityId(368L, "UTF"));
      ids.getApplicabilityIds().add(new ApplicabilityId(466L, "MDR"));
      return ids;
   }

   @Override
   public Applicabilities getApplicabilities(ArtifactIds artifactIds) {
      // TBD - Replace with call to IApplicabilityService calls once implemented
      ApplicabilityId arc210 = new ApplicabilityId(345L, "ARC-210");
      ApplicabilityId comm = new ApplicabilityId(366L, "COMM");

      Applicabilities results = new Applicabilities();
      results.getApplicabilities().add(new Applicability(12L, arc210));
      results.getApplicabilities().add(new Applicability(13L, arc210));
      results.getApplicabilities().add(new Applicability(23L, comm));
      results.getApplicabilities().add(new Applicability(24L, comm));
      for (Long artId : artifactIds.getArtifactIds()) {
         results.getApplicabilities().add(new Applicability(artId, comm));
      }
      return results;
   }

   @Override
   public void setApplicability(Applicability appl) {
      // TBD - Implement this with call to IApplicabilityService
   }

   @Override
   public void setApplicabilities(Applicabilities applicabilities) {
      // TBD - Implement this with call to IApplicabilityService
   }

}
