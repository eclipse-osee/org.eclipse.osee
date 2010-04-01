/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.messaging.event.res.event;

import org.eclipse.osee.framework.jdk.core.util.AXml;

/**
 * @author Donald G. Dunne
 */
public class NetworkBranchRenameEvent extends NetworkBranchEvent {
   private String branchName;
   private String shortName;

   public NetworkBranchRenameEvent(String branchGuid, String branchName, String shortName, NetworkSender networkSender) {
      super(BranchEventModificationType.Renamed, branchGuid, networkSender);
      this.branchName = branchName;
      this.shortName = shortName;
   }

   public NetworkBranchRenameEvent(String xml) {
      super(xml);
   }

   public String getBranchName() {
      return branchName;
   }

   public void setBranchName(String branchName) {
      this.branchName = branchName;
   }

   public String getShortName() {
      return shortName;
   }

   public void setShortName(String shortName) {
      this.shortName = shortName;
   }

   @Override
   public void fromXml(String xml) {
      super.fromXml(xml);
      this.branchName = AXml.getTagData(xml, "branchName");
      this.shortName = AXml.getTagData(xml, "shortName");
   }

   @Override
   public void toXml(StringBuffer sb) {
      super.toXml(sb);
      sb.append(AXml.addTagData("branchName", branchName));
      sb.append(AXml.addTagData("shortName", shortName));
   }

}
