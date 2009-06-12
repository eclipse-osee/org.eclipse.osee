/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.util;

import java.awt.Color;
import java.io.OutputStream;
import com.lowagie.text.Cell;
import com.lowagie.text.DocWriter;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.ElementTags;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Table;
import com.lowagie.text.html.HtmlWriter;
import com.lowagie.text.markup.WebColors;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.rtf.RtfWriter2;

/**
 * @author Roberto E. Escobar
 */
public class TableWriterAdaptor {
   public static final String ENCODING = "ISO-8859-1";
   public static final String PDF = "pdf";
   public static final String HTML = "html";
   public static final String RTF = "rtf";

   private final Document document;
   private Table table;
   private final OutputStream outputStream;
   private boolean complete;
   private String title;
   private DocWriter writer;

   public TableWriterAdaptor(String writerId, OutputStream outputStream) throws Exception {
      this(writerId, PageSize.LETTER.rotate(), outputStream);
   }

   public TableWriterAdaptor(String writerId, Rectangle rectangle, OutputStream outputStream) throws Exception {
      this.complete = false;
      this.document = new Document(rectangle);
      this.outputStream = outputStream;
      this.setDocWriter(writerId);
   }

   private void setDocWriter(String value) throws Exception {
      if (value.equalsIgnoreCase(PDF)) {
         this.writer = PdfWriter.getInstance(document, outputStream);
         //         ((PdfWriter) writer).setPDFXConformance(PdfWriter.PDFX32002);
         ((PdfWriter) writer).setPdfVersion(PdfWriter.VERSION_1_6);
      } else if (value.equalsIgnoreCase(HTML)) {
         this.writer = HtmlWriter.getInstance(document, outputStream);
      } else if (value.equalsIgnoreCase(RTF)) {
         this.writer = RtfWriter2.getInstance(document, outputStream);
      }
   }

   public void writeHeader(String[] headers) throws Exception {
      table = new Table(headers.length);
      table.setBorder(1);
      table.setSpacing(1);
      table.setPadding(1);
      //      this.table.setCellsFitPage(true);
      //      this.table.setTableFitsPage(true);
      table.setBorderColor(new Color(0, 0, 0));
      writeHeader(table, headers);
   }

   public void writeHeader(Table table, String[] headers) throws Exception {
      for (String header : headers) {
         Cell cell = new Cell();
         cell.setHeader(true);
         cell.setColspan(1);
         cell.setBackgroundColor(WebColors.getRGBColor("#d9d9d9"));
         cell.setHorizontalAlignment(ElementTags.ALIGN_CENTER);

         Font font =
               FontFactory.getFont("Times New Roman", BaseFont.CP1252, BaseFont.EMBEDDED, 9, Font.BOLD,
                     WebColors.getRGBColor("#000000"));
         Paragraph paragraph = new Paragraph(header, font);
         paragraph.setAlignment(ElementTags.ALIGN_CENTER);
         cell.add(paragraph);
         table.addCell(cell);
      }
   }

   public void writeRow(String... cellData) {
      writeRow(this.table, cellData);
   }

   public void writeRow(Table table, String... cellData) {
      for (String cellText : cellData) {
         Cell cell = new Cell();
         cell.setHeader(false);
         cell.setColspan(1);
         Font font =
               FontFactory.getFont("Times New Roman", BaseFont.CP1252, BaseFont.EMBEDDED, 9, Font.NORMAL,
                     WebColors.getRGBColor("#000000"));
         Paragraph paragraph = new Paragraph(cellText, font);
         cell.add(paragraph);
         table.addCell(cell);
      }
   }

   public void writeTitle(String title) {
      this.title = title;
   }

   public Document getDocument() {
      return document;
   }

   public Document openDocument() {
      document.addTitle(title);
      document.addSubject("This report is automatically generated.");
      document.addKeywords("Metadata, iText");
      document.addCreationDate();
      if (writer instanceof PdfWriter) {
         PdfWriter pdfWriter = (PdfWriter) writer;
         pdfWriter.createXmpMetadata();
      }
      document.open();
      return document;
   }

   public void writeDocument() throws DocumentException {
      Font font =
            FontFactory.getFont("Times New Roman", BaseFont.CP1252, BaseFont.EMBEDDED, 9, Font.BOLD,
                  WebColors.getRGBColor("#000000"));
      Paragraph paragraph = new Paragraph(title, font);
      paragraph.setAlignment(ElementTags.ALIGN_CENTER);
      document.add(paragraph);
      document.add(table);
   }

   public void close() {
      if (complete != true) {
         document.close();
         complete = true;
      }
   }

   public boolean isCompleted() {
      return complete;
   }

   //   public String toString() {
   //      try {
   //         return outputStream.toString("ISO-8859-1");
   //      } catch (Exception ex) {
   //         ex.printStackTrace();
   //      }
   //      return "";
   //   }

}