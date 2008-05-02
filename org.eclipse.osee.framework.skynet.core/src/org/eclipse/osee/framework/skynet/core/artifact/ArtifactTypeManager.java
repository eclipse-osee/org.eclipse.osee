/*
 * Created on May 2, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.artifact;

import java.sql.SQLException;
import org.eclipse.osee.framework.skynet.core.artifact.factory.IArtifactFactory;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;

/**
 * Contains methods specific to artifact types. All artifact methods will eventually be moved from the
 * ConfigurationPersistenceManager to here.
 * 
 * @author Donald G. Dunne
 */
public class ArtifactTypeManager {

   /**
    * Get a new instance of type artifactTypeName
    * 
    * @param artifactTypeName
    * @param branch
    * @return
    * @throws SQLException
    */
   public static Artifact addArtifact(String artifactTypeName, Branch branch) throws SQLException {
      return ConfigurationPersistenceManager.getInstance().getArtifactSubtypeDescriptor(artifactTypeName).makeNewArtifact(
            branch);
   }

   /**
    * Get a new instance of type artifactTypeName and set it's name.
    * 
    * @param artifactTypeName
    * @param branch
    * @param name
    * @return
    * @throws SQLException
    */
   public static Artifact addArtifact(String artifactTypeName, Branch branch, String name) throws SQLException {
      Artifact artifact =
            ConfigurationPersistenceManager.getInstance().getArtifactSubtypeDescriptor(artifactTypeName).makeNewArtifact(
                  branch);
      artifact.setDescriptiveName(name);
      return artifact;
   }

   /**
    * Get a new instance of the type of artifact. This is just a convenience method that calls makeNewArtifact on the
    * known factory with this descriptor for the descriptor parameter, and the supplied branch.
    * 
    * @param branch branch on which artifact will be created
    * @return Return artifact reference
    * @throws SQLException
    * @see IArtifactFactory#makeNewArtifact(Branch, ArtifactSubtypeDescriptor, String, String)
    */
   public static Artifact addArtifact(String artifactTypeName, Branch branch, String guid, String humandReadableId) throws SQLException {
      return ConfigurationPersistenceManager.getInstance().getArtifactSubtypeDescriptor(artifactTypeName).makeNewArtifact(
            branch, guid, humandReadableId);
   }

}
