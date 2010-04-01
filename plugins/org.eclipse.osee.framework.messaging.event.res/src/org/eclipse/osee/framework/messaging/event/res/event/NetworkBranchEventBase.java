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
public abstract class NetworkBranchEventBase extends FrameworkEventBase {

   private String branchGuid;

   public NetworkBranchEventBase(String branchGuid, String eventGuid, NetworkSender networkSender) {
      super(networkSender, eventGuid);
      this.branchGuid = branchGuid;
   }

   public NetworkBranchEventBase(String xml) {
      super(xml);
   }

   @Override
   public void fromXml(String xml) {
      super.fromXml(xml);
      this.branchGuid = AXml.getTagData(xml, "branchGuid");
   }

   @Override
   public void toXml(StringBuffer sb) {
      super.toXml(sb);
      sb.append(AXml.addTagData("branchGuid", branchGuid));
   }

   public String getBranchGuid() {
      return branchGuid;
   }

}
