/*
 * Created on Mar 25, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.event.artifact;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;

/**
 * @author Donald G. Dunne
 */
public class DefaultEventChangeTypeBasicGuidArtifact extends DefaultEventBasicGuidArtifact implements IEventChangeTypeBasicGuidArtifact {

   private final String fromArtTypeGuid;

   public DefaultEventChangeTypeBasicGuidArtifact(String branchGuid, String fromArtTypeGuid, String artTypeGuid, String guid) {
      super(EventModType.ChangeType, branchGuid, artTypeGuid, guid);
      this.fromArtTypeGuid = fromArtTypeGuid;
   }

   public String getFromArtTypeGuid() {
      return fromArtTypeGuid;
   }

   public String toString() {
      try {
         return String.format("[%s - %s from type [%s][%s] to [%s][%s]]", EventModType.ChangeType.name(), getGuid(),
               fromArtTypeGuid, ArtifactTypeManager.getTypeByGuid(fromArtTypeGuid), getArtTypeGuid(),
               ArtifactTypeManager.getTypeByGuid(getArtTypeGuid()));
      } catch (OseeCoreException ex) {
         return String.format("[%s - %s from type [%s] to [%s]]", EventModType.ChangeType.name(), getGuid(),
               fromArtTypeGuid, getArtTypeGuid());
      }
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((fromArtTypeGuid == null) ? 0 : fromArtTypeGuid.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (!super.equals(obj)) return false;
      if (getClass() != obj.getClass()) return false;
      DefaultEventChangeTypeBasicGuidArtifact other = (DefaultEventChangeTypeBasicGuidArtifact) obj;
      if (fromArtTypeGuid == null) {
         if (other.fromArtTypeGuid != null) return false;
      } else if (!fromArtTypeGuid.equals(other.fromArtTypeGuid)) return false;
      return true;
   }

}
