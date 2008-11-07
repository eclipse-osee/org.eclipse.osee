package org.eclipse.osee.framework.db.connection;

import java.io.IOException;
import java.io.InputStream;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeWrappedException;
import org.eclipse.osee.framework.db.connection.internal.InternalActivator;
import org.eclipse.osee.framework.db.connection.internal.parser.DbConfigParser;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DatabaseInfoManager {

   private DatabaseInfoManager() {
   }

   public static IDatabaseInfo getDefault() throws OseeDataStoreException {
      try {
         return InternalActivator.getConnectionInfos().getSelectedDatabaseInfo();
      } catch (InterruptedException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   public static IDatabaseInfo getDataStoreById(String id) throws OseeDataStoreException {
      try {
         return InternalActivator.getConnectionInfos().getDatabaseInfo(id);
      } catch (InterruptedException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   public static IDatabaseInfo[] readFromXml(InputStream inputStream) throws OseeCoreException {
      try {
         Document document = Jaxp.readXmlDocument(inputStream);
         Element rootElement = document.getDocumentElement();
         return DbConfigParser.parse(rootElement);
      } catch (Exception ex) {
         throw new OseeWrappedException(ex);
      } finally {
         if (inputStream != null) {
            try {
               inputStream.close();
            } catch (IOException ex) {
               throw new OseeWrappedException(ex);
            }
         }
      }
   }

}
