/*********************************************************************
 * Copyright (c) 2021 Robert Bosch Engineering and Business Solutions Ltd India
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Robert Bosch Engineering and Business Solutions Ltd India - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.icteam.reqif.export;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;
import org.eclipse.rmf.reqif10.xhtml.XhtmlDivType;
import org.eclipse.rmf.reqif10.xhtml.XhtmlFactory;
import org.eclipse.rmf.reqif10.xhtml.XhtmlInlPresType;
import org.eclipse.rmf.reqif10.xhtml.XhtmlPType;

/**
 * This class is used to Parse the Word template content which is in form of Xml
 * 
 * @author Manjunath Sangappa
 */
public class CustomisedXmlTextInputStream extends BufferedInputStream {


  private final StringBuilder buffer = new StringBuilder();

  private final StringBuilder b = new StringBuilder();
  private static final String STOP_PARAGRAPH = "</w:p>";
  private static final String WORD_UNDERLINE = "<w:u";
  private static final String WORD_ITALIC = "<w:i>";
  private static final String END_WORDML_TEXT = "</w:t>";
  private static final String WORD_BOLD = "<w:b>";
  private static final String START_PICT = "<w:binData";
  private static final String START_WR = "<w:r";
  private static final String END_WR = "</w:r>";


  XhtmlDivType xhmtlDiv;

  byte[] bTemp = new byte[16000];

  int j = 1;

  /**
   * @param arg0
   */
  public CustomisedXmlTextInputStream(final InputStream arg0) {
    super(arg0);
  }


  /**
   * @param input :
   * @param div :
   * @throws UnsupportedEncodingException :
   */
  public CustomisedXmlTextInputStream(final String input, final XhtmlDivType div) throws UnsupportedEncodingException {
    this(new ByteArrayInputStream(input.getBytes("UTF-8")));
    this.xhmtlDiv = div;
  }


  @Override
  public synchronized int read() throws IOException {

    int value = process(super.read());
    return value;
  }

  @Override
  public synchronized int read(final byte[] b, final int off, final int len) throws IOException {
    if (b == null) {
      throw new NullPointerException();
    }
    else if ((off < 0) || (len < 0) || (len > (b.length - off))) {
      throw new IndexOutOfBoundsException();
    }
    else if (len == 0) {
      return 0;
    }

    int c = this.read();
    if (c == -1) {
      return -1;
    }
    this.bTemp[off] = (byte) c;
    b[off] = (byte) c;

    int i = 1;
    try {
      for (; i < len; i++) {
        c = this.read();
        if (c == -1) {
          break;
        }
        this.bTemp[off + this.j] = (byte) c;
        b[off + i] = (byte) c;
        this.j++;
      }
    }
    catch (IOException ee) {
      // Do Nothing
    }
    return i;
  }


  private int readFromOriginalBuffer() throws IOException {
    return super.read();
  }

  /**
   * This method is used to get the Whole Paragraph i.e whold <w.p> ....</w.p> tag
   * 
   * @param read
   * @return
   * @throws IOException
   */
  private int process(final int value) throws IOException {

    this.buffer.append((char) value);

    if ((char) value == '>') {
      String tag = this.buffer.toString();
      if (tag.endsWith(STOP_PARAGRAPH)) {
        createSubInputStream(tag);
        this.buffer.delete(0, this.buffer.length());
        this.bTemp = new byte[16000];
        this.j = 1;
      }
    }
    if (this.buffer.toString().endsWith(START_PICT)) {
      this.buffer.delete(0, this.buffer.length());
      int value1 = readFromOriginalBuffer();
      while ((char) value1 != '<') {
        value1 = readFromOriginalBuffer();
      }
      this.buffer.append((char) readFromOriginalBuffer());
      while (!this.buffer.toString().endsWith(STOP_PARAGRAPH)) {
        value1 = readFromOriginalBuffer();
        this.buffer.append((char) value1);
      }
      this.buffer.delete(0, this.buffer.length());
    }

    // }
    return value;
  }

