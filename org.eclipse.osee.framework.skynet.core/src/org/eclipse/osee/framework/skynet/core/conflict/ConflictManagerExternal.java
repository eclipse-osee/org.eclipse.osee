/*
 * Created on Jun 16, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.conflict;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.revision.ConflictManagerInternal;
import org.eclipse.osee.framework.skynet.core.status.EmptyMonitor;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;

/**
 * @author Donald G. Dunne
 */
public class ConflictManagerExternal {

   Branch toBranch;
   Branch fromBranch;
   List<Conflict> originalConflicts;

   public ConflictManagerExternal(Branch toBranch, Branch fromBranch) {
      this.toBranch = toBranch;
      this.fromBranch = fromBranch;
   }

   public List<Conflict> getOriginalConflicts() throws OseeCoreException {
      if (originalConflicts == null) {
         originalConflicts =
               ConflictManagerInternal.getConflictsPerBranch(fromBranch, toBranch,
                     TransactionIdManager.getStartEndPoint(fromBranch).getKey(), new EmptyMonitor());
      }
      return originalConflicts;
   }

   public boolean originalConflictsExist() throws OseeCoreException {
      return getOriginalConflicts().size() > 0;
   }

   public List<Conflict> getRemainingConflicts() throws OseeCoreException {
      List<Conflict> remainingConflicts = new ArrayList<Conflict>();
      if (originalConflictsExist()) {
         for (Conflict conflict : getOriginalConflicts()) {
            if (!conflict.statusResolved() && !conflict.statusCommitted() && !conflict.statusInformational()) {
               remainingConflicts.add(conflict);
            }
         }
      }
      return remainingConflicts;
   }

   public boolean remainingConflictsExist() throws OseeCoreException {
      return getRemainingConflicts().size() > 0;
   }

   public Branch getToBranch() {
      return toBranch;
   }

   public Branch getFromBranch() {
      return fromBranch;
   }
}
