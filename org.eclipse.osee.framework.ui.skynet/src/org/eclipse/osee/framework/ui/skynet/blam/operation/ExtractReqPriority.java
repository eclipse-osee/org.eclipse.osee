/*
 * Created on Dec 8, 2007
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelSaxHandler;
import org.eclipse.osee.framework.jdk.core.util.io.xml.RowProcessor;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Ryan D. Brooks
 */
public class ExtractReqPriority implements RowProcessor {
   private HashMap<String, String> reqPriorities;

   public ExtractReqPriority(String excelMlPath) throws UnsupportedEncodingException, FileNotFoundException, IOException, SAXException {
      this.reqPriorities = new HashMap<String, String>();

      File file = new File(excelMlPath);

      XMLReader xmlReader = XMLReaderFactory.createXMLReader();
      xmlReader.setContentHandler(new ExcelSaxHandler(this, true));
      xmlReader.parse(new InputSource(new InputStreamReader(new FileInputStream(file), "UTF-8")));
   }

   public HashMap<String, String> getReqPriorities() {
      return reqPriorities;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.jdk.core.util.io.xml.RowProcessor#detectedRowAndColumnCounts(int, int)
    */
   public void detectedRowAndColumnCounts(int rowCount, int columnCount) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.jdk.core.util.io.xml.RowProcessor#foundStartOfWorksheet(java.lang.String)
    */
   public void foundStartOfWorksheet(String sheetName) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.jdk.core.util.io.xml.RowProcessor#processCommentRow(java.lang.String[])
    */
   public void processCommentRow(String[] row) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.jdk.core.util.io.xml.RowProcessor#processEmptyRow()
    */
   public void processEmptyRow() {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.jdk.core.util.io.xml.RowProcessor#processHeaderRow(java.lang.String[])
    */
   public void processHeaderRow(String[] row) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.jdk.core.util.io.xml.RowProcessor#processRow(java.lang.String[])
    */
   public void processRow(String[] row) {
      // pick the highest priority specified in the workbook (in case there are multiple priorities for the same item)
      if (row[1] != null) {
         String priority = reqPriorities.get(row[1]);
         if (priority != null) {
a             if (priority.compareTo(row[0]) > 0) {
               return;
            }
         }
         reqPriorities.put(row[1], row[0]);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.jdk.core.util.io.xml.RowProcessor#reachedEndOfWorksheet()
    */
   public void reachedEndOfWorksheet() {
   }
}