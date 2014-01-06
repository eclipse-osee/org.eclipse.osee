/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.disposition.rest.util;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import org.eclipse.osee.disposition.model.Note;
import org.eclipse.osee.framework.jdk.core.type.Identifiable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author Angel Avila
 */
public class HtmlWriterTest {

   private final static String PAGE_FORMAT = "<!-- pageDeclaration.html -->\n" + //
   "<!DOCTYPE HTML>\n" + //
   "<html>\n" + //   
   "<head>\n" + //
   "       <title>%s</title>\n" + //
   "</head>\n" + //
   "<body>\n" + //
   "%s" + //
   "</body>\n" + //
   "</html>";

   private HtmlWriter writer;

   //@formatter:off
   @Mock private Identifiable<String> id1;
   @Mock private Identifiable<String> id2;
   //@formatter:on

   @Before
   public void setUp() {
      MockitoAnnotations.initMocks(this);
      writer = new HtmlWriter(TemplateRegistry.newRegistry());

      when(id1.getGuid()).thenReturn("abcdef");
      when(id1.getName()).thenReturn("Id 1");

      when(id2.getGuid()).thenReturn("12345");
      when(id2.getName()).thenReturn("Id 2");
   }

   @Test
   public void testDispoPage() throws Exception {
      String expected = htmlPage("Hello", "<center>\n" + //
      "       <h2>Hello</h2>\n" + //
      "</center>\n" + //
      "\t<p align=\"right\"></p>\n" + //
      "\t<ul><li><a href=\"abcdef\">Id 1</a></li><li><a href=\"12345\">Id 2</a></li></ul>\n");

      @SuppressWarnings("unchecked")
      String actual = writer.createDispositionPage("Hello", "", Arrays.asList(id1, id2));
      assertEquals(expected, actual);
   }

   @Test
   public void testDispoPageWithPrefix() throws Exception {
      String expected = htmlPage("HELLO2", "<center>\n" + //
      "       <h2>HELLO2</h2>\n" + //
      "</center>\n" + //
      "\t<p align=\"right\"></p>\n" + //
      "\t<li><a href=\"prefix\">subTitle</a></li>\n");

      String actual = writer.createDispoPage("HELLO2", "prefix", "subTitle", "[]");
      assertEquals(expected, actual);
   }

   @Test
   public void testDispoPageWithPrefixAndNotes() throws Exception {
      Note noteOne = new Note();
      noteOne.setContent("Hola");
      noteOne.setDateString("dateString");
      noteOne.setType("Dev");

      Note noteTwo = new Note();
      noteTwo.setContent("Hola2");
      noteTwo.setDateString("dateString2");
      noteTwo.setType("Dev2");

      JSONObject noteOneAsJson = new JSONObject(noteOne);
      JSONObject noteTwoAsJson = new JSONObject(noteTwo);
      JSONArray notes = new JSONArray();
      notes.put(noteOneAsJson);
      notes.put(noteTwoAsJson);
      String notesString = getNotesString(notes);

      String expected = htmlPage("HELLO2", "<center>\n" + //
      "       <h2>HELLO2</h2>\n" + //
      "</center>\n" + //
      "\t<p align=\"right\">" + notesString + "</p>\n" + //
      "\t<li><a href=\"prefix\">subTitle</a></li>\n");

      String actual = writer.createDispoPage("HELLO2", "prefix", "subTitle", notes.toString());
      assertEquals(expected, actual);
   }

   private String getNotesString(JSONArray notesArray) throws JSONException {
      StringBuilder sb = new StringBuilder();
      int count = notesArray.length();

      for (int i = 0; i < count; i++) {
         if (i != 0) {
            sb.append("<br><br>---------------------------<br><br>");
         }
         JSONObject note = notesArray.getJSONObject(i);
         if (note.has("content")) {
            sb.append(note.getString("content"));
         }
         if (note.has("type")) {
            sb.append(String.format("<br><b>Type:</b><i>%s</i><br>", note.getString("type")));
         }
         if (note.has("dateString")) {
            sb.append(note.getString("dateString"));
         }
      }
      return sb.toString();
   }

   private static String htmlPage(String title, String body) {
      return String.format(PAGE_FORMAT, title, body);
   }

}
