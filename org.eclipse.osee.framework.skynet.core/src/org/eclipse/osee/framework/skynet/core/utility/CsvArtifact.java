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

import java.io.IOException;
import java.io.InputStream;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.NativeArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.StaticIdManager;

/**
 * Supports the loading, modifying and saving of General Document artifacts of extension csv
 * 
 * @author Donald G. Dunne
 */
public class CsvArtifact {

   private final NativeArtifact artifact;

   /**
    * @return the artifact
    */
   public NativeArtifact getArtifact() {
      return artifact;
   }

   public CsvArtifact(NativeArtifact nativeArtifact) {
      this.artifact = nativeArtifact;
   }

   public void setCsvData(String csvData) throws OseeCoreException {
      artifact.setSoleAttributeFromString(NativeArtifact.CONTENT_NAME, csvData);
   }

   public String getCsvData() throws OseeCoreException {
      InputStream inputStream = artifact.getNativeContent();
      try {
         String data = Lib.inputStreamToString(inputStream);
         return data;
      } catch (IOException ex) {
         throw new OseeCoreException(ex);
      }
   }

   public void appendData(String csvData) throws OseeCoreException {
      InputStream inputStream = artifact.getNativeContent();
      try {
         String data = Lib.inputStreamToString(inputStream);
         data.replaceFirst("\n+$", "");
         data = data + "\n" + csvData;
         setCsvData(data);
      } catch (IOException ex) {
         throw new OseeCoreException(ex);
      }
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
   public static CsvArtifact generateCsvArtifact(String staticId, String artifactName, String csvData, Branch branch) throws OseeCoreException {
      NativeArtifact artifact = (NativeArtifact) ArtifactTypeManager.addArtifact("General Document", branch);
      artifact.setDescriptiveName(artifactName);
      artifact.setSoleAttributeValue("Extension", "csv");
      artifact.setSoleAttributeFromString(NativeArtifact.CONTENT_NAME, csvData);
      StaticIdManager.setSingletonAttributeValue(artifact, staticId);
      return new CsvArtifact(artifact);
   }

   public static CsvArtifact getCsvArtifact(String staticId, Branch branch, boolean create) throws OseeCoreException {
      Artifact art = StaticIdManager.getSingletonArtifact("General Document", staticId, branch);
      if (art != null) return new CsvArtifact((NativeArtifact) art);
      return generateCsvArtifact(staticId, staticId, "", branch);
   }

}
