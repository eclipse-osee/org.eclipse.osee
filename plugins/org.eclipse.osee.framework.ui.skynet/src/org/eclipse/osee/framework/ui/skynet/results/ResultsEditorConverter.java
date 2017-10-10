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

import java.io.ByteArrayOutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ISheetWriter;
import org.eclipse.osee.framework.ui.skynet.results.html.IResultsEditorHtmlTab;
import org.eclipse.osee.framework.ui.skynet.results.table.IResultsEditorTableTab;
import org.eclipse.osee.framework.ui.skynet.results.table.IResultsXViewerRow;
import org.eclipse.osee.framework.ui.skynet.util.TableWriterAdaptor;

/**
 * @author Roberto E. Escobar
 */
public class ResultsEditorConverter {
   private final List<IEditorTabConverter> converters;

   public ResultsEditorConverter() {
      converters = new ArrayList<>();
      converters.add(new HTMLEditorTabConverter());
      converters.add(new ExcelEditorTabConverter());
      converters.add(new MultiTypeEditorTabConverter("PDF"));
      converters.add(new MultiTypeEditorTabConverter("RTF"));
      converters.add(new MultiTypeEditorTabConverter("HTML"));
   }

   private IEditorTabConverter getConverter(String type) {
      IEditorTabConverter toReturn = null;
      for (IEditorTabConverter converter : converters) {
         if (converter.getType().equalsIgnoreCase(type)) {
            toReturn = converter;
            break;
         }
      }
      if (toReturn == null) {
         throw new OseeArgumentException("Conversion to [%s] is not supported", type);
      }
      return toReturn;
   }

   private void checkForNull(String param, Object object) {
      if (object == null) {
         throw new OseeArgumentException("[%s] cannot be null", param);
      }
   }

   public void convert(String type, Writer writer, IResultsEditorProvider provider) {
      checkForNull("Type", type);
      checkForNull("Writer", writer);
      checkForNull("IResultsEditorProvider", provider);

      IEditorTabConverter converter = getConverter(type);

      Collection<IResultsEditorTab> tabs = provider.getResultsEditorTabs();
      for (IResultsEditorTab tab : tabs) {
         converter.canConvert(tab);
      }

      converter.convert(writer, tabs);
   }

   private interface IEditorTabConverter {

      public String getType();

      public void canConvert(IResultsEditorTab tab);

      public void convert(Writer writer, Collection<IResultsEditorTab> tabs);

   }

   private static abstract class AbstractEditorTabConverter implements IEditorTabConverter {
      @Override
      public void canConvert(IResultsEditorTab tab) {
         if (!(tab instanceof IResultsEditorTableTab) && !(tab instanceof IResultsEditorHtmlTab)) {
            throw new OseeArgumentException(
               String.format("%s to type [%s] is not supported", tab.getClass(), getType()));
         }
      }

      protected String[] getColumns(IResultsEditorTableTab tab) throws Exception {
         List<XViewerColumn> columns = tab.getTableColumns();
         String[] columnNames = new String[columns.size()];
         for (int index = 0; index < columns.size(); index++) {
            columnNames[index] = columns.get(index).getName();
         }
         return columnNames;
      }
   }

   private final static class HTMLEditorTabConverter extends AbstractEditorTabConverter {
      private final static String HTML_HEADER =
         "<!DOCTYPE html PUBLIC \"-//W3C//DTD html 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\"><html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\">\n";
      private final static String PAGE_TITLE = "<title>%s</title>\n";
      private final static String STYLE_CSS = "<style type=\"text/css\">\n" + //
         " .blueBox { color: black; background-color:#999999; display: block; padding: 6px; margin-top: 8px; width:95%; cursor:pointer; border: solid;  border-color:black; border-width: thin; text-align: left; vertical-align: middle; }\n" + //
         " .results { color: black; background-color:whitesmoke; display: block; padding: 6px; width:95%; cursor:pointer; border: solid; border-width: thin; text-align: left; vertical-align: middle; }\n" + //
         "</style>\n";
      private final static String JAVASCRIPT = "<script type=\"text/javascript\">\n" + //
         "function opendiv(id){\n" + //
         "var divStyle = document.getElementById(id).style;\n" + //
         "divStyle.display = (divStyle.display=='none') ? 'block':'none';\n" + //
         "}\n";
      private final static String START_HTML = "</script>\n</head>\n<body>\n";
      private final static String END_HTML = "\n</body>\n</html>\n";

