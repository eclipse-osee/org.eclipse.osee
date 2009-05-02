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
package org.eclipse.osee.framework.skynet.core.attribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.core.SequenceManager;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.db.connection.exception.OseeWrappedException;
import org.eclipse.osee.framework.jdk.core.type.ObjectPair;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Roberto E. Escobar
 */
public class OseeEnumTypeManager {

   private static final String QUERY_ENUM =
         "select oet.enum_type_name, oetd.* from osee_enum_type oet, osee_enum_type_def oetd order by oetd.ordinal";

   private static final String INSERT_ENUM_TYPE =
         "insert into osee_enum_type (ENUM_TYPE_ID, ENUM_TYPE_NAME) values (?,?)";

   private static final String INSERT_ENUM_TYPE_DEF =
         "insert into osee_enum_type_def (ENUM_TYPE_ID, NAME, ORDINAL) values (?,?,?)";

   private static final OseeEnumTypeManager instance = new OseeEnumTypeManager();

   private final Map<String, OseeEnumType> enumTypeByNameMap;
   private final Map<Integer, OseeEnumType> enumTypeByIdMap;

   private OseeEnumTypeManager() {
      enumTypeByNameMap = new HashMap<String, OseeEnumType>();
      enumTypeByIdMap = new HashMap<Integer, OseeEnumType>();
   }

   public static OseeEnumType getType(int enumTypeId) throws OseeDataStoreException, OseeTypeDoesNotExist {
      instance.checkLoaded();
      OseeEnumType oseeEnumType = instance.enumTypeByIdMap.get(enumTypeId);
      if (oseeEnumType == null) {
         throw new OseeTypeDoesNotExist(String.format("Osee Enum Type with id:[%s] does not exist.", enumTypeId));
      }
      return oseeEnumType;
   }

   public static OseeEnumType getType(String enumTypeName) throws OseeDataStoreException, OseeTypeDoesNotExist {
      instance.checkLoaded();
      OseeEnumType oseeEnumType = instance.enumTypeByNameMap.get(enumTypeName);
      if (oseeEnumType == null) {
         throw new OseeTypeDoesNotExist(String.format("Osee Enum Type with name:[%s] does not exist.", enumTypeName));
      }
      return oseeEnumType;
   }

   public static boolean typeExist(String enumTypeName) throws OseeDataStoreException {
      instance.checkLoaded();
      return instance.enumTypeByNameMap.get(enumTypeName) != null;
   }

   private static void checkEnumTypeName(String enumTypeName) throws OseeCoreException {
      if (!Strings.isValid(enumTypeName)) throw new OseeArgumentException("Osee Enum Type Name cannot be null.");
   }

   private static void checkEntryIntegrity(String enumTypeName, List<ObjectPair<String, Integer>> entries) throws OseeCoreException {
      if (entries == null) throw new OseeArgumentException(String.format("Osee Enum Type [%s] had null entries",
            enumTypeName));

      //      if (entries.size() <= 0) throw new OseeArgumentException(String.format("Osee Enum Type [%s] had 0 entries",
      //            enumTypeName));
      Map<String, Integer> values = new HashMap<String, Integer>();
      for (ObjectPair<String, Integer> entry : entries) {
         String name = entry.object1;
         int ordinal = entry.object2;
         if (!Strings.isValid(name)) throw new OseeArgumentException("Enum entry name cannot be null");
         if (ordinal < 0) throw new OseeArgumentException("Enum entry ordinal cannot be of negative value");
         if (values.containsKey(name)) throw new OseeArgumentException(String.format(
               "Unique enum entry name violation - [%s] already exists.", name));
         if (values.containsValue(ordinal)) throw new OseeArgumentException(String.format(
               "Unique enum entry ordinal violation - [%s] already exists.", ordinal));
         values.put(name, ordinal);
      }
   }

   public static OseeEnumType createEnumType(String enumTypeName, List<ObjectPair<String, Integer>> entries) throws OseeCoreException {
      checkEnumTypeName(enumTypeName);
      checkEntryIntegrity(enumTypeName, entries);
      if (typeExist(enumTypeName)) {
         return getType(enumTypeName);
      }
      int oseeEnumTypeId = SequenceManager.getNextOseeEnumTypeId();
      OseeEnumType oseeEnumType = new OseeEnumType(oseeEnumTypeId, enumTypeName);
      ConnectionHandler.runPreparedUpdate(INSERT_ENUM_TYPE, oseeEnumType.getEnumTypeId(),
            oseeEnumType.getEnumTypeName());
      List<Object[]> data = new ArrayList<Object[]>();
      for (ObjectPair<String, Integer> entry : entries) {
         oseeEnumType.addEnum(entry.object1, entry.object2);
         data.add(new Object[] {oseeEnumTypeId, entry.object1, entry.object2});
      }
      if (!data.isEmpty()) {
         ConnectionHandler.runBatchUpdate(INSERT_ENUM_TYPE_DEF, data);
      }
      instance.cache(oseeEnumType);
      return oseeEnumType;
   }

   public static OseeEnumType createEnumTypeFromXml(String attributeTypeName, String validityXml) throws OseeCoreException {
      OseeEnumType oseeEnumType = null;
      try {
         Set<String> choices = new HashSet<String>();
         String enumTypeName = "";
         if (validityXml == null) {
            validityXml = "";
            enumTypeName = "Osee Default Enum";
         } else {
            Document document = Jaxp.readXmlDocument(validityXml);
            enumTypeName = attributeTypeName;
            Element choicesElement = document.getDocumentElement();
            NodeList enumerations = choicesElement.getChildNodes();
            for (int i = 0; i < enumerations.getLength(); i++) {
               Node node = enumerations.item(i);
               if (node.getNodeName().equals("Enum")) {
                  choices.add(node.getTextContent());
               } else {
                  choices.add(node.getNodeName());
               }
            }
         }
         List<ObjectPair<String, Integer>> entries = new ArrayList<ObjectPair<String, Integer>>();
         int ordinal = 0;
         for (String choice : choices) {
            entries.add(new ObjectPair<String, Integer>(choice, ordinal));
            ordinal++;
         }
         oseeEnumType = createEnumType(enumTypeName, entries);
      } catch (Exception ex) {
         throw new OseeWrappedException(ex);
      }
      return oseeEnumType;
   }

   private void cache(OseeEnumType oseeEnumType) {
      enumTypeByNameMap.put(oseeEnumType.getEnumTypeName(), oseeEnumType);
      enumTypeByIdMap.put(oseeEnumType.getEnumTypeId(), oseeEnumType);
   }

   private synchronized void checkLoaded() throws OseeDataStoreException {
      if (enumTypeByNameMap.isEmpty() && enumTypeByIdMap.isEmpty()) {
         ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
         try {
            chStmt.runPreparedQuery(QUERY_ENUM);
            OseeEnumType oseeEnumType = null;
            int lastEnumTypeId = -1;
            while (chStmt.next()) {
               try {
                  int currentEnumTypeId = chStmt.getInt("enum_type_id");
                  if (lastEnumTypeId != currentEnumTypeId) {
                     oseeEnumType = new OseeEnumType(currentEnumTypeId, chStmt.getString("enum_type_name"));
                     cache(oseeEnumType);
                     lastEnumTypeId = currentEnumTypeId;
                  }
                  oseeEnumType.addEnum(chStmt.getString("name"), chStmt.getInt("ordinal"));
               } catch (OseeCoreException ex) {
                  OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
               }
            }
         } finally {
            chStmt.close();
         }
      }
   }
}