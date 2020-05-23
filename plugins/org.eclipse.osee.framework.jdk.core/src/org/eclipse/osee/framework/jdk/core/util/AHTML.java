/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.jdk.core.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.List;

/**
 * @author Michael A. Winston
 */
public class AHTML {
   private static final String HTTP_CHARSET_ENCODING =
      "<meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\">";
   private static final String begin = "<table ";
   public final static String LABEL_FONT = "<font color=\"black\" face=\"Arial\" size=\"-1\">";

   public static String getHyperlink(String url, String name) {
      return String.format("<a href=\"%s\">%s</a>", url, name);
   }

   //TODO: replace with ReservedCharacters.java
   public static String textToHtml(String text) {
      if (text == null) {
         return "";
      }
      text = text.replaceAll("&", "&amp;");
      text = text.replaceAll(">", "&gt;");
      text = text.replaceAll("<", "&lt;");
      text = text.replaceAll("\"", "&quot;");
      text = text.replaceAll("\\n", "<br/>");
      text = text.replaceAll("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
      text = text.replaceAll("[\\x0B\\f\\r]+", "");
      return text;
   }

   //TODO: replace with ReservedCharacters.java
   public static String htmlToText(String html) {
      if (html == null) {
         return "";
      }
      html = html.replaceAll("&amp;", "&");
      html = html.replaceAll("&gt;", ">");
      html = html.replaceAll("&lt;", "<");
      html = html.replaceAll("&quot;", "\"");
      html = html.replaceAll("&nbsp;", " ");
      return html;
   }

   /**
    * <p>
    * Remove (X|HT)ML like comments of form:<br/>
    * <code>&lt;!--\\s*.*\\s*--&gt;</code><br/>
    * </p>
    *
    * @param value &lt;tagA&gt;&lt;!-- Comment -->aValue&lt;/tagA&gt;
    * @return &lt;tagA&gt;aValue&lt;/tagA&gt;
    */
   public static String removeComments(String value) {
      return Strings.isValid(value) ? value.replaceAll("<!--\\s*.*\\s*-->", "") : value;
   }

   /**
    *
    */
   public static Exception isUrlValid(String urlStr, InetSocketAddress addr) {
      try {
         URL url = new URL(urlStr);
         URLConnection connection = url.openConnection(new Proxy(Proxy.Type.HTTP, addr));
         connection.setReadTimeout(5000);
         connection.connect();
      } catch (Exception ex) {
         return ex;
      }
      return null;
   }

   public static String getUrlPageHtml(String urlStr, InetSocketAddress addr) {
      StringBuffer buffer = new StringBuffer();
      try {
         URL url = new URL(urlStr);
         URLConnection connection = url.openConnection(new Proxy(Proxy.Type.HTTP, addr));
         BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
         String line = null;
         while ((line = rd.readLine()) != null) {
            buffer.append(line);
         }
         rd.close();
         return buffer.toString();
      } catch (Exception ex) {
         ex.printStackTrace();
         return simplePage("Exception opening url " + ex.getLocalizedMessage());
      }
   }

   public static String titledPage(String title, String text) {
      return simplePage("<head><title>" + title + "</title></head>" + text);
   }

   public static String pageEncoding(String html) {
      return HTTP_CHARSET_ENCODING + html;
   }

   public static String simplePage(String text) {
      return pageEncoding("<html>" + text + "</html>");
   }

   public static String simplePageNoPageEncoding(String text) {
      return "<html>" + text + "</html>";
   }

   public static String getLabelStr(String labelFont, String str) {
      return labelFont + "<b>" + textToHtml(str) + "</b></font>";
   }

   public static String getLabelValueStr(String labelFont, String label, String value) {
      return getLabelStr(labelFont, label) + value;
   }

   public static String getLabelValueStr(String label, String value) {
      return getLabelStr(LABEL_FONT, label + ":") + "&nbsp;&nbsp;" + value;
   }

   public static String color(String color, String str) {
      if (color == null) {
         return str;
      } else {
         return "<font color=\"" + color + "\">" + str + "</font>";
      }
   }

   public static String boldColor(String color, String str) {
      return "<font color=\"" + color + "\"><b>" + textToHtml(str) + "</b></font>";
   }

   public static String bold(String str) {
      return "<b>" + textToHtml(str) + "</b>";
   }

   public static String boldColorTags(String color, String str) {
      return "<font color=\"" + color + "\"><b>" + str + "</b></font>";
   }

   public static String imageBlock(String description, String filename) {
      String filenames[] = new String[1];
      filenames[0] = filename;
      return imageBlock(description, filenames);
   }

   public static String imageBlock(String description, String filenames[]) {
      StringBuilder str = new StringBuilder();
      str.append("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\"><tr><td>");
      if (!description.equals("")) {
         str.append(description);
         str.append(AHTML.newline());
      }
      for (int i = 0; i < filenames.length; i++) {
         str.append("<IMG SRC=\"" + filenames[i] + "\"><br>");
      }
      str.append("</td></tr></table>");
      return str.toString();
   }

   public static String urlBlock(String description, String urls[]) {
      StringBuilder str = new StringBuilder();
      str.append("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
      if (!description.equals("")) {
         str.append(description);
         str.append(AHTML.newline());
      }
      for (int i = 0; i < urls.length; i++) {
         str.append("<A HREF=\"" + urls[i] + "\">" + urls[i] + "</A><br>");
      }
      str.append("</td></tr></table>");
      return str.toString();
   }

   public static String heading(int heading, String str, String id) {
      return "<h" + heading + (Strings.isValid(id) ? " id=\"" + id + "\"" : "") + ">" + textToHtml(
         str) + "</h" + heading + ">";
   }

   public static String heading(int heading, String str) {
      return heading(heading, str, null);
   }

   public static String padSpace(int num, String str) {
      StringBuilder out = new StringBuilder();
      for (int i = 0; i < num; i++) {
         out.append("&nbsp;");
      }
      out.append(str);
      return out.toString();
   }

   public static String addSpace(int num) {
      StringBuilder out = new StringBuilder();
      for (int i = 0; i < num; i++) {
         out.append("&nbsp;");
      }
      return out.toString();
   }

   public static String para(String str) {
      return "<p>" + textToHtml(str) + "</p>";
   }

   public static String italics(String str) {
      return "<i>" + textToHtml(str) + "</i>";
   }

   public static String pre(String str) {
      return "<pre>" + str + "</pre>";
   }

   public static String blockQuote(String str) {
      return "<blockquote>" + str + "</blockquote>";
   }

   public static String newline() {
      return newline(1);
   }

   public static String newline(int num) {
      StringBuilder str = new StringBuilder();
      for (int i = 0; i < num; i++) {
         str.append("<br />");
      }
      return str + "";
   }

   public static String name(int num) {
      return nameTarget("" + num);
   }

   /**
    * Create target for hyperlink to jump to
    *
    * @return Return name target string
    */
   public static String nameTarget(String str) {
      if (str == null) {
         return "";
      }
      return "<A NAME=\"" + str + "\">";
   }

   /**
    * Create &lt;a href> hyperlink to nameTarget
    *
    * @return Return name link string
    */
   public static String nameLink(int num, String text) {
      return nameLink("" + num, text);
   }

   /**
    * Create &lt;a href> hyperlink to nameTarget
    *
    * @return Return name link string
    */
   public static String nameLink(String name, String text) {
      return "<A HREF=\"#" + name + "\">" + text + "</A>";
   }

   /**
    * Create &lt;a href> hyperlink to nameTarget using name as hyperlink tag and display text
    *
    * @return Return name link string
    */
   public static String nameLink(String name) {
      return "<A HREF=\"#" + name + "\">" + name + "</A>";
   }

   public static String simpleTable(String str) {
      return simpleTable(str, 100);
   }

   /**
    * Create a table with one row/colum containing str
    *
    * @return return simple table string
    */
   public static String simpleTable(String str, int width) {
      return "<table border=\"0\" cellpadding=\"3\" cellspacing=\"0\" width=\"" + width + "%\">" + "<tr><td>" + str + "</td></tr>" + "</table>";
   }

   /**
    * Create a table with one row/colum containing str
    *
    * @return Return border table string
    */
   public static String borderTable(String str, int width, String bgcolor, String caption) {
      return startBorderTable(width, bgcolor, caption) + str + endBorderTable();
   }

   public static String startBorderTable(int width, String bgcolor, String caption) {
      String capStr = "";
      if (!caption.equals("")) {
         capStr = "<caption ALIGN=top>" + caption + "</caption>";
      }
      return "<table border=\"1\" align=\"center\" bgcolor=\"" + bgcolor + "\" cellpadding=\"3\" cellspacing=\"0\" width=\"" + width + "%\">" + capStr + "<tr><td>";
   }

   public static String endBorderTable() {
      return "</td></tr></table>";
   }

   /**
    * Create a table with one row multi column containing str[]
    *
    * @param str = array of strings for columns
    * @return Return multi-column table string
    */
   public static String multiColumnTable(String... str) {
      return multiColumnTable(85, str);
   }

   /**
    * Create a table with one row multi column containing str[]
    *
    * @param str - array of strings for columns
    * @param width - percent (1..100) of screen for table
    * @return Return multi-column table string
    */
   public static String multiColumnTable(int width, String... str) {
      StringBuilder s = new StringBuilder();
      s.append("<table border=\"0\" cellpadding=\"3\" cellspacing=\"0\" width=\"" + width + "%\"><tr>");
      for (int i = 0; i < str.length; i++) {
         s.append("<td>");
         s.append(str[i]);
         s.append("</td>");
      }
      s.append("</tr></table>");
      return s.toString();
   }

   public static String beginMultiColumnTable(int width) {
      return beginMultiColumnTable(width, 0);
   }

   public static String beginMultiColumnTable(int width, int border) {
      return beginMultiColumnTable(width, border, null);
   }

   public static String beginMultiColumnTable(int width, int border, Integer color) {
      return "<table border=\"" + border + "\" " + (color != null ? "color=\"" + color + "\"" : "") + "cellpadding=\"3\" cellspacing=\"0\" width=\"" + width + "%\">";
   }

   public static String endMultiColumnTable() {
      return "</table>";
   }

   public static String addRowMultiColumnTable(String... str) {
      return addRowMultiColumnTable(str, null, null);
   }

   public static String addRowMultiColumnTable(String[] str, String[] colOptions) {
      return addRowMultiColumnTable(str, colOptions, null);
   }

   public static String addRowMultiColumnTable(String[] str, String[] colOptions, String backgroundColor) {
      StringBuilder s = new StringBuilder();
      if (backgroundColor != null) {
         s.append("<tr bgcolor=\"" + backgroundColor + "\">");
      } else {
         s.append("<tr>");
      }
      String show = "";
      for (int i = 0; i < str.length; i++) {
         show = str[i];
         if (!Strings.isValid(show)) {
            show = AHTML.addSpace(1);
         }
         String colOptionStr = "";
         if (colOptions != null) {
            colOptionStr = colOptions[i];
         }
         s.append("<td" + (Strings.isValid(colOptionStr) ? colOptionStr : "") + ">" + show + "</td>");
      }
      s.append("</tr>");
      return s.toString();
   }

   public static String addRowSpanMultiColumnTable(String str, int span) {
      return "<tr><td colspan=" + span + ">" + str + "</td></tr>";
   }

   public static class CellItem {
      String text;
      private final String fgColor;
      private final String bgColor;

      public CellItem(String text) {
         this(text, null, null);
      }

      public CellItem(String text, String fgColor, String bgColor) {
         this.text = text;
         this.fgColor = fgColor;
         this.bgColor = bgColor;
      }
   }

   public static String addRowMultiColumnTable(Collection<CellItem> items) {
      StringBuilder s = new StringBuilder("<tr>");
      for (CellItem item : items) {
         if (!Strings.isValid(item.text)) {
            item.text = ".";
         }
         if (item.bgColor != null) {
            s.append("<td bgcolor=\"" + item.bgColor + "\">");
         } else {
            s.append("<td>");
         }
         s.append(AHTML.color(item.fgColor, item.text));
         s.append("</td>");
      }
      s.append("</tr>");
      return s.toString();
   }

   public static String addHeaderRowMultiColumnTable(List<String> strs) {
      return addHeaderRowMultiColumnTable(strs.toArray(new String[strs.size()]));
   }

   public static String addHeaderRowMultiColumnTable(String[] str) {
      return addHeaderRowMultiColumnTable(str, null);
   }

   public static String addHeaderRowMultiColumnTable(String[] str, Integer width[]) {
      StringBuilder s = new StringBuilder("<tr>");
      String widthStr = "";
      for (int i = 0; i < str.length; i++) {
         if (width != null) {
            widthStr = " width =\"" + width[i] + "\"";
         }
         s.append("<th");
         s.append(widthStr);
         s.append(">");
         s.append(str[i]);
         s.append("</th>");
      }
      s.append("</tr>");
      return s.toString();
   }

   public static void addSimpleTableRow(Appendable appendable, String contents) throws IOException {
      appendable.append("<tr><td>");
      appendable.append(contents);
      appendable.append("</td></tr>");
   }

   public static void addSimpleHeaderRow(Appendable appendable, String contents) throws IOException {
      appendable.append("<tr><th>");
      appendable.append(contents);
      appendable.append("</th></tr>");
   }

   public static void beginSimpleTable(Appendable appendable) throws IOException {
      beginSimpleTable(appendable, 1, 100);
   }

   public static void beginSimpleTable(Appendable appendable, int border, int width) throws IOException {
      appendable.append("<table border=\"");
      appendable.append(String.valueOf(border));
      appendable.append("\" cellpadding=\"0\" cellspacing=\"0\" width=\"");
      appendable.append(String.valueOf(width));
      appendable.append("%\">");
   }

   public static void endSimpleTable(Appendable appendable) throws IOException {
      appendable.append("</table>");
   }

   public static String createTable(List<String> datas, String[] headers, int numColumns, int cellPadding, int border) {
      StringBuilder table = new StringBuilder(begin);

      if (datas == null) {
         throw new IllegalArgumentException("The data can not be null");
      }
      if (datas.size() % numColumns != 0) {
         throw new IllegalArgumentException(
            "The table could not be created becuase the data does not match the column size");
      }
      if (border > 0) {
         table.append("border=\"" + border + "\"");
      }
      if (cellPadding > 0) {
         table.append("cellpadding=\"" + cellPadding + "\"");
      }
      table.append(">");

      if (headers != null && headers.length == numColumns) {
         table.append("<tr>");
         for (String header : headers) {
            table.append("<th>" + header + "</th>");
         }
         table.append("</tr>");
      }

      int colIndex = 0;
      for (String data : datas) {

         if (colIndex == 0) {
            table.append("<tr>");
         }
         table.append("<td>" + data + "</td>");
         colIndex++;

         if (colIndex == numColumns) {
            table.append("</tr>");
            colIndex = 0;
         }
      }
      return table.toString();
   }

}