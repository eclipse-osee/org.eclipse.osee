/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.utility;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * Supports the loading, modifying and saving of General Document artifacts of extension csv
 *
 * @author Donald G. Dunne
 */
public class CsvArtifact {

   private final Artifact artifact;

   public CsvArtifact(Artifact nativeArtifact) {
      this.artifact = nativeArtifact;
   }

   public Artifact getArtifact() {
      return artifact;
   }

   public void setCsvData(String csvData)  {
      artifact.setSoleAttributeFromString(CoreAttributeTypes.NativeContent, csvData);
   }

   public String getCsvData()  {
      String csvData = null;
      if (artifact != null) {
         csvData = artifact.getSoleAttributeValueAsString(CoreAttributeTypes.NativeContent, null);
      }
      return csvData;
   }

   public void appendData(String csvData)  {
      String data = getCsvData();
      if (Strings.isValid(data)) {
         data = data.replaceFirst("\n+$", "");
         data = data + "\n" + csvData;
      } else {
         data = csvData;
      }
      setCsvData(data);
   }

   /**
    * Creates a new un-persisted CsvArtifact
    */
   public static CsvArtifact generateCsvArtifact(String staticId, String artifactName, String csvData, BranchId branch)  {
      Artifact artifact = ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralDocument, branch);
      artifact.setName(artifactName);
      artifact.setSoleAttributeValue(CoreAttributeTypes.Extension, "csv");
      artifact.setSoleAttributeFromString(CoreAttributeTypes.NativeContent, csvData);
      artifact.setSingletonAttributeValue(CoreAttributeTypes.StaticId, staticId);
      return new CsvArtifact(artifact);
   }

   public static CsvArtifact getCsvArtifact(String staticId, BranchId branch, boolean create)  {
      Artifact art = null;
      try {
         art = ArtifactQuery.getArtifactFromTypeAndAttribute(CoreArtifactTypes.GeneralDocument,
            CoreAttributeTypes.StaticId, staticId, branch);
      } catch (ArtifactDoesNotExist ex) {
         // do nothing
      }

      if (art != null) {
         return new CsvArtifact(art);
      }
      return generateCsvArtifact(staticId, staticId, "", branch);
   }

   public List<List<String>> getRows(boolean ignoreHeaderRow)  {
      List<List<String>> rows = new ArrayList<>();
      String csvData = getCsvData();
      String[] csvLines = csvData.split("\n");

      int rowIndex = 0;
      for (String csvLine : csvLines) {
         if (ignoreHeaderRow && rowIndex > 0 || !ignoreHeaderRow) {
            String[] values = csvLine.split(",");
            List<String> row = new ArrayList<>();
            for (String value : values) {
               value = value.trim();
               row.add(value);
            }
            rows.add(row);
         }
         rowIndex++;
      }
      return rows;
   }
}
