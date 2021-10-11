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

package org.eclipse.osee.ats.rest.internal.config;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Angel Avila
 */
public class TypeCountWriter {

   private final OrcsApi orcsApi;

   public TypeCountWriter(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public void write(BranchId branch, Set<ArtifactId> newArts, Set<ArtifactId> modifiedArts, Set<ArtifactId> deletedArts, List<Long> artTypes, List<Long> attrTypes, OutputStream outputStream) {
      try {
         Writer writer = new OutputStreamWriter(outputStream, "UTF-8");
         ExcelXmlWriter sheetWriter = new ExcelXmlWriter(writer);

         List<AttributeTypeToken> attributes = getAttrTypes(attrTypes);
         String[] headers = getHeaders(attributes);
         int columns = headers.length;
         sheetWriter.startSheet("Type Count Report", headers.length);
         sheetWriter.writeRow((Object[]) headers);

         List<ArtifactTypeToken> artifactTypes = getTypes(artTypes);

         if (!newArts.isEmpty()) {
            ResultSet<ArtifactReadable> newArtifacts =
               orcsApi.getQueryFactory().fromBranch(branch).andTypeEquals(artifactTypes).andIds(newArts).getResults();
            for (ArtifactReadable art : newArtifacts) {
               String[] row = new String[columns];
               int index = 0;

               row[index++] = art.getName();
               row[index++] = "NEW";
               row[index++] = art.getArtifactType().toString();
               for (AttributeTypeToken type : attributes) {
                  row[index++] = art.getAttributeValues(type).toString();
               }

               sheetWriter.writeRow((Object[]) row);
            }
         }

         if (!modifiedArts.isEmpty()) {
            ResultSet<ArtifactReadable> modifiedArtifacts =
               orcsApi.getQueryFactory().fromBranch(branch).andTypeEquals(artifactTypes).andIds(
                  modifiedArts).getResults();
            for (ArtifactReadable art : modifiedArtifacts) {
               String[] row = new String[columns];
               int index = 0;

               row[index++] = art.getName();
               row[index++] = "MODIFIED";
               row[index++] = art.getArtifactType().toString();
               for (AttributeTypeToken type : attributes) {
                  row[index++] = art.getAttributeValues(type).toString();
               }
               sheetWriter.writeRow((Object[]) row);
            }
         }

         if (!deletedArts.isEmpty()) {
            ResultSet<ArtifactReadable> deletedArtifacts =
               orcsApi.getQueryFactory().fromBranch(branch).andTypeEquals(artifactTypes).andIds(
                  deletedArts).getResults();
            for (ArtifactReadable art : deletedArtifacts) {
               String[] row = new String[columns];
               int index = 0;

               row[index++] = art.getName();
               row[index++] = "DELETED";
               row[index++] = art.getArtifactType().toString();
               for (AttributeTypeToken type : attributes) {
                  row[index++] = art.getAttributeValues(type).toString();
               }
               sheetWriter.writeRow((Object[]) row);
            }
         }

         sheetWriter.endSheet();
         sheetWriter.endWorkbook();
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   private List<ArtifactTypeToken> getTypes(List<Long> typeIds) {
      List<ArtifactTypeToken> toReturn = new ArrayList<>();

      Collection<? extends ArtifactTypeToken> allTypes = orcsApi.tokenService().getArtifactTypes();
      for (ArtifactTypeToken type : allTypes) {
         if (typeIds.contains(type.getId())) {
            toReturn.add(type);
         }
      }
      return toReturn;
   }

   private List<AttributeTypeToken> getAttrTypes(List<Long> typeIds) {
      List<AttributeTypeToken> toReturn = new ArrayList<>();

      Collection<AttributeTypeGeneric<?>> allTypes = orcsApi.tokenService().getAttributeTypes();
      for (AttributeTypeGeneric<?> type : allTypes) {
         if (typeIds.contains(type.getId())) {
            toReturn.add(type);
         }
      }
      return toReturn;
   }

   private String[] getHeaders(List<AttributeTypeToken> types) {
      String[] toReturn = new String[types.size() + 3];
      int index = 0;
      toReturn[index++] = "Name";
      toReturn[index++] = "Mod Type";
      toReturn[index++] = "Art Type";
      for (AttributeTypeToken type : types) {
         toReturn[index++] = type.getName();
      }
      return toReturn;
   }
}
