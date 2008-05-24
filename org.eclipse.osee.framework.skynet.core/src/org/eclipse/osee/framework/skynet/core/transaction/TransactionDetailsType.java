/*
 * Created on Apr 30, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.transaction;

/**
 * @author Jeff C. Phillips
 */
public enum TransactionDetailsType {
   NonBaselined(0), Baselined(1);

   private int id;

   private TransactionDetailsType(int id) {
      this.id = id;
   }

   public int getId() {
      return id;
   }
}
