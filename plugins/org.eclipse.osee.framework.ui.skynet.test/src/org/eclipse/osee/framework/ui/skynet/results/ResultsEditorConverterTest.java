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

package org.eclipse.osee.framework.ui.skynet.results;

import com.lowagie.text.pdf.PdfReader;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.results.table.IResultsEditorTableTab;
import org.eclipse.osee.framework.ui.skynet.results.table.IResultsXViewerRow;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsXViewerRow;
import org.eclipse.osee.framework.ui.skynet.util.TableWriterAdaptor;
import org.eclipse.swt.widgets.Composite;
import org.junit.Assert;
import org.junit.Test;

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
   public void testCreateCollapsingHTMLTabsCase()  {
      StringWriter writer = new StringWriter();
      MockResultsProvider provider = new MockResultsProvider("Test Provider");
      executeConversion("collapsing_html", writer, provider);
      Assert.assertEquals(getExpected_Collapsing_HTMLReport(provider), Strings.minimize(writer.toString()));
   }

   @Test
   public void testCreateHTMLTabsCase()  {
      StringWriter writer = new StringWriter();
      MockResultsProvider provider = new MockResultsProvider("Test Provider");
      executeConversion("html", writer, provider);
      Assert.assertEquals(getExpectedHTMLReport(provider), AHTML.removeComments(Strings.minimize(writer.toString())));
   }

   @Test
   public void testCreateExcelTabsCase()  {
      StringWriter writer = new StringWriter();
      MockResultsProvider provider = new MockResultsProvider("Test Provider");
      executeConversion("excel", writer, provider);

      // remove from the comparison the style information in the row element
      String actualXml = writer.toString().replaceAll("<Row[^>]+>", "<Row>").replaceAll("\\s*<Column[^>]+>", "");
      actualXml = actualXml.substring(actualXml.indexOf(" <Worksheet"));

      Assert.assertEquals(getExpectedExcelReport(provider), actualXml);
   }

   @Test
   public void testCreatePDFTabsCase() throws Exception {
      StringWriter writer = new StringWriter();
      MockResultsProvider provider = new MockResultsProvider("Test Provider");
      executeConversion("pdf", writer, provider);

      //      byte[] expected = getExpectedOtherReport("pdf", provider);
      byte[] actual = writer.toString().getBytes("ISO-8859-1");

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

   private void executeConversion(String type, StringWriter writer, MockResultsProvider provider)  {
      ResultsEditorConverter converter = new ResultsEditorConverter();
      provider.tabs.add(createTab("tab 1"));
      provider.tabs.add(createTab("tab 2"));
      provider.tabs.add(createTab("tab 3"));
      converter.convert(type, writer, provider);
   }

   private String getExpectedHTMLReport(MockResultsProvider provider) {
      StringBuilder builder = new StringBuilder();
      builder.append("<html><head>");
      builder.append("<title>Report</title>\n");
      builder.append("<meta name=\"subject\" content=\"This report is automatically generated.\" />");
      builder.append("<meta name=\"keywords\" content=\"Metadata, iText\" />");
      builder.append(
         "</head>\n<body leftmargin=\"36.0\" rightmargin=\"36.0\" topmargin=\"36.0\" bottommargin=\"36.0\">\n");

      //split into 2 to avoid % format exception
      String beginTable =
         "<table width=\"80.0%\" align=\"Center\" cellpadding=\"1.0\" cellspacing=\"1.0\" border=\"1.0\" bordercolor=\"#000000\">";
      String tabTable = "<tr>%s</tr><tr>%s</tr><tr>%s</tr></table>";
      String th =
         "<th border=\"0.5\" bgcolor=\"#d9d9d9\" align=\"Center\" width=\"0\"><div align=\"Center\" style=\"font-family: unknown; font-size: 9.0pt; color: #000000; font-weight: bold; \">%s</div></th>"; //1-3
      String td =
         "<td border=\"0.5\" width=\"0\"><div style=\"font-family: unknown; font-size: 9.0pt; color: #000000; \">%s</div></td>";

      List<IResultsEditorTab> tabs = provider.getResultsEditorTabs();

      if (!tabs.isEmpty()) {
         for (int tabIndex = 0; tabIndex < tabs.size(); tabIndex++) {

            String[] items = new String[3];

            builder.append(String.format(
               "<div align=\"Center\" style=\"font-family: unknown; font-size: 9.0pt; color: #000000; font-weight: bold; \">tab %s</div>",
               tabIndex + 1));

            StringBuilder tempBuilder = new StringBuilder((th.length() + td.length() * 2) * 3);
            for (int j = 0; j < 3; j++) {
               switch (j) {
                  case 0:
                     for (int thIndex = 1; thIndex <= 3; thIndex++) {
                        tempBuilder.append(String.format(th, thIndex));
                     }
                     break;
                  case 1:
                     for (String item : new String[] {"a", "b", "c"}) {
                        tempBuilder.append(String.format(td, item));
                     }
                     break;
                  case 2:
                     for (String item : new String[] {"e", "d", "f"}) {
                        tempBuilder.append(String.format(td, item));
                     }
                     break;
               }
               items[j] = tempBuilder.toString();
               tempBuilder.setLength(0);
            }
            builder.append(beginTable);
            builder.append(String.format(tabTable, items[0], items[1], items[2]));
         }
      }
      builder.append("</body>");
      builder.append("</html>");
      return Strings.minimize(builder.toString());
   }

   private String getExpected_Collapsing_HTMLReport(MockResultsProvider provider) {
      StringBuilder builder = new StringBuilder();
      builder.append(
         "<!DOCTYPE html PUBLIC \"-//W3C//DTD html 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\"><html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\">\n");
      builder.append("<title>Report</title>\n");
      builder.append("<style type=\"text/css\">\n");
      builder.append(
         " .blueBox { color: black; background-color:#999999; display: block; padding: 6px; margin-top: 8px; width:95%; cursor:pointer; border: solid;  border-color:black; border-width: thin; text-align: left; vertical-align: middle; }\n");
      builder.append(
         " .results { color: black; background-color:whitesmoke; display: block; padding: 6px; width:95%; cursor:pointer; border: solid; border-width: thin; text-align: left; vertical-align: middle; }\n");
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

   private void addExcelTabXml(StringBuilder builder, String name, int index) {
      builder.append(" <Worksheet ss:Name=\"");
      builder.append(name);
      builder.append("\">\n");
      builder.append("  <Table x:FullColumns=\"1\" x:FullRows=\"1\" ss:ExpandedColumnCount=\"3\">\n");
      builder.append("   <Row>\n");
      builder.append("    <Cell><Data ss:Type=\"String\">1</Data></Cell>\n");
      builder.append("    <Cell><Data ss:Type=\"String\">2</Data></Cell>\n");
      builder.append("    <Cell><Data ss:Type=\"String\">3</Data></Cell>\n");
      builder.append("   </Row>\n");
      builder.append("   <Row>\n");
      builder.append("    <Cell><Data ss:Type=\"String\">a</Data></Cell>\n");
      builder.append("    <Cell><Data ss:Type=\"String\">b</Data></Cell>\n");
      builder.append("    <Cell><Data ss:Type=\"String\">c</Data></Cell>\n");
      builder.append("   </Row>\n");
      builder.append("   <Row>\n");
      builder.append("    <Cell><Data ss:Type=\"String\">e</Data></Cell>\n");
      builder.append("    <Cell><Data ss:Type=\"String\">d</Data></Cell>\n");
      builder.append("    <Cell><Data ss:Type=\"String\">f</Data></Cell>\n");
      builder.append("   </Row>\n");
      builder.append("  </Table>\n");
      builder.append(" </Worksheet>\n");
   }

   private String getExpectedExcelReport(MockResultsProvider provider) {
      StringBuilder builder = new StringBuilder();
      int index = 0;
      for (IResultsEditorTab tab : provider.getResultsEditorTabs()) {
         addExcelTabXml(builder, tab.getTabName(), ++index);
      }
      builder.append("</Workbook>\n");
      return builder.toString();
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
      List<String[]> rows = new ArrayList<>();
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
         this.tabs = new ArrayList<>();
      }

      @Override
      public String getEditorName() {
         return editorName;
      }

      @Override
      public List<IResultsEditorTab> getResultsEditorTabs() {
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
      public List<XViewerColumn> getTableColumns() {
         List<XViewerColumn> xColumns = new ArrayList<>();
         for (String col : columns) {
            xColumns.add(new XViewerColumn("", col, 0, XViewerAlign.Left, true, SortDataType.String, false, ""));
         }
         return xColumns;
      }

      @Override
      public Collection<IResultsXViewerRow> getTableRows() {
         List<IResultsXViewerRow> xRows = new ArrayList<>();
         for (String[] row : rows) {
            xRows.add(new ResultsXViewerRow(row));
         }
         return xRows;
      }

      @Override
      public Composite createTab(Composite parent, ResultsEditor resultsEditor) {
         return null;
      }

      @Override
      public String getTabName() {
         return tabName;
      }
   }
}
