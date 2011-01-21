/*
 * Created on Feb 8, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.artifact;

import org.eclipse.osee.ats.util.AtsArtifactTypes;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

public class AtsArtifactToken extends ArtifactToken {

   public static AtsArtifactToken HeadingFolder = new AtsArtifactToken("Action Tracking System",
      CoreArtifactTypes.Folder, "AAABER+3yR4A8O7WYQ+Xbw");
   public static AtsArtifactToken TopTeamDefinition = new AtsArtifactToken("Teams", AtsArtifactTypes.TeamDefinition,
      "AAABER+35b4A8O7WHrXTiA");
   public static AtsArtifactToken TopActionableItem = new AtsArtifactToken("Actionable Items",
      AtsArtifactTypes.ActionableItem, "AAABER+37QEA8O7WSQaqJQ");
   public static AtsArtifactToken ConfigFolder = new AtsArtifactToken("Config", CoreArtifactTypes.Folder,
      "AAABF4n18eYAc1ruQSSWdg");
   public static AtsArtifactToken WorkDefinitionsFolder = new AtsArtifactToken("Work Definitions",
      CoreArtifactTypes.Folder, "ADTfjCLEj2DH2WYyeOgA");
   public static AtsArtifactToken WorkPagesFolder = new AtsArtifactToken("Work Pages", CoreArtifactTypes.Folder,
      "AAABGnncY_gAAo+3N69ASA");
   public static AtsArtifactToken WorkRulesFolder = new AtsArtifactToken("Work Rules", CoreArtifactTypes.Folder,
      "AAABGnmhCyYAoJoIciyaag");
   public static AtsArtifactToken WorkWidgetsFolder = new AtsArtifactToken("Work Widgets", CoreArtifactTypes.Folder,
      "AAABGnmjk4IAoJoIa945Kg");
   public static AtsArtifactToken WorkFlowsFolder = new AtsArtifactToken("Work Flows", CoreArtifactTypes.Folder,
      "AAABGnncZ_4AAo+3D0sGfw");

   public AtsArtifactToken(String name, IArtifactType artifactType, String guid) {
      this(name, artifactType, guid, AtsUtil.getAtsBranchToken());
   }

   public AtsArtifactToken(String name, IArtifactType artifactType, String guid, IOseeBranch oseeBranch) {
      super(guid, name, artifactType, oseeBranch);
   }

   public static Artifact get(AtsArtifactToken atsArtifactToken) throws OseeCoreException {
      try {
         return ArtifactQuery.getArtifactFromToken(atsArtifactToken);
      } catch (ArtifactDoesNotExist ex) {
         // do nothing
      }
      return null;
   }
}
