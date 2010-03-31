/*
 * Created on Mar 24, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.event.artifact;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.core.data.DefaultBasicGuidArtifact;
import org.eclipse.osee.framework.core.data.IBasicGuidArtifact;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class DefaultEventBasicGuidArtifact extends DefaultBasicGuidArtifact implements IEventBasicGuidArtifact {

   private final EventModType eventModType;

   public DefaultEventBasicGuidArtifact(EventModType eventModType, String branchGuid, String artTypeGuid, String guid) {
      super(branchGuid, artTypeGuid, guid);
      this.eventModType = eventModType;
   }

   @Override
   public EventModType getModType() {
      return eventModType;
   }

   public DefaultEventBasicGuidArtifact(EventModType eventModType, Artifact artifact) throws OseeCoreException {
      this(eventModType, artifact.getBranch().getGuid(), artifact.getArtifactType().getGuid(), artifact.getGuid());
   }

   public DefaultEventBasicGuidArtifact(EventModType eventModType, IBasicGuidArtifact basicGuidArtifact) throws OseeCoreException {
      this(eventModType, basicGuidArtifact.getBranchGuid(), basicGuidArtifact.getArtTypeGuid(),
            basicGuidArtifact.getGuid());
   }

   public static Set<IEventBasicGuidArtifact> get(EventModType eventModType, Collection<IBasicGuidArtifact> basicGuidArtifacts) throws OseeCoreException {
      if (eventModType == EventModType.ChangeType) throw new OseeArgumentException("Can't be used for ChangeType");
      Set<IEventBasicGuidArtifact> eventArts = new HashSet<IEventBasicGuidArtifact>();
      for (IBasicGuidArtifact guidArt : basicGuidArtifacts) {
         eventArts.add(new DefaultEventBasicGuidArtifact(eventModType, guidArt));
      }
      return eventArts;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((eventModType == null) ? 0 : eventModType.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (!super.equals(obj)) return false;
      DefaultEventBasicGuidArtifact other = (DefaultEventBasicGuidArtifact) obj;
      if (eventModType == null) {
         if (other.eventModType != null) return false;
      } else if (!eventModType.equals(other.eventModType)) return false;
      return true;
   }

   public String toString() {
      return String.format("[%s - %s]", eventModType, getGuid());
   }

}
