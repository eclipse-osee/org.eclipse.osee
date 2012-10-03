package org.eclipse.osee.coverage.internal.vcast.model;

import java.util.Collection;
import org.eclipse.osee.coverage.internal.vcast.VCastDataStore;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

public class VCastBranchDataTable implements VCastTableData<VCastBranchData> {

   @Override
   public String getName() {
      return "branch_data";
   }

   @Override
   public String[] getColumns() {
      return new String[] {"id", "branch_id", "result_id", "result_line", "taken"};
   }

   @Override
   public Collection<VCastBranchData> getRows(VCastDataStore dataStore) throws OseeCoreException {
      return dataStore.getAllBranchData();
   }

   @Override
   public Object[] toRow(VCastBranchData data) {
      int id = data.getId();
      Integer branchId = data.getBranchId();
      Integer resultId = data.getResultId();
      Integer resultLine = data.getResultLine();
      Boolean taken = data.getTaken();
      return new Object[] {id, branchId, resultId, resultLine, taken};
   }
}