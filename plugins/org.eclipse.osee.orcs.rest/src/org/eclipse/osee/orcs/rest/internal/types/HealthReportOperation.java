/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.internal.types;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.jdbc.JdbcStatement;

/**
 * @author Donald G. Dunne
 */
public class HealthReportOperation {

   private final OrcsTokenService tokenService;
   private final JdbcService jdbcService;

   public HealthReportOperation(OrcsTokenService tokenService, JdbcService jdbcService) {
      this.tokenService = tokenService;
      this.jdbcService = jdbcService;
   }

   public XResultData run() {
      XResultData rd = new XResultData();

      List<AttributeTypeGeneric<?>> attrTypes = new ArrayList<>();
      attrTypes.addAll(tokenService.getAttributeTypes());

      for (ArtifactTypeToken artType : tokenService.getArtifactTypes()) {
         for (AttributeTypeToken attrType : artType.getValidAttributeTypes()) {
            attrTypes.remove(attrType);
         }
      }

      rd.log("ORCS Types Health Check\n");
      rd.log("Server: " + System.getProperty("OseeApplicationServer") + "\n");
      rd.log("Attribute Types defined in code without Artifact reference in code: \n");
      for (AttributeTypeToken attrType : attrTypes) {
         if (attrType.isValid()) {
            rd.error(attrType.toStringWithId());
         }
      }

      rd.log("\nAttribute Types used in database and not defined in code: \n");

      JdbcStatement chStmt = jdbcService.getClient().getStatement();
      try {
         String query = "SELECT UNIQUE attr_type_id FROM osee_attribute";
         chStmt.runPreparedQuery(query);
         while (chStmt.next()) {
            Long attrTypeId = chStmt.getLong("attr_type_id");
            AttributeTypeGeneric<?> attributeType = tokenService.getAttributeTypeOrSentinel(attrTypeId);
            if (attributeType.isInvalid()) {
               rd.error(attrTypeId.toString());
            }
         }
      } finally {
         chStmt.close();
      }

      return rd;
   }
}