  /**
   * This method creates a sub input stream for one paragraph (<w.p> .... </w.p>) and parse the Requried string to
   * create Xhmtl Content
   * 
   * @param tag
   * @throws IOException
   */
  private void createSubInputStream(final String tag) throws IOException {

    List<WordMLContent> listWorMLContents = new ArrayList<WordMLContent>();
    ByteArrayInputStream in = new ByteArrayInputStream(tag.getBytes("UTF-8"));
    int c;
    StringBuilder bufferTemp = new StringBuilder();
    StringBuilder bufferWr = new StringBuilder();
    String xhmtlData = "";
    while ((c = in.read()) != -1) {
      bufferTemp.append((char) c);
      bufferWr.append((char) c);
      if ((char) c == '>') {

        String temp = bufferTemp.toString();
        if (temp.endsWith(END_WORDML_TEXT)) {
          xhmtlData = temp.replace("</w:t>", "");
        }
        if (temp.endsWith(END_WR)) {
          bufferWr.delete(0, bufferWr.length());
        }

        bufferTemp.delete(0, bufferTemp.length());
        if (xhmtlData.length() > 0) {
          // String data = handleSpecialCharaters(tag);
          boolean bold = false, italic = false, underline = false;
          if (bufferWr.toString().contains(WORD_BOLD)) {
            bold = true;
          }
          if (bufferWr.toString().contains(WORD_ITALIC)) {
            italic = true;
          }
          if (bufferWr.toString().contains(WORD_UNDERLINE)) {
            // Not Yet Implemented : nas1kor
            underline = true;
          }

          if (xhmtlData.contains("&amp;")) {
            xhmtlData = xhmtlData.replace("&amp;", "&");
          }

          WordMLContent content = new WordMLContent();
          content.setInputString(xhmtlData);
          content.setBold(bold);
          content.setItalic(italic);
          listWorMLContents.add(content);
          xhmtlData = "";
        }
      }
    }
    if (!tag.contains("<w:t>")) {
      WordMLContent content = new WordMLContent();
      content.setInputString("");
      content.setBold(false);
      content.setItalic(false);
      listWorMLContents.add(content);
    }

    createxhtmlContent(listWorMLContents);
  }


  /**
   * Creates the exact xhmtl content and adds the necessary data
   * 
   * @param xhmtlContent
   * @param xhmtlData
   * @param bold
   * @param italic
   * @param underline
   */
  private void createxhtmlContent(final List<WordMLContent> listWorMLContents) {

    XhtmlPType xhtmlPType = null;
    if (listWorMLContents.size() > 0) {
      xhtmlPType = XhtmlFactory.eINSTANCE.createXhtmlPType();
    }
    if (xhtmlPType != null) {
      for (WordMLContent wordMLContent : listWorMLContents) {
        String xhmtlData = wordMLContent.getInputString();
        boolean bold = wordMLContent.isBold();
        boolean italic = wordMLContent.isItalic();


        if (bold && italic) {
          XhtmlInlPresType xhtmlBoldType = XhtmlFactory.eINSTANCE.createXhtmlInlPresType();
          XhtmlInlPresType xhtmlitalicType = XhtmlFactory.eINSTANCE.createXhtmlInlPresType();
          xhtmlitalicType.getMixed().add(XMLTypePackage.Literals.XML_TYPE_DOCUMENT_ROOT__TEXT, xhmtlData);
          xhtmlBoldType.getI().add(xhtmlitalicType);
          xhtmlPType.getB().add(xhtmlBoldType);
        }
        else if (bold) {
          XhtmlInlPresType xhtmlBoldType = XhtmlFactory.eINSTANCE.createXhtmlInlPresType();
          xhtmlBoldType.getMixed().add(XMLTypePackage.Literals.XML_TYPE_DOCUMENT_ROOT__TEXT, xhmtlData);
          xhtmlPType.getB().add(xhtmlBoldType);
        }
        else if (italic) {
          XhtmlInlPresType xhtmlitalicType = XhtmlFactory.eINSTANCE.createXhtmlInlPresType();
          xhtmlitalicType.getMixed().add(XMLTypePackage.Literals.XML_TYPE_DOCUMENT_ROOT__TEXT, xhmtlData);
          xhtmlPType.getI().add(xhtmlitalicType);
        }
        else {
          xhtmlPType.getMixed().add(XMLTypePackage.Literals.XML_TYPE_DOCUMENT_ROOT__TEXT, xhmtlData);
        }

      }
      this.xhmtlDiv.getP().add(xhtmlPType);
    }

  }

}