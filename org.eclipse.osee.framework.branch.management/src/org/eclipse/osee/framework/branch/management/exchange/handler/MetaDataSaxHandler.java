/*
 * Created on Aug 20, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.branch.management.exchange.handler;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.branch.management.exchange.ExportImportXml;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.xml.AbstractSaxHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Roberto E. Escobar
 */
public class MetaDataSaxHandler extends AbstractSaxHandler {

   private Map<String, MetaData> metadataMap;
   private MetaData currentMetadata;

   public MetaDataSaxHandler() {
      this.metadataMap = new HashMap<String, MetaData>();
   }

   public MetaData getMetadata(String source) {
      return metadataMap.get(source);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.jdk.core.util.io.xml.AbstractSaxHandler#startElementFound(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
    */
   @Override
   public void startElementFound(String uri, String localName, String name, Attributes attributes) throws SAXException {
      try {
         if (localName.equalsIgnoreCase(ExportImportXml.METADATA)) {
            handleMetaData(attributes);
         } else if (localName.equalsIgnoreCase(ExportImportXml.TABLE)) {
            handleTable(attributes);
         } else if (localName.equalsIgnoreCase(ExportImportXml.COLUMN)) {
            handleColumn(attributes);
         }
      } catch (Exception ex) {
         throw new IllegalStateException(ex);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.jdk.core.util.io.xml.AbstractSaxHandler#endElementFound(java.lang.String, java.lang.String, java.lang.String)
    */
   @Override
   public void endElementFound(String uri, String localName, String name) throws SAXException {
      try {
         if (localName.equalsIgnoreCase(ExportImportXml.METADATA)) {
            finishMetaData();
         } else if (localName.equalsIgnoreCase(ExportImportXml.TABLE)) {
            finishTable();
         } else if (localName.equalsIgnoreCase(ExportImportXml.COLUMN)) {
            finishColumn();
         }
      } catch (Exception ex) {
         throw new IllegalStateException(ex);
      }
   }

   private void finishMetaData() {
      // Do Nothing;
   }

   private void finishColumn() {
      // Do Nothing;
   }

   private void finishTable() {
      this.currentMetadata = null;
   }

   private void handleMetaData(Attributes attributes) {
      this.metadataMap.clear();
   }

   private void handleTable(Attributes attributes) {
      String tableName = attributes.getValue(ExportImportXml.TABLE_NAME);
      if (Strings.isValid(tableName)) {
         this.currentMetadata = new MetaData(tableName);
         this.metadataMap.put(tableName, currentMetadata);
      } else {
         this.currentMetadata = null;
      }
   }

   private void handleColumn(Attributes attributes) {
      String columnName = attributes.getValue(ExportImportXml.ID);
      String typeName = attributes.getValue(ExportImportXml.TYPE);
      SQL3DataType sql3DataType = SQL3DataType.valueOf(typeName);
      this.currentMetadata.addColumn(columnName, sql3DataType);
   }

}
