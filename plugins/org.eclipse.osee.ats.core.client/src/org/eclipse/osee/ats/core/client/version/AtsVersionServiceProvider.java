/*
 * Created on Jul 16, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.client.version;

import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.version.IAtsVersionService;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.client.internal.Activator;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.config.AtsVersionService;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;

public class AtsVersionServiceProvider implements IAtsVersionService {

   private IAtsVersion getTargetedVersionByTeamWf(IAtsTeamWorkflow teamWf) {
      IAtsVersion version = AtsVersionCache.instance.getVersion(teamWf);
      if (version == null) {
         try {
            version = AtsVersionStore.getTargetedVersion(teamWf);
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
            version = NullVersion.instance;
         }
         AtsVersionCache.instance.cache(teamWf, version);
      }
      return version;
   }

   @Override
   public IAtsVersion getTargetedVersion(Object object) {
      IAtsVersion version = NullVersion.instance;
      if (version == null) {
         if (object instanceof AbstractWorkflowArtifact) {
            try {
               TeamWorkFlowArtifact teamArt = ((AbstractWorkflowArtifact) object).getParentTeamWorkflow();
               if (teamArt != null) {
                  version = getTargetedVersionByTeamWf(teamArt);
               }
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      }
      return version;
   }

   @Override
   public IAtsVersion setTargetedVersion(IAtsTeamWorkflow teamWf, IAtsVersion version) {
      return AtsVersionCache.instance.cache(teamWf, version);
   }

   @Override
   public Collection<IAtsVersion> getVersions(IAtsTeamDefinition teamDef) {
      return null;
   }

   @Override
   public void addVersion(IAtsTeamDefinition teamDef, IAtsVersion version) {
   }

   @Override
   public IAtsTeamDefinition getTeamDefinition(IAtsVersion version) {
      return null;
   }

   @Override
   public void setTeamDefinition(IAtsVersion version, IAtsTeamDefinition teamDef) {
   }

   /**
    * @return true if this is a TeamWorkflow and the version it's been targeted for has been released
    */
   @Override
   public boolean isReleased(IAtsTeamWorkflow teamWf) {
      try {
         IAtsVersion verArt = AtsVersionService.get().getTargetedVersion(teamWf);
         if (verArt != null) {
            return verArt.isReleased();
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return false;
   }

   @Override
   public boolean isVersionLocked(IAtsTeamWorkflow teamWf) {
      try {
         IAtsVersion verArt = AtsVersionService.get().getTargetedVersion(teamWf);
         if (verArt != null) {
            return verArt.isVersionLocked();
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return false;
   }

   @Override
   public boolean hasTargetedVersion(Object object) {
      return getTargetedVersion(object) != null && !getTargetedVersion(object).equals(NullVersion.instance);
   }

}
