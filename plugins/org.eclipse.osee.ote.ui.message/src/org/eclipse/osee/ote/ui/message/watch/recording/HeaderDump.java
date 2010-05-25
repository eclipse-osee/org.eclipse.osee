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

/**
 * @author Andrew M. Finkbeiner
 */
public class HeaderDump extends DetailsWrapper {

   public HeaderDump(MessageRecordDetails details) {
      super(details);
   }

   public HeaderDump(MessageNode messageNode) {
      super(messageNode);
   }

   @Override
   public String toString() {
      return "Header Hex Data";
   }
}
