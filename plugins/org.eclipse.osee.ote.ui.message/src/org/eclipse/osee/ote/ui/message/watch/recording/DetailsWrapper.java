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

import org.eclipse.osee.ote.message.commands.RecordCommand.MessageRecordDetails;
import org.eclipse.osee.ote.ui.message.tree.MessageNode;
import org.eclipse.osee.ote.ui.message.watch.ElementPath;

/**
 * @author Andrew M. Finkbeiner
 */
public class DetailsWrapper implements IElementPath {

   private MessageRecordDetails details;
   private ElementPath elementPath;
   private MessageNode messageNode;

   public DetailsWrapper(MessageRecordDetails details, ElementPath elementPath) {
      this.details = details;
      this.elementPath = elementPath;
   }

   public DetailsWrapper(MessageRecordDetails details) {
      this.details = details;

   }

   public DetailsWrapper(MessageNode messageNode) {
      this.messageNode = messageNode;
      elementPath = new ElementPath(messageNode.getMessageClassName());
   }

   public MessageRecordDetails getDetails() {
      return details;
   }

   @Override
   public ElementPath getElementPath() {
      return elementPath;
   }

   public MessageNode getMessageNode() {
      return messageNode;
   }
}
