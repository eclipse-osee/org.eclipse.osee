/*
 * Created on Feb 17, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.commit.actions;

import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.WordAttribute;
import org.eclipse.osee.framework.skynet.core.change.AttributeChanged;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.revision.ChangeManager;
import org.eclipse.osee.framework.skynet.core.status.EmptyMonitor;

/**
 * @author Theron Virgin
 */
public class CatchTrackedChanges implements CommitAction {

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.commit.actions.CommitAction#runCommitAction(org.eclipse.osee.framework.skynet.core.artifact.Branch)
    */
   /**
    * Check that none of the artifacts that will be commited contain tracked changes Use the change report to get
    * attributeChanges and check their content for trackedChanges
    */
   
   @Override
   public void runCommitAction(Branch branch) throws OseeCoreException {
      for (Change change : ChangeManager.getChangesPerBranch(branch, new EmptyMonitor())) {
         if (!change.getModificationType().equals(ModificationType.DELETED)) {
            if (change instanceof AttributeChanged) {
               Attribute<?> attribute = ((AttributeChanged) change).getAttribute();
               if (attribute instanceof WordAttribute) {
                  if (((WordAttribute) attribute).containsWordAnnotations()) {
                     throw new OseeCoreException(
                           String.format(
                                 "Commit Branch Failed Artifact \"%s\" Art_ID \"%d\" contains Tracked Changes. Accept all revision changes and then recommit",
                                 attribute.getArtifact().getSafeName(), attribute.getArtifact().getArtId()));
                  }
               }
            }
         }
      }
   }

}
