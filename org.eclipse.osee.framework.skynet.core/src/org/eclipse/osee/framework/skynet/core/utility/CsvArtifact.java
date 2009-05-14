/*
 * Created on May 14, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.utility;

import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.GeneralData;
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
      return artifact.getSoleAttributeValueAsString(NativeArtifact.CONTENT_NAME, "");
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
      NativeArtifact artifact = (NativeArtifact) ArtifactTypeManager.addArtifact(GeneralData.ARTIFACT_TYPE, branch);
      artifact.setDescriptiveName(artifactName);
      artifact.setSoleAttributeFromString(NativeArtifact.CONTENT_NAME, csvData);
      StaticIdManager.setSingletonAttributeValue(artifact, staticId);
      return new CsvArtifact(artifact);
   }
}
