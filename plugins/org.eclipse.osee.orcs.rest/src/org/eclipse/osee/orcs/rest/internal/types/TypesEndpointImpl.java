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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.rest.model.TypesEndpoint;

/**
 * @author Roberto E. Escobar
 */
public class TypesEndpointImpl implements TypesEndpoint {
   private final OrcsApi orcsApi;
   private final OrcsTokenService orcsTokenService;
   private final JdbcService jdbcService;

   public TypesEndpointImpl(OrcsApi orcsApi, JdbcService jdbcService) {
      this.orcsApi = Objects.requireNonNull(orcsApi, "orcsApi cannot be null.");
      this.orcsTokenService = Objects.requireNonNull(orcsApi.tokenService(), "orcsApi.tokenService must not be null");
      this.jdbcService = Objects.requireNonNull(jdbcService, "jdbcService cannot be null.");
   }

   @Override
   public XResultData getTypes() {
      XResultData rd = new XResultData();
      for (ArtifactTypeToken artType : orcsTokenService.getArtifactTypes()) {
         if (artType.equals(ArtifactTypeToken.SENTINEL)) {
            continue;
         }
         rd.logf("Art: [%s][%s]\n", artType.getName(), artType.getIdString());
         for (ArtifactTypeToken type : artType.getSuperTypes()) {
            rd.logf("   - IsOfType: [%s]\n", type.getName());
         }
         for (AttributeTypeToken attrType : artType.getValidAttributeTypes()) {
            rd.logf("   - Attr: [%s][%s] - [%s]\n\n", attrType.getName(), attrType.getIdString(),
               attrType.getStoreType());
         }
      }
      for (RelationTypeToken relType : orcsTokenService.getRelationTypes()) {
         if (relType.equals(RelationTypeToken.SENTINEL)) {
            continue;
         }
         ArtifactTypeToken artA = relType.getArtifactType(RelationSide.SIDE_A);
         ArtifactTypeToken artB = relType.getArtifactType(RelationSide.SIDE_B);
         rd.logf("   - Rel: [%s][%s] - [%s][%s] <-> [%s][%s]\n\n", relType.getName(), relType.getIdString(),
            artA.getName(), artA.getIdString(), artB.getName(), artB.getIdString());
      }
      return rd;
   }

   @Override
   public XResultData getHealthReport() {
      HealthReportOperation reportOp = new HealthReportOperation(orcsTokenService, jdbcService);
      return reportOp.run();
   }

   @Override
   public List<LinkedHashMap<String, Object>> getServerEnumTypesAndValues() {

      List<LinkedHashMap<String, Object>> output = new ArrayList<>();

      for (AttributeTypeToken attrType : orcsTokenService.getAttributeTypes()) {
         if (attrType.isEnumerated()) {

            LinkedHashMap<String, Object> attributeMap = new LinkedHashMap<String, Object>();

            attributeMap.put("Name", attrType.getName());
            attributeMap.put("Namespace", attrType.getNamespace().getName());
            attributeMap.put("Enum Values", attrType.toEnum().getEnumStrValues());

            output.add(attributeMap);
         }
      }
      return output;
   }
}