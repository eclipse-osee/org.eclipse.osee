/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.util.widgets;

/**
 * This is an example of how to implement and use the XAttachmentCombo.
 *
 * @author Donald G. Dunne
 */
public class XPeerChecklistAttachmentExampleCombo extends XAttachmentCombo {

   private static final String LABEL = "Select Peer Review Checklist to Attach";
   public static final Object WIDGET_ID = XPeerChecklistAttachmentExampleCombo.class.getSimpleName();

   public XPeerChecklistAttachmentExampleCombo() {
      super(LABEL);
   }

   @Override
   protected String getFileList() {
      StringBuilder sb = new StringBuilder();
      sb.append("Checklist 1;P:/UserData/checklist_1.xls");
      sb.append("Checklist 2;P:/UserData/checklist_1.xls");
      sb.append("Checklist 3;P:/UserData/checklist_1.xls");
      // Load and return file list
      return sb.toString();
   }

}