      @Override
      public void convert(Writer writer, Collection<IResultsEditorTab> tabs) {
         try {
            writer.write(HTML_HEADER);
            writer.write(String.format(PAGE_TITLE, "Report"));
            writer.write(STYLE_CSS);
            writer.write(String.format(JAVASCRIPT, tabs.size() + 1));
            writer.write(START_HTML);
            writer.write("<div id=\"alldivs\">\n");
            int index = 1;
            for (IResultsEditorTab rawTab : tabs) {
               writer.write(String.format("<a class=\"blueBox\" onclick=\"opendiv('div%s');\">%s</a>\n", index,
                  rawTab.getTabName()));
               writer.write(String.format("<div class=\"results\" id=\"div%s\" style=\"display:none;\">\n", index++));
               if (rawTab instanceof IResultsEditorTableTab) {
                  IResultsEditorTableTab tab = (IResultsEditorTableTab) rawTab;
                  writer.write(AHTML.beginMultiColumnTable(95, 1));
                  String[] columns = getColumns(tab);
                  writer.write(AHTML.addHeaderRowMultiColumnTable(columns));
                  writeRows(writer, tab);
                  writer.write(AHTML.endMultiColumnTable());
               } else if (rawTab instanceof IResultsEditorHtmlTab) {
                  String htmlString = ((IResultsEditorHtmlTab) rawTab).getReportHtml();
                  writer.write(htmlString);
               } else {
                  throw new OseeStateException("%s to type %s is not supported", rawTab.getClass(), getType());
               }
               writer.write("</div>\n");
            }
            writer.write(END_HTML);
         } catch (Exception ex) {
            throw OseeCoreException.wrap(ex);
         }
      }

      private void writeRows(Writer outputWriter, IResultsEditorTableTab tab) throws Exception {
         Collection<IResultsXViewerRow> rows = tab.getTableRows();
         for (IResultsXViewerRow row : rows) {
            outputWriter.write(AHTML.addRowMultiColumnTable(row.values()));
         }
      }

      @Override
      public String getType() {
         return "collapsing_html";
      }
   }

   private final static class ExcelEditorTabConverter extends AbstractEditorTabConverter {

      @Override
      public void canConvert(IResultsEditorTab tab) {
         if (!(tab instanceof IResultsEditorTableTab)) {
            throw new OseeArgumentException("%s to type %s is not supported", tab.getClass(), getType().toUpperCase());
         }
      }

      @Override
      public void convert(Writer writer, Collection<IResultsEditorTab> tabs) {
         try {
            ISheetWriter sheetWriter = new ExcelXmlWriter(writer);
            for (IResultsEditorTab rawTab : tabs) {
               if (rawTab instanceof IResultsEditorTableTab) {
                  IResultsEditorTableTab tab = (IResultsEditorTableTab) rawTab;
                  String[] columns = getColumns(tab);
                  sheetWriter.startSheet(tab.getTabName(), columns.length);
                  sheetWriter.writeRow((Object[]) columns);
                  Collection<IResultsXViewerRow> rows = tab.getTableRows();
                  for (IResultsXViewerRow row : rows) {
                     sheetWriter.writeRow((Object[]) row.values());
                  }
                  sheetWriter.endSheet();
               } else {
                  throw new OseeStateException("%s to type %s is not supported", rawTab.getClass(), getType());
               }
            }
            sheetWriter.endWorkbook();
         } catch (Exception ex) {
            throw OseeCoreException.wrap(ex);
         }
      }

      @Override
      public String getType() {
         return "EXCEL";
      }
   }

   private final static class MultiTypeEditorTabConverter extends AbstractEditorTabConverter {

      private final String type;

      public MultiTypeEditorTabConverter(String type) {
         this.type = type.toUpperCase();
      }

      @Override
      public void canConvert(IResultsEditorTab tab) {
         if (!(tab instanceof IResultsEditorTableTab)) {
            throw new OseeArgumentException("%s to type %s is not supported", tab.getClass(), getType().toUpperCase());
         }
      }

      @Override
      public void convert(Writer writer, Collection<IResultsEditorTab> tabs) {
         try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            TableWriterAdaptor writerAdaptor = new TableWriterAdaptor(type.toLowerCase(), outputStream);
            writerAdaptor.writeTitle("Report");
            writerAdaptor.openDocument();

            for (IResultsEditorTab rawTab : tabs) {
               if (rawTab instanceof IResultsEditorTableTab) {
                  IResultsEditorTableTab tab = (IResultsEditorTableTab) rawTab;

                  writerAdaptor.writeTitle(tab.getTabName());

                  String[] columns = getColumns(tab);
                  writerAdaptor.writeHeader(columns);

                  Collection<IResultsXViewerRow> rows = tab.getTableRows();
                  for (IResultsXViewerRow row : rows) {
                     writerAdaptor.writeRow(row.values());
                  }
                  writerAdaptor.writeDocument();
               } else {
                  throw new OseeStateException("%s to type %s is not supported", rawTab.getClass(), getType());
               }
            }
            writerAdaptor.close();
            writer.write(outputStream.toString("ISO-8859-1"));
         } catch (Exception ex) {
            throw OseeCoreException.wrap(ex);
         }
      }

      @Override
      public String getType() {
         return type;
      }
   }
}