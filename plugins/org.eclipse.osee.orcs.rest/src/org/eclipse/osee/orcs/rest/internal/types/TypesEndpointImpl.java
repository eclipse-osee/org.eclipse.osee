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

package org.eclipse.osee.orcs.rest.internal.types;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.rest.model.TypesEndpoint;

/**
 * @author Roberto E. Escobar
 */
public class TypesEndpointImpl implements TypesEndpoint {
   private final OrcsApi orcsApi;
   private final JdbcService jdbcService;

   public TypesEndpointImpl(OrcsApi orcsApi, JdbcService jdbcService) {
      this.orcsApi = orcsApi;
      this.jdbcService = jdbcService;
   }

   @Override
   public XResultData getHealthReport() {
      HealthReportOperation reportOp = new HealthReportOperation(orcsApi.tokenService(), jdbcService);
      return reportOp.run();
   }

   @Override
   public List<String> getProductApplicabilityTypes() {
      List<String> types = new ArrayList<String>();
      types.addAll(CoreAttributeTypes.ProductApplicability.getEnumStrValues());
      types.sort(Comparator.comparing(String::toString));
      return types;
   }
}