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

import java.util.List;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osee.ote.message.commands.RecordCommand.MessageRecordDetails;
import org.eclipse.osee.ote.ui.message.tree.WatchList;

/**
 * @author Andrew M. Finkbeiner
 */
public class RecordingWizard extends Wizard {

   private final RecordingFilter filter;
   private String fileName;
   private final RecordingFilePage fileCreation;
   private List<MessageRecordDetails> filteredList;

   public RecordingWizard(WatchList watchList) {
      setWindowTitle("Recording Options");
      fileCreation = new RecordingFilePage();
      filter = new RecordingFilter(watchList);
      addPage(fileCreation);
      addPage(filter);
   }

   @Override
   public boolean performFinish() {
      filteredList = filter.getData();
      fileName = fileCreation.getFileName();
      return super.canFinish();
   }

   public List<MessageRecordDetails> getFilteredMessageRecordDetails() {
      return filteredList;
   }

   public String getFileName() {
      return fileName;
   }

}
