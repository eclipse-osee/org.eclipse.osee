/*
 * Created on Aug 11, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.replace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.skynet.core.change.ArtifactChange;
import org.eclipse.osee.framework.skynet.core.change.AttributeChange;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.change.RelationChange;

/**
 * @author Jeff C. Phillips
 */
public class ChangeCombiner {

   public static Collection<Change> combine(Collection<Change> changes, TransactionRecord baselineTransaction) {
      HashMap<Integer, Change> attributeChanges = new HashMap<Integer, Change>();
      HashMap<Integer, Change> artifactChanges = new HashMap<Integer, Change>();
      HashMap<Integer, Change> relationChanges = new HashMap<Integer, Change>();
      Collection<Change> changesToReturn = new ArrayList<Change>();

      for (Change change : changes) {
         if (change instanceof AttributeChange) {
            attributeChanges = handleChanges(attributeChanges, change, baselineTransaction);
         } else if (change instanceof ArtifactChange) {
            artifactChanges = handleChanges(artifactChanges, change, baselineTransaction);
         } else if (change instanceof RelationChange) {
            relationChanges = handleChanges(relationChanges, change, baselineTransaction);
         }
      }

      changesToReturn.addAll(attributeChanges.values());
      changesToReturn.addAll(artifactChanges.values());
      changesToReturn.addAll(relationChanges.values());

      return changesToReturn;
   }

   private static HashMap<Integer, Change> handleChanges(HashMap<Integer, Change> changes, Change change, TransactionRecord baselineTransaction) {
      Change storedChange = changes.get(change.getItemId());
      if (storedChange != null) {
         if (!storedChange.getTxDelta().getStartTx().equals(baselineTransaction)) {
            changes.put(change.getItemId(), change);
         }
      } else {
         changes.put(change.getItemId(), change);
      }
      return changes;

   }
}
