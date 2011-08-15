/*
 * Created on Aug 11, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.replace;

import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;

public class HandleAttributeOrderData {
   private final RelationLink link;
   private final Artifact artifact;
   private final TransactionRecord baselineTransactionRecord;

   public HandleAttributeOrderData(RelationLink link, Artifact artifact, TransactionRecord baselineTransactionRecord) {
      super();
      this.link = link;
      this.artifact = artifact;
      this.baselineTransactionRecord = baselineTransactionRecord;
   }

   public RelationLink getLink() {
      return link;
   }

   public Artifact getArtifact() {
      return artifact;
   }

   public TransactionRecord getBaselineTransactionRecord() {
      return baselineTransactionRecord;
   }
}
