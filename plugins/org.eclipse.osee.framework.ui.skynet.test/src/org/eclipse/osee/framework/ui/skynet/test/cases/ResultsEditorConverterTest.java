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

package org.eclipse.osee.framework.ui.skynet.test.cases;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn.SortDataType;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.TableWriterAdaptor;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorProvider;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorTab;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditorConverter;
import org.eclipse.osee.framework.ui.skynet.results.table.IResultsEditorTableTab;
import org.eclipse.osee.framework.ui.skynet.results.table.IResultsXViewerRow;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsXViewerRow;
import org.eclipse.swt.widgets.Composite;
import org.junit.Assert;
import org.junit.Test;
import com.lowagie.text.pdf.PdfReader;

/**
 * @see Test Case for {@link ResultsEditorConverter}
 * @author Roberto E. Escobar
 */
public class ResultsEditorConverterTest {

   @Test
   public void testNotSupportedCheck() {
      ResultsEditorConverter converter = new ResultsEditorConverter();
      Writer dummyWriter = new StringWriter();
      IResultsEditorProvider dummyProvider = new MockResultsProvider("");

      checkArgException(converter, null, dummyWriter, dummyProvider);
      checkArgException(converter, "html", null, dummyProvider);
      checkArgException(converter, "html", dummyWriter, null);
   }

   @Test
   public void testCreateCollapsingHTMLTabsCase() throws OseeCoreException {
      StringWriter writer = new StringWriter();
      MockResultsProvider provider = new MockResultsProvider("Test Provider");
      executeConversion("collapsing_html", writer, provider);
      Assert.assertEquals(getExpectedHTMLReport(provider), writer.toString().replaceAll("\n", ""));
   }

   @Test
   public void testCreateHTMLTabsCase() throws OseeCoreException {
      StringWriter writer = new StringWriter();
      MockResultsProvider provider = new MockResultsProvider("Test Provider");
      executeConversion("html", writer, provider);
      System.out.println(writer.toString());
      //      Assert.assertEquals(getExpectedHTMLReport(provider), writer.toString().replaceAll("\n", ""));
   }

   @Test
   public void testCreateExcelTabsCase() throws OseeCoreException {
      StringWriter writer = new StringWriter();
      MockResultsProvider provider = new MockResultsProvider("Test Provider");
      executeConversion("excel", writer, provider);

      Assert.assertEquals(getExpectedExcelReport(provider), writer.toString().replaceAll("\n", ""));
   }

   @Test
   public void testCreatePDFTabsCase() throws Exception {
      StringWriter writer = new StringWriter();
      MockResultsProvider provider = new MockResultsProvider("Test Provider");
      executeConversion("pdf", writer, provider);
      writer.flush();

      byte[] expected = getExpectedOtherReport("pdf", provider);
      byte[] actual = writer.toString().getBytes("ISO-8859-1");
      Assert.assertEquals(expected.length, actual.length);

      //      String home = System.getProperty("user.home");
      //      Lib.writeBytesToFile(expected, new File(home, "expected.pdf"));
      //      Lib.writeBytesToFile(actual, new File(home, "actual.pdf"));
      //      for (int index = 0; index < expected.length; index++) {
      //         Assert.assertEquals(String.format("no match at [%s]", index), expected[index], actual[index]);
      //      }

      // Check that Actual is a valid file
      PdfReader reader = null;
      try {
         reader = new PdfReader(actual);
         Assert.assertEquals(1, reader.getNumberOfPages());
      } finally {
         if (reader != null) {
            reader.close();
         }
      }
   }

   @Test
   public void testCreateRTFTabsCase() throws Exception {
      StringWriter writer = new StringWriter();
      MockResultsProvider provider = new MockResultsProvider("Test Provider");
      executeConversion("rtf", writer, provider);
      writer.flush();

      byte[] expected = getExpectedOtherReport("rtf", provider);
      byte[] actual = writer.toString().getBytes("ISO-8859-1");
      Assert.assertEquals(expected.length, actual.length);
   }

