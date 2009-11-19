/*
 * Created on Nov 18, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.test.util;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.IBasicArtifact;
import org.eclipse.osee.framework.core.data.TransactionRecord;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

public class BranchTestUtil extends Branch {

   private BranchState state;
   private BranchType type;
   private boolean isArchived;
   private BranchArchivedState archivedState;

   protected BranchTestUtil(String guid, String name) {
      super(guid, name);
   }

   public BranchTestUtil(String guid, String name, BranchState state, BranchType type, BranchArchivedState archivedState, boolean isArchived) {
      super(guid, name);
      this.state = state;
      this.type = type;
      this.isArchived = isArchived;
      this.archivedState = archivedState;
   }

   @Override
   public int compareTo(Branch other) {
      return 0;
   }

   @Override
   public Branch getAccessControlBranch() {
      return null;
   }

   @Override
   public Object getAdapter(Class adapter) {
      return null;
   }

   @Override
   public Collection<String> getAliases() throws OseeCoreException {
      return null;
   }

   @Override
   public Collection<Branch> getAncestors() throws OseeCoreException {
      return null;
   }

   @Override
   public BranchArchivedState getArchiveState() {
      return archivedState;
   }

   @Override
   public IBasicArtifact<?> getAssociatedArtifact() throws OseeCoreException {
      return null;
   }

   @Override
   public TransactionRecord getBaseTransaction() throws OseeCoreException {
      return null;
   }

   @Override
   public BranchState getBranchState() {
      return this.state;
   }

   @Override
   public BranchType getBranchType() {
      return this.type;
   }

   @Override
   public Collection<Branch> getChildBranches() throws OseeCoreException {
      return null;
   }

   @Override
   public Collection<Branch> getChildBranches(boolean recurse) throws OseeCoreException {
      return null;
   }

   @Override
   public Collection<Branch> getChildren() throws OseeCoreException {
      return null;
   }

   @Override
   public Branch getParentBranch() throws OseeCoreException {
      return null;
   }

   @Override
   public String getShortName() {
      return null;
   }

   @Override
   public TransactionRecord getSourceTransaction() throws OseeCoreException {
      return null;
   }

   @Override
   public Collection<Branch> getWorkingBranches() throws OseeCoreException {
      return null;
   }

   @Override
   public boolean hasParentBranch() throws OseeCoreException {
      return false;
   }

   @Override
   public boolean isEditable() {
      return false;
   }

   @Override
   public void setAliases(String... alias) throws OseeCoreException {
   }

   @Override
   public void setArchived(boolean isArchived) {
      this.isArchived = isArchived;
   }

   @Override
   public void setAssociatedArtifact(IBasicArtifact<?> artifact) throws OseeCoreException {
   }

   @Override
   public void setBranchState(BranchState branchState) {
      this.state = branchState;
   }

   @Override
   public void setBranchType(BranchType branchType) {
      this.type = branchType;
   }

}
