/*
 * Created on Jul 16, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.impl.internal.version;

import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.version.IAtsVersionService;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.impl.internal.AtsVersionStore;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

public class AtsVersionServiceImpl implements IAtsVersionService {

   public IAtsVersion getTargetedVersionByTeamWf(IAtsTeamWorkflow teamWf) throws OseeCoreException {
      IAtsVersion version = AtsVersionCache.instance.getVersion(teamWf);
      if (version == null && AtsVersionStore.getService() != null) {
         version = AtsVersionStore.getService().getTargetedVersion(teamWf);
      }
      AtsVersionCache.instance.cache(teamWf, version);
      return version;
   }

   @Override
   public IAtsVersion getTargetedVersion(Object object) throws OseeCoreException {
      IAtsVersion version = null;
      if (object instanceof IAtsWorkItem) {
         IAtsTeamWorkflow teamArt = ((IAtsWorkItem) object).getParentTeamWorkflow();
         if (teamArt != null) {
            version = getTargetedVersionByTeamWf(teamArt);
         }
      }
      return version;
   }

   @Override
   public void removeTargetedVersion(IAtsTeamWorkflow teamWf) throws OseeCoreException {
      removeTargetedVersion(teamWf, false);
   }

   @Override
   public void removeTargetedVersionAndStore(IAtsTeamWorkflow teamWf) throws OseeCoreException {
      removeTargetedVersion(teamWf, true);
   }

   public void removeTargetedVersion(IAtsTeamWorkflow teamWf, boolean store) throws OseeCoreException {
      if (store) {
         AtsVersionStore.getService().removeTargetedVersionLink(teamWf);
      }
      AtsVersionCache.instance.deCache(teamWf);
   }

   @Override
   public IAtsVersion setTargetedVersion(IAtsTeamWorkflow teamWf, IAtsVersion version) throws OseeCoreException {
      return setTargetedVersion(teamWf, version, false);
   }

   @Override
   public IAtsVersion setTargetedVersionAndStore(IAtsTeamWorkflow teamWf, IAtsVersion version) throws OseeCoreException {
      return setTargetedVersion(teamWf, version, true);
   }

   private IAtsVersion setTargetedVersion(IAtsTeamWorkflow teamWf, IAtsVersion version, boolean store) throws OseeCoreException {
      if (store) {
         if (version == null) {
            AtsVersionStore.getService().setTargetedVersionLink(teamWf, version);
         } else {
            AtsVersionStore.getService().setTargetedVersionLink(teamWf, version);
         }
      }
      if (version == null) {
         AtsVersionCache.instance.deCache(teamWf);
      } else {
         return AtsVersionCache.instance.cache(teamWf, version);
      }
      return null;
   }

   @Override
   public void setTeamDefinition(IAtsVersion version, IAtsTeamDefinition teamDef) throws OseeCoreException {
      AtsVersionStore.getService().setTeamDefinition(version, teamDef);
   }

   /**
    * @return true if this is a TeamWorkflow and the version it's been targeted for has been released
    * @throws OseeCoreException
    */
   @Override
   public boolean isReleased(IAtsTeamWorkflow teamWf) throws OseeCoreException {
      IAtsVersion verArt = getTargetedVersion(teamWf);
      if (verArt != null) {
         return verArt.isReleased();
      }
      return false;
   }

   @Override
   public boolean isVersionLocked(IAtsTeamWorkflow teamWf) throws OseeCoreException {
      IAtsVersion verArt = getTargetedVersion(teamWf);
      if (verArt != null) {
         return verArt.isVersionLocked();
      }
      return false;
   }

   @Override
   public boolean hasTargetedVersion(Object object) throws OseeCoreException {
      return getTargetedVersion(object) != null;
   }

   @Override
   public IAtsTeamDefinition getTeamDefinition(IAtsVersion version) throws OseeCoreException {
      return AtsVersionStore.getService().getTeamDefinition(version);
   }

}
