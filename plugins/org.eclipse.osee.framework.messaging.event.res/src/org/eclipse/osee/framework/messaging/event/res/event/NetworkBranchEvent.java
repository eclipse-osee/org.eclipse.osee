/*
 * Created on Mar 31, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.event.res.event;

import org.eclipse.osee.framework.jdk.core.util.AXml;

/**
 * @author Donald G. Dunne
 */
public class NetworkBranchEvent extends NetworkBranchEventBase {

   private BranchEventModificationType branchEventModificationType;

   public NetworkBranchEvent(BranchEventModificationType branchEventModificationType, String branchGuid, NetworkSender networkSender) {
      super(branchGuid, branchEventModificationType.getGuid(), networkSender);
      this.branchEventModificationType = branchEventModificationType;
   }

   public NetworkBranchEvent(String xml) {
      super(xml);
   }

   public BranchEventModificationType getModType() {
      return branchEventModificationType;
   }

   @Override
   public void fromXml(String xml) {
      super.fromXml(xml);
      this.branchEventModificationType = BranchEventModificationType.getType(AXml.getTagData(xml, "modTypeGuid"));
   }

   @Override
   public void toXml(StringBuffer sb) {
      super.toXml(sb);
      sb.append(AXml.addTagData("modTypeGuid", branchEventModificationType.getGuid()));
   }

}
