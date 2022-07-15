/*********************************************************************
 * Copyright (c) 2013 Boeing
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

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.util.health.AtsHealthEndpointApi;
import org.eclipse.osee.ats.rest.internal.util.health.operations.HealthOperations;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.jdbc.JdbcService;

/**
 * @author Donald G. Dunne
 */
public final class AtsHealthEndpointImpl implements AtsHealthEndpointApi {

   private AtsApi atsApi;
   private JdbcService jdbcService;

   public AtsHealthEndpointImpl() {
      // for osgi instantiation; this optionally sets the mail service if available
   }

   public AtsHealthEndpointImpl(AtsApi atsApi, JdbcService jdbcService) {
      this.atsApi = atsApi;
      this.jdbcService = jdbcService;
   }

   /**
    * @return html representation of ATS Health Checks
    */
   @Override
   public String get() {
      Thread.currentThread().setName("ATS Health Check Operation");
      AtsHealthCheckOperation validate = new AtsHealthCheckOperation(atsApi, jdbcService);
      XResultData rd = validate.run();
      return rd.toString().replaceAll("\n", "</br>");
   }

   @Override
   public Boolean alive() {
      return true;
   }

   @Override
   public XResultData dupArtReport(ArtifactId id, String newArtId) {
      HealthOperations ops = new HealthOperations(atsApi);
      return ops.getDuplicateArtifactReport(id, newArtId);
   }

}
