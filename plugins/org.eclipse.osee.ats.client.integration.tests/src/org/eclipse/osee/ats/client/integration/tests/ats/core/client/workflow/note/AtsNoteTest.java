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
package org.eclipse.osee.ats.client.integration.tests.ats.core.client.workflow.note;

import java.util.Date;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.ats.core.client.workflow.note.AtsNote;
import org.eclipse.osee.ats.core.client.workflow.note.INoteStorageProvider;
import org.eclipse.osee.ats.core.client.workflow.note.NoteItem;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class AtsNoteTest {

   @Test
   public void testToAndFromStore() throws OseeCoreException {
      Date date = new Date();
      SimpleNoteStore store = new SimpleNoteStore();
      AtsNote log = new AtsNote(store);
      NoteItem item = NoteItemTest.getTestNoteItem(date);
      log.addNoteItem(item);

      AtsNote log2 = new AtsNote(store);
      Assert.assertEquals(1, log2.getNoteItems().size());
      NoteItem loadItem = log2.getNoteItems().iterator().next();
      NoteItemTest.validate(loadItem, date);
   }

   public class SimpleNoteStore implements INoteStorageProvider {

      String store = "";

      @Override
      public String getNoteXml() {
         return store;
      }

      @Override
      public IStatus saveNoteXml(String xml) {
         store = xml;
         return Status.OK_STATUS;
      }

      @Override
      public String getNoteTitle() {
         return "This is the title";
      }

      @Override
      public String getNoteId() {
         return GUID.create();
      }

      @Override
      public boolean isNoteable() {
         return false;
      }

   }

}
