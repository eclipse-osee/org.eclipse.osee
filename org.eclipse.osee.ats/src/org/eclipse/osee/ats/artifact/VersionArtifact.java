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
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BasicArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.factory.IArtifactFactory;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;

public class VersionArtifact extends BasicArtifact {

   public static String ARTIFACT_NAME = "Version";

   public static enum VersionReleaseType {
      Released, UnReleased, Both
   };

   public VersionArtifact(String guid) throws SQLException {
      this(null, guid, null, null);
   }

   public VersionArtifact(IArtifactFactory parentFactory, Branch tagId) throws SQLException {
      this(parentFactory, null, null, tagId);
   }

   public VersionArtifact(IArtifactFactory parentFactory, String guid, String humandReadableId, Branch tagId) throws SQLException {
      super(parentFactory, guid, humandReadableId, tagId);
   }

   public TeamDefinitionArtifact getParentTeamDefinition() throws SQLException {
      return getArtifacts(RelationSide.TeamDefinitionToVersion_TeamDefinition, TeamDefinitionArtifact.class).iterator().next();
   }

   public Boolean isReleased() throws IllegalStateException, SQLException {
      return getSoleBooleanAttributeValue(ATSAttributes.RELEASED_ATTRIBUTE.getStoreName());
   }

   public Boolean isNextVersion() throws IllegalStateException, SQLException {
      return getSoleBooleanAttributeValue(ATSAttributes.NEXT_VERSION_ATTRIBUTE.getStoreName());
   }

   public String toString() {
      return getDescriptiveName();
   }

   public void setReleased(boolean released) throws IllegalStateException, SQLException {
      setSoleBooleanAttributeValue(ATSAttributes.RELEASED_ATTRIBUTE.getStoreName(), released);
   }

   public void setNextVersion(boolean nextVersion) throws IllegalStateException, SQLException {
      setSoleBooleanAttributeValue(ATSAttributes.NEXT_VERSION_ATTRIBUTE.getStoreName(), nextVersion);
   }

   public String getFullName() {
      return getSoleAttributeValue(ATSAttributes.FULL_NAME_ATTRIBUTE.getStoreName());
   }

   public void setFullName(String name) throws IllegalStateException, SQLException {
      setSoleAttributeValue(ATSAttributes.FULL_NAME_ATTRIBUTE.getStoreName(), name);
   }

   public String getDescription() {
      return getSoleAttributeValue(ATSAttributes.DESCRIPTION_ATTRIBUTE.getStoreName());
   }

   public void setDescription(String desc) throws IllegalStateException, SQLException {
      setSoleAttributeValue(ATSAttributes.DESCRIPTION_ATTRIBUTE.getStoreName(), desc);
   }

   public Collection<TeamWorkFlowArtifact> getTargetedForTeamArtifacts() throws SQLException {
      return getArtifacts(RelationSide.TeamWorkflowTargetedForVersion_Workflow, TeamWorkFlowArtifact.class);
   }

   public String getFullDisplayName() {
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
      return (TeamDefinitionArtifact) getFirstArtifact(RelationSide.TeamDefinitionToVersion_TeamDefinition);
   }

   public Date getEstimatedReleaseDate() throws IllegalStateException, SQLException {
      return getSoleDateAttributeValue(ATSAttributes.ESTIMATED_RELEASE_DATE_ATTRIBUTE.getStoreName());
   }

   public Date getReleaseDate() throws IllegalStateException, SQLException {
      return getSoleDateAttributeValue(ATSAttributes.RELEASE_DATE_ATTRIBUTE.getStoreName());
   }

}
