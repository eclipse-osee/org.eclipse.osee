/*
 * Created on Jun 16, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.conflict;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.revision.RevisionManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;

/**
 * @author Donald G. Dunne
 */
public class ConflictManager {

   Branch toBranch;
   Branch fromBranch;
   List<Conflict> originalConflicts;

   public ConflictManager(Branch toBranch, Branch fromBranch) {
      this.toBranch = toBranch;
      this.fromBranch = fromBranch;
   }

   public List<Conflict> getOriginalConflicts() throws OseeCoreException, SQLException {
      if (originalConflicts == null) {
         originalConflicts =
               RevisionManager.getInstance().getConflictsPerBranch(fromBranch, toBranch,
                     TransactionIdManager.getInstance().getStartEndPoint(fromBranch).getKey());
      }
      return originalConflicts;
   }

   public boolean originalConflictsExist() throws OseeCoreException, SQLException {
      return getOriginalConflicts().size() > 0;
   }

   public List<Conflict> getRemainingConflicts() throws OseeCoreException, SQLException {
      List<Conflict> remainingConflicts = new ArrayList<Conflict>();
      if (originalConflictsExist()) {
         for (Conflict conflict : getOriginalConflicts()) {
            if (!conflict.statusResolved() && !conflict.statusCommited() && !conflict.statusInformational()) {
               remainingConflicts.add(conflict);
            }
         }
      }
      return remainingConflicts;
   }

   public boolean remainingConflictsExist() throws OseeCoreException, SQLException {
      return getRemainingConflicts().size() > 0;
   }

   public Branch getToBranch() {
      return toBranch;
   }

   public Branch getFromBranch() {
      return fromBranch;
   }
}
