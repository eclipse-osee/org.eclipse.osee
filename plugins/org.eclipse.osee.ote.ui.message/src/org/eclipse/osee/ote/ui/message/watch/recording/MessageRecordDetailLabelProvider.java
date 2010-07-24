/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.ui.message.watch.recording;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osee.ote.message.commands.RecordCommand.MessageRecordDetails;
import org.eclipse.osee.ote.ui.message.tree.HeaderElementNode;
import org.eclipse.osee.ote.ui.message.tree.MessageNode;
import org.eclipse.osee.ote.ui.message.tree.WatchedElementNode;
import org.eclipse.swt.graphics.Image;

/**
 * @author Andrew M. Finkbeiner
 */
public class MessageRecordDetailLabelProvider extends LabelProvider {

   @Override
   public Image getImage(Object element) {
      return null;
   }

   @Override
   public String getText(Object element) {
      if (element instanceof MessageRecordDetails) {
         MessageRecordDetails details = (MessageRecordDetails) element;
         return details.getName();
      } else if (element instanceof BodyElements || element instanceof HeaderElements || element instanceof BodyDump || element instanceof HeaderDump) {
         return element.toString();
      } else if (element instanceof DetailsWrapper) {
         DetailsWrapper details = (DetailsWrapper) element;
         return details.getElementPath().asString();
      } else if (element instanceof MessageNode) {
         return ((MessageNode) element).getMessageClassName();
      } else if (element instanceof WatchedElementNode) {
         WatchedElementNode node = (WatchedElementNode) element;
         return String.format("%s: byte=%d, msb=%d, lsb=%d", node.getElementName(), node.getByteOffset(),
            node.getMsb(), node.getLsb());
      } else if (element instanceof HeaderElementNode) {
         HeaderElementNode node = (HeaderElementNode) element;
         return String.format("%s: byte=%d, msb=%d, lsb=%d", node.getElementName(), node.getByteOffset(),
            node.getMsb(), node.getLsb());
      }
      return element.toString();
   }
}