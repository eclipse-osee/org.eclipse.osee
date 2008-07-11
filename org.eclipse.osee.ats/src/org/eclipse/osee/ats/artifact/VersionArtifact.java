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
package org.eclipse.osee.ats.artifact;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.config.AtsCache;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.BasicArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.MultipleAttributesExist;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;

public class VersionArtifact extends BasicArtifact {

   public static String ARTIFACT_NAME = "Version";

   public static enum VersionReleaseType {
      Released, UnReleased, Both
   };

   public VersionArtifact(ArtifactFactory parentFactory, String guid, String humandReadableId, Branch branch, ArtifactType artifactType) {
      super(parentFactory, guid, humandReadableId, branch, artifactType);
   }

   public TeamDefinitionArtifact getParentTeamDefinition() throws SQLException {
      return getArtifacts(AtsRelation.TeamDefinitionToVersion_TeamDefinition, TeamDefinitionArtifact.class).iterator().next();
   }

   public Boolean isReleased() throws IllegalStateException, SQLException, MultipleAttributesExist {
      return getSoleAttributeValue(ATSAttributes.RELEASED_ATTRIBUTE.getStoreName(), false);
   }

   public Boolean isNextVersion() throws IllegalStateException, SQLException, MultipleAttributesExist {
      return getSoleAttributeValue(ATSAttributes.NEXT_VERSION_ATTRIBUTE.getStoreName(), false);
   }

   public String toString() {
      return getDescriptiveName();
   }

   public void setReleased(boolean released) throws SQLException, MultipleAttributesExist {
      setSoleAttributeValue(ATSAttributes.RELEASED_ATTRIBUTE.getStoreName(), released);
   }

   public void setNextVersion(boolean nextVersion) throws SQLException, MultipleAttributesExist {
      setSoleAttributeValue(ATSAttributes.NEXT_VERSION_ATTRIBUTE.getStoreName(), nextVersion);
   }

   public String getFullName() throws SQLException, MultipleAttributesExist {
      return getSoleAttributeValue(ATSAttributes.FULL_NAME_ATTRIBUTE.getStoreName(), "");
   }

   public void setFullName(String name) throws SQLException, MultipleAttributesExist {
      setSoleAttributeValue(ATSAttributes.FULL_NAME_ATTRIBUTE.getStoreName(), name);
   }

   public String getDescription() throws SQLException, MultipleAttributesExist {
      return getSoleAttributeValue(ATSAttributes.DESCRIPTION_ATTRIBUTE.getStoreName(), "");
   }

   public void setDescription(String desc) throws SQLException, MultipleAttributesExist {
      setSoleAttributeValue(ATSAttributes.DESCRIPTION_ATTRIBUTE.getStoreName(), desc);
   }

   public Collection<TeamWorkFlowArtifact> getTargetedForTeamArtifacts() throws SQLException {
      return getArtifacts(AtsRelation.TeamWorkflowTargetedForVersion_Workflow, TeamWorkFlowArtifact.class);
   }

   public String getFullDisplayName() throws SQLException, MultipleAttributesExist {
      String str = "";
      if (!getDescriptiveName().equals(Artifact.UNNAMED)) str += getDescriptiveName();
      if (!getFullName().equals("")) {
         if (str.equals(""))
            str = getFullName();
         else
            str += " - " + getFullName();
      }
      if (!getDescription().equals("")) {
         if (str.equals(""))
            str = getDescription();
         else
            str += " - " + getDescription();
      }
      return str;
   }

   public TeamDefinitionArtifact getTeamDefinitionArtifact() throws SQLException {
      try {
         return (TeamDefinitionArtifact) getRelatedArtifact(AtsRelation.TeamDefinitionToVersion_TeamDefinition);
      } catch (ArtifactDoesNotExist ex) {
         return null;
      } catch (Exception ex) {
         throw new SQLException(ex);
      }
   }

   public Date getEstimatedReleaseDate() throws IllegalStateException, SQLException, MultipleAttributesExist {
      return getSoleAttributeValue(ATSAttributes.ESTIMATED_RELEASE_DATE_ATTRIBUTE.getStoreName(), null);
   }

   public Date getReleaseDate() throws IllegalStateException, SQLException, MultipleAttributesExist {
      return getSoleAttributeValue(ATSAttributes.RELEASE_DATE_ATTRIBUTE.getStoreName(), null);
   }

   public static Set<VersionArtifact> getVersions(Collection<String> teamDefNames) throws OseeCoreException, SQLException {
      Set<VersionArtifact> teamDefs = new HashSet<VersionArtifact>();
      for (String teamDefName : teamDefNames) {
         teamDefs.add(getSoleVersion(teamDefName));
      }
      return teamDefs;
   }

   /**
    * Refrain from using this method as Version Artifact names can be changed by the user.
    * 
    * @param name
    * @return
    * @throws SQLException
    */
   public static VersionArtifact getSoleVersion(String name) throws OseeCoreException, SQLException {
      return AtsCache.getArtifactsByName(name, VersionArtifact.class).iterator().next();
   }
}