   private void executeConversion(String type, StringWriter writer, MockResultsProvider provider) throws OseeCoreException {
      ResultsEditorConverter converter = new ResultsEditorConverter();
      provider.tabs.add(createTab("tab 1"));
      provider.tabs.add(createTab("tab 2"));
      provider.tabs.add(createTab("tab 3"));
      converter.convert(type, writer, provider);
   }

   private String getExpectedHTMLReport(MockResultsProvider provider) throws OseeCoreException {
      StringBuilder builder = new StringBuilder();
      builder.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD html 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\"><html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\">\n");
      builder.append("<title>Report</title>\n");
      builder.append("<style type=\"text/css\">\n");
      builder.append(" .blueBox { color: black; background-color:#999999; display: block; padding: 6px; margin-top: 8px; width:95%; cursor:pointer; border: solid;  border-color:black; border-width: thin; text-align: left; vertical-align: middle; }\n");
      builder.append(" .results { color: black; background-color:whitesmoke; display: block; padding: 6px; width:95%; cursor:pointer; border: solid; border-width: thin; text-align: left; vertical-align: middle; }\n");
      builder.append("</style>\n");
      builder.append("<script type=\"text/javascript\">\n");
      builder.append("function opendiv(id){\n");
      builder.append("var divStyle = document.getElementById(id).style;\n");
      builder.append("divStyle.display = (divStyle.display=='none') ? 'block':'none';\n");
      builder.append("}\n");
      builder.append("</script>\n</head>\n<body>\n<div id=\"alldivs\">\n");

