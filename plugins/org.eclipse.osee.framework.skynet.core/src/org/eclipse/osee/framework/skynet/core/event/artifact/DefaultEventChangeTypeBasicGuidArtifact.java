/*
 * Created on Mar 25, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.event.artifact;

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
      return String.format("[%s - %s from type %s to %s]", EventModType.ChangeType.name(), getGuid(), fromArtTypeGuid,
            getArtTypeGuid());
   }

}
