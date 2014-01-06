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

import java.io.IOException;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.framework.jdk.core.type.Identifiable;
import org.eclipse.osee.template.engine.AppendableRule;
import org.eclipse.osee.template.engine.PageCreator;
import org.eclipse.osee.template.engine.PageFactory;
import org.eclipse.osee.template.engine.StringRule;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Angel Avila
 */
public class HtmlWriter {

   private final IResourceRegistry registry;

   public HtmlWriter(IResourceRegistry registry) {
      this.registry = registry;
   }

   public String createDispositionPage(String title, String path, Iterable<? extends Identifiable<String>> dispoEntities) {

      PageCreator page = PageFactory.newPageCreator(registry, "title", title);
      page.addSubstitution(new LinkListRule("disposition", path, false, dispoEntities));
      //      page.addSubstitution(new StringRule("subTitle", ""));
      page.addSubstitution(new StringRule("notes", ""));
      return page.realizePage(TemplateRegistry.DispositionHtml);
   }

   public String createDispoPage(String title, String prefix, String subTitle, final String notesJson) {
      PageCreator page = PageFactory.newPageCreator(registry, "title", title);
      //      page.addSubstitution(new StringRule("disposition", ""));
      String asHtmlLink = String.format("<li><a href=\"%s\">%s</a></li>", prefix, subTitle);
      page.addSubstitution(new StringRule("disposition", asHtmlLink));
      page.addSubstitution(new AppendableRule<String>("notes") {
         @Override
         public void applyTo(Appendable appendable) throws IOException {
            try {
               writeNotes(appendable, notesJson);
            } catch (JSONException ex) {
               throw new IOException(ex);
            }
         }
      });

      return page.realizePage(TemplateRegistry.DispositionHtml);
   }

   private void writeNotes(Appendable sb, String notesJsonString) throws IOException, JSONException {
      JSONArray notesArray = new JSONArray(notesJsonString);
      int count = notesArray.length();

      if (count == 0) {
         sb.append("");
      } else {
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
      }
   }
   private static final class LinkListRule extends AppendableRule<String> {
      private final boolean ordered;
      private final Iterable<? extends Identifiable<String>> indentities;
      private final String path;

      public LinkListRule(String ruleName, String path, boolean ordered, Iterable<? extends Identifiable<String>> indentities) {
         super(ruleName);
         this.ordered = ordered;
         this.indentities = indentities;
         this.path = path;
      }

      @Override
      public void applyTo(Appendable appendable) throws IOException {
         appendable.append(ordered ? "<ol>" : "<ul>");
         for (Identifiable<String> indentity : indentities) {
            appendable.append("<li>");
            appendable.append("<a href=\"");
            appendable.append(path);
            appendable.append(indentity.getGuid());
            appendable.append("\">");
            appendable.append(indentity.getName());
            appendable.append("</a>");
            appendable.append("</li>");
         }
         appendable.append(ordered ? "</ol>" : "</ul>");
      }
   }

}
