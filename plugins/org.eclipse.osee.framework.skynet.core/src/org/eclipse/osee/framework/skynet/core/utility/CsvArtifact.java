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

import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.StaticIdManager;

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

   public void setCsvData(String csvData) throws OseeCoreException {
      artifact.setSoleAttributeFromString(CoreAttributeTypes.NATIVE_CONTENT.getName(), csvData);
   }

   public String getCsvData() throws OseeCoreException {
      String csvData = null;
      if (artifact != null) {
         csvData = artifact.getSoleAttributeValueAsString(CoreAttributeTypes.NATIVE_CONTENT.getName(), null);
      }
      return csvData;
   }

   public void appendData(String csvData) throws OseeCoreException {
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
    * 
    * @param staticId
    * @param artifactName
    * @param csvData
    * @param branch
    * @throws OseeCoreException
    */
   public static CsvArtifact generateCsvArtifact(String staticId, String artifactName, String csvData, IOseeBranch branch) throws OseeCoreException {
      Artifact artifact = ArtifactTypeManager.addArtifact("General Document", branch);
      artifact.setName(artifactName);
      artifact.setSoleAttributeValue("Extension", "csv");
      artifact.setSoleAttributeFromString(CoreAttributeTypes.NATIVE_CONTENT.getName(), csvData);
      StaticIdManager.setSingletonAttributeValue(artifact, staticId);
      return new CsvArtifact(artifact);
   }

   public static CsvArtifact getCsvArtifact(String staticId, IOseeBranch branch, boolean create) throws OseeCoreException {
      Artifact art = StaticIdManager.getSingletonArtifact(CoreArtifactTypes.GeneralDocument, staticId, branch, true);
      if (art != null) {
         return new CsvArtifact(art);
      }
      return generateCsvArtifact(staticId, staticId, "", branch);
   }

}
