/*
 * Created on Oct 3, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.dbHealth;

/**
 * @author Theron Virgin
 */
public class LocalTxData {
   public int dataId;
   public int branchId;
   public int number;

   public LocalTxData(int dataId, int branchId, int number) {
      super();
      this.branchId = branchId;
      this.dataId = dataId;
      this.number = number;
   }
}