      int index = 0;
      for (IResultsEditorTab tab : provider.getResultsEditorTabs()) {
         builder.append(getHTMLTab(tab.getTabName(), ++index));
      }
      builder.append("</body>\n</html>");
      return builder.toString().replaceAll("\n", "");
   }

   private String getHTMLTab(String name, int index) {
      StringBuilder builder = new StringBuilder();
      builder.append("<a class=\"blueBox\" onclick=\"opendiv('div" + index + "');\">");
      builder.append(name);
      builder.append("</a>");
      builder.append("<div class=\"results\" id=\"div" + index + "\" style=\"display:none;\">");

      builder.append("<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" width=\"95%\">");
      builder.append("<tr><th>1</th><th>2</th><th>3</th></tr>");
      builder.append("<tr><td>a</td><td>b</td><td>c</td></tr>");
      builder.append("<tr><td>e</td><td>d</td><td>f</td></tr>");
      builder.append("</table></div>");
      return builder.toString();
   }

   private String getExcelTab(String name, int index) {
      StringBuilder builder = new StringBuilder();
      builder.append(" <Worksheet ss:Name=\"");
      builder.append(name);
      builder.append("\">  ");
      builder.append("<Table x:FullColumns=\"1\" x:FullRows=\"1\" ss:ExpandedColumnCount=\"3\">   ");
      builder.append("<Row>    ");
      builder.append("<Cell><Data ss:Type=\"String\">1</Data></Cell>    ");
      builder.append("<Cell><Data ss:Type=\"String\">2</Data></Cell>    ");
      builder.append("<Cell><Data ss:Type=\"String\">3</Data></Cell>   ");
      builder.append("</Row>   ");
      builder.append("<Row>    ");
      builder.append("<Cell><Data ss:Type=\"String\">a</Data></Cell>    ");
      builder.append("<Cell><Data ss:Type=\"String\">b</Data></Cell>    ");
      builder.append("<Cell><Data ss:Type=\"String\">c</Data></Cell>   ");
      builder.append("</Row>   ");
      builder.append("<Row>    ");
      builder.append("<Cell><Data ss:Type=\"String\">e</Data></Cell>    ");
      builder.append("<Cell><Data ss:Type=\"String\">d</Data></Cell>    ");
      builder.append("<Cell><Data ss:Type=\"String\">f</Data></Cell>   ");
      builder.append("</Row>  ");
      builder.append("</Table> ");
      builder.append("</Worksheet>");
      return builder.toString();
   }

   private String getExpectedExcelReport(MockResultsProvider provider) throws OseeCoreException {
      StringBuilder builder = new StringBuilder();
      builder.append("<?xml version=\"1.0\"?><?mso-application progid=\"Excel.Sheet\"?>");
      builder.append("<Workbook");
      builder.append(" xmlns=\"urn:schemas-microsoft-com:office:spreadsheet\"");
      builder.append(" xmlns:o=\"urn:schemas-microsoft-com:office:office\"");
      builder.append(" xmlns:x=\"urn:schemas-microsoft-com:office:excel\"");
      builder.append(" xmlns:ss=\"urn:schemas-microsoft-com:office:spreadsheet\"");
      builder.append(" xmlns:html=\"http://www.w3.org/TR/REC-html40\">");
      int index = 0;
      for (IResultsEditorTab tab : provider.getResultsEditorTabs()) {
         builder.append(getExcelTab(tab.getTabName(), ++index));
      }
      builder.append("</Workbook>");
      return builder.toString().replaceAll("\n", "");
   }

   private byte[] getExpectedOtherReport(String type, MockResultsProvider provider) throws Exception {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      TableWriterAdaptor writerAdaptor = new TableWriterAdaptor(type, outputStream);
      writerAdaptor.writeTitle("Report");
      writerAdaptor.openDocument();
      for (IResultsEditorTab tab : provider.getResultsEditorTabs()) {
         writerAdaptor.writeTitle(tab.getTabName());
         writerAdaptor.writeHeader(new String[] {"1", "2", "3"});
         writerAdaptor.writeRow(new String[] {"a", "b", "c"});
         writerAdaptor.writeRow(new String[] {"e", "d", "f"});
         writerAdaptor.writeDocument();
      }
      writerAdaptor.close();
      return outputStream.toByteArray();
   }

   private IResultsEditorTableTab createTab(String tabName) {
      List<String[]> rows = new ArrayList<String[]>();
      rows.add(new String[] {"a", "b", "c"});
      rows.add(new String[] {"e", "d", "f"});
      return new MockResultsEditorTableTab(tabName, new String[] {"1", "2", "3"}, rows);
   }

   private void checkArgException(ResultsEditorConverter converter, String type, Writer writer, IResultsEditorProvider provider) {
      try {
         converter.convert(type, null, null);
         Assert.fail("This line should not be executed");
      } catch (Exception ex) {
         Assert.assertTrue("Unexcepted Exception - " + ex, ex instanceof OseeArgumentException);
      }
   }

   private final class MockResultsProvider implements IResultsEditorProvider {
      protected String editorName;
      protected List<IResultsEditorTab> tabs;

      public MockResultsProvider(String editorName) {
         this.editorName = editorName;
         this.tabs = new ArrayList<IResultsEditorTab>();
      }

      @Override
      public String getEditorName() throws OseeCoreException {
         return editorName;
      }

      @Override
      public List<IResultsEditorTab> getResultsEditorTabs() throws OseeCoreException {
         return tabs;
      }
   }

   private final class MockResultsEditorTableTab implements IResultsEditorTableTab {
      protected String tabName;
      protected String[] columns;
      protected List<String[]> rows;

      public MockResultsEditorTableTab(String tabName, String[] columns, List<String[]> rows) {
         this.tabName = tabName;
         this.columns = columns;
         this.rows = rows;
      }

      @Override
      public List<XViewerColumn> getTableColumns() throws OseeCoreException {
         List<XViewerColumn> xColumns = new ArrayList<XViewerColumn>();
         for (String col : columns) {
            xColumns.add(new XViewerColumn("", col, 0, 0, true, SortDataType.String, false, ""));
         }
         return xColumns;
      }

      @Override
      public Collection<IResultsXViewerRow> getTableRows() throws OseeCoreException {
         List<IResultsXViewerRow> xRows = new ArrayList<IResultsXViewerRow>();
         for (String[] row : rows) {
            xRows.add(new ResultsXViewerRow(row));
         }
         return xRows;
      }

      @Override
      public Composite createTab(Composite parent, ResultsEditor resultsEditor) throws OseeCoreException {
         return null;
      }

      @Override
      public String getTabName() {
         return tabName;
      }
   }
}
