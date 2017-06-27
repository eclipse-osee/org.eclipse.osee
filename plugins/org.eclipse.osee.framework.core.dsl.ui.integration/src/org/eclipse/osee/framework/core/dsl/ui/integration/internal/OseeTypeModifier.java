/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.dsl.ui.integration.internal;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.osee.framework.core.client.OseeClientProperties;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.OrcsTypesConfig;
import org.eclipse.osee.framework.core.data.OrcsTypesData;
import org.eclipse.osee.framework.core.data.OrcsTypesSheet;
import org.eclipse.osee.framework.core.data.OrcsTypesVersion;
import org.eclipse.osee.framework.core.dsl.OseeDslResourceUtil;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeType;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.render.AttributeModifier;
import org.eclipse.osee.jaxrs.client.JaxRsClient;

public class OseeTypeModifier implements AttributeModifier {

   @Override
   public InputStream modifyForSave(Artifact owner, File file) throws OseeCoreException {
      String value = null;
      try {
         value = Lib.fileToString(file);
      } catch (IOException ex) {
         OseeCoreException.wrapAndThrow(ex);
      }

      String appServer = OseeClientProperties.getOseeApplicationServer();
      String typesUri = String.format("%s/orcs/types/config", appServer);
      OseeDsl oseeDsl = null;
      try {
         Response response = JaxRsClient.newClient().target(typesUri).request(MediaType.APPLICATION_JSON_TYPE).get();
         if (javax.ws.rs.core.Response.Status.OK.getStatusCode() != response.getStatus()) {
            throw new OseeStateException("Error retrieving orcs types config " + response);
         }
         List<ArtifactId> artIds = new LinkedList<>();
         OrcsTypesConfig config = response.readEntity(OrcsTypesConfig.class);
         for (OrcsTypesVersion version : config.getVersions()) {
            if (OrcsTypesData.OSEE_TYPE_VERSION.intValue() == version.getVersionNum()) {
               for (OrcsTypesSheet sheet : version.getSheets()) {
                  artIds.add(ArtifactId.valueOf(sheet.getArtifactId()));
               }
            }
         }

         List<Artifact> artifacts = ArtifactQuery.getArtifactListFrom(artIds, COMMON);
         StringBuilder combinedSheets = new StringBuilder();
         for (Artifact art : artifacts) {
            String sheetData;
            if (art.equals(owner)) {
               sheetData = value;
            } else {
               sheetData = art.getSoleAttributeValueAsString(CoreAttributeTypes.UriGeneralStringData, "");
            }
            combinedSheets.append(sheetData.replaceAll("import\\s+\"", "// import \""));
         }
         oseeDsl = OseeDslResourceUtil.loadModel("osee:/TypeModel.osee", combinedSheets.toString()).getModel();
      } catch (Exception ex) {
         OseeCoreException.wrapAndThrow(ex);
      }

      Set<Long> uuids = new HashSet<>();

      if (oseeDsl != null) {
         for (EObject object : oseeDsl.eContents()) {
            if (object instanceof OseeType) {
               addUuid(uuids, (OseeType) object);
            }
         }
      }

      Conditions.checkExpressionFailOnTrue(uuids.contains(0L), "Uuid of 0L is not allowed");

      InputStream inputStream = null;
      try {
         inputStream = Lib.stringToInputStream(value);
      } catch (Exception ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
      return inputStream;
   }

   private void addUuid(Set<Long> set, OseeType type) throws OseeCoreException {
      Long uuid = Long.valueOf(type.getId());
      boolean wasAdded = set.add(uuid);
      Conditions.checkExpressionFailOnTrue(!wasAdded, "Duplicate uuid found: [0x%X]", uuid);
   }
}