/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.workflow;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bhawana Mishra
 */
public class AtsAttachments {

   List<AtsAttachment> attachments = new ArrayList<AtsAttachment>();

   public List<AtsAttachment> getAttachments() {
      return attachments;
   }

   public void setAttachments(List<AtsAttachment> attachments) {
      this.attachments = attachments;
   }

   public void addAttachment(AtsAttachment attachment) {
      this.attachments.add(attachment);
   }

   @Override
   public String toString() {
      return "Attachments=" + attachments;
   }

}
