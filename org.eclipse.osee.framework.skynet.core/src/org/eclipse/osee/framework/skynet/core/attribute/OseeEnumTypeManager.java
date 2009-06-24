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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbTransaction;
import org.eclipse.osee.framework.db.connection.OseeConnection;
import org.eclipse.osee.framework.db.connection.core.SequenceManager;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeMultipleEnumTypesExist;
import org.eclipse.osee.framework.db.connection.exception.OseeStateException;
import org.eclipse.osee.framework.db.connection.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.db.connection.exception.OseeWrappedException;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.ObjectPair;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.attribute.OseeEnumType.OseeEnumEntry;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Roberto E. Escobar
 */
public class OseeEnumTypeManager {
   private static final String QUERY_ENUM =
         "select oet.enum_type_name, oetd.* from osee_enum_type oet, osee_enum_type_def oetd where oet.enum_type_id = oetd.enum_type_id order by oetd.enum_type_id, oetd.ordinal";

   private static final String INSERT_ENUM_TYPE =
         "insert into osee_enum_type (ENUM_TYPE_ID, ENUM_TYPE_NAME) values (?,?)";

   private static final String INSERT_ENUM_TYPE_DEF =
         "insert into osee_enum_type_def (ENUM_TYPE_ID, NAME, ORDINAL) values (?,?,?)";

   private static final String DELETE_ENUM_TYPE_ENTRIES = "delete from osee_enum_type_def where enum_type_id = ?";

   private static final String DELETE_ENUM_TYPE = "delete from osee_enum_type oet where enum_type_id = ?";

   private static final OseeEnumTypeManager instance = new OseeEnumTypeManager();

   private final HashCollection<String, OseeEnumType> enumTypeByNameMap;
   private final Map<Integer, OseeEnumType> enumTypeByIdMap;

   private OseeEnumTypeManager() {
      enumTypeByNameMap = new HashCollection<String, OseeEnumType>();
      enumTypeByIdMap = new HashMap<Integer, OseeEnumType>();
   }

   public static OseeEnumType getType(int enumTypeId, boolean includeDeleted) throws OseeDataStoreException, OseeTypeDoesNotExist {
      instance.checkLoaded();
      OseeEnumType oseeEnumType = instance.enumTypeByIdMap.get(enumTypeId);
      if (oseeEnumType == null || !includeDeleted && oseeEnumType.isDeleted()) {
         throw new OseeTypeDoesNotExist(String.format("Osee Enum Type with id:[%s] does not exist.", enumTypeId));
      }
      return oseeEnumType;
   }

   public static OseeEnumType getUniqueType(String enumTypeName, boolean includeDeleted) throws OseeDataStoreException, OseeTypeDoesNotExist, OseeMultipleEnumTypesExist {
      Collection<OseeEnumType> results = getTypes(enumTypeName, includeDeleted);
      if (results.size() > 1) {
         throw new OseeMultipleEnumTypesExist(String.format("Found multiple OSEE enum types matching [%s] name",
               enumTypeName));
      }
      return results.iterator().next();
   }

   public static Collection<OseeEnumType> getTypes(String enumTypeName, boolean includeDeleted) throws OseeDataStoreException, OseeTypeDoesNotExist {
      Collection<OseeEnumType> toReturn = getTypesAllowEmpty(enumTypeName, includeDeleted);
      if (toReturn.isEmpty()) {
         throw new OseeTypeDoesNotExist(String.format("Osee Enum Type with name:[%s] does not exist.", enumTypeName));
      }
      return toReturn;
   }

   public static Collection<OseeEnumType> getTypesAllowEmpty(String enumTypeName, boolean includeDeleted) throws OseeDataStoreException {
      instance.checkLoaded();
      List<OseeEnumType> toReturn = new ArrayList<OseeEnumType>();
      Collection<OseeEnumType> itemsFound = instance.enumTypeByNameMap.getValues(enumTypeName);
      if (itemsFound != null) {
         for (OseeEnumType oseeEnumType : itemsFound) {
            if (includeDeleted || !oseeEnumType.isDeleted()) {
               toReturn.add(oseeEnumType);
            }
         }
      }
      return toReturn;
   }

   public static Collection<OseeEnumType> getAllTypes(boolean includeDeleted) throws OseeDataStoreException {
      instance.checkLoaded();
      List<OseeEnumType> items = new ArrayList<OseeEnumType>();
      for (OseeEnumType types : instance.enumTypeByIdMap.values()) {
         if (includeDeleted || !types.isDeleted()) {
            items.add(types);
         }
      }
      return items;
   }

   public static Collection<String> getAllTypeNames(boolean includeDeleted) throws OseeDataStoreException {
      List<String> items = new ArrayList<String>();
      for (OseeEnumType types : getAllTypes(includeDeleted)) {
         items.add(types.getEnumTypeName());
      }
      return items;
   }

   public static boolean typeExist(String enumTypeName, boolean includeDeleted) throws OseeDataStoreException {
      return !getTypesAllowEmpty(enumTypeName, includeDeleted).isEmpty();
   }

   public static OseeEnumType getType(int enumTypeId) throws OseeDataStoreException, OseeTypeDoesNotExist {
      return getType(enumTypeId, false);
   }

   public static OseeEnumType getUniqueType(String enumTypeName) throws OseeDataStoreException, OseeTypeDoesNotExist, OseeMultipleEnumTypesExist {
      return getUniqueType(enumTypeName, false);
   }

   public static Collection<String> getAllTypeNames() throws OseeDataStoreException {
      return getAllTypeNames(false);
   }

   public static Collection<OseeEnumType> getAllTypes() throws OseeDataStoreException {
      return getAllTypes(false);
   }

   public static boolean typeExist(String enumTypeName) throws OseeDataStoreException {
      return typeExist(enumTypeName, false);
   }

   private static void checkNull(Object value) throws OseeCoreException {
      if (value == null) {
         throw new OseeArgumentException("Object cannot be null.");
      }
   }

   private static void checkEnumTypeName(String enumTypeName) throws OseeCoreException {
      if (!Strings.isValid(enumTypeName)) {
         throw new OseeArgumentException("Osee Enum Type Name cannot be null.");
      }
   }

   private static void checkEntryIntegrity(String enumTypeName, List<ObjectPair<String, Integer>> entries) throws OseeCoreException {
      if (entries == null) {
         throw new OseeArgumentException(String.format("Osee Enum Type [%s] had null entries", enumTypeName));
      }

      //      if (entries.size() <= 0) throw new OseeArgumentException(String.format("Osee Enum Type [%s] had 0 entries",
      //            enumTypeName));
      Map<String, Integer> values = new HashMap<String, Integer>();
      for (ObjectPair<String, Integer> entry : entries) {
         String name = entry.object1;
         int ordinal = entry.object2;
         if (!Strings.isValid(name)) {
            throw new OseeArgumentException("Enum entry name cannot be null");
         }
         if (ordinal < 0) {
            throw new OseeArgumentException("Enum entry ordinal cannot be of negative value");
         }
         if (values.containsKey(name)) {
            throw new OseeArgumentException(String.format("Unique enum entry name violation - [%s] already exists.",
                  name));
         }
         if (values.containsValue(ordinal)) {
            throw new OseeArgumentException(String.format("Unique enum entry ordinal violation - [%s] already exists.",
                  ordinal));
         }
         values.put(name, ordinal);
      }
   }

   public static int getDefaultEnumTypeId() throws OseeCoreException {
      return -1;
   }

   public static OseeEnumType createEnumType(String enumTypeName, List<ObjectPair<String, Integer>> entries) throws OseeCoreException {
      checkEnumTypeName(enumTypeName);
      checkEntryIntegrity(enumTypeName, entries);

      if (typeExist(enumTypeName)) {
         return getUniqueType(enumTypeName);
      }

      int oseeEnumTypeId = SequenceManager.getNextOseeEnumTypeId();
      OseeEnumType oseeEnumType = new OseeEnumType(oseeEnumTypeId, enumTypeName);
      ConnectionHandler.runPreparedUpdate(INSERT_ENUM_TYPE, oseeEnumType.getEnumTypeId(),
            oseeEnumType.getEnumTypeName());

      addEntries(oseeEnumType, entries);
      return oseeEnumType;
   }

   public static OseeEnumType createEnumTypeFromXml(String attributeTypeName, String xmlDefinition) throws OseeCoreException {
      List<ObjectPair<String, Integer>> entries = new ArrayList<ObjectPair<String, Integer>>();
      String enumTypeName = "";

      if (!Strings.isValid(xmlDefinition)) {
         throw new OseeArgumentException("The enum xml definition must not be null or empty");
      }

      Document document;
      try {
         document = Jaxp.readXmlDocument(xmlDefinition);
      } catch (Exception ex) {
         throw new OseeWrappedException(ex);
      }
      enumTypeName = attributeTypeName;
      Element choicesElement = document.getDocumentElement();
      NodeList enumerations = choicesElement.getChildNodes();
      Set<String> choices = new LinkedHashSet<String>();

      for (int i = 0; i < enumerations.getLength(); i++) {
         Node node = enumerations.item(i);
         if (node.getNodeName().equalsIgnoreCase("Enum")) {
            choices.add(node.getTextContent());
         } else {
            throw new OseeArgumentException("Validity Xml not of excepted enum format");
         }
      }

      int ordinal = 0;
      for (String choice : choices) {
         entries.add(new ObjectPair<String, Integer>(choice, ordinal++));
      }

      return createEnumType(enumTypeName, entries);
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
                  oseeEnumType.internalAddEnum(chStmt.getString("name"), chStmt.getInt("ordinal"));
               } catch (OseeCoreException ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
               }
            }
         } finally {
            chStmt.close();
         }
      }
   }

   public static void deleteEnumType(OseeEnumType typeToDelete) throws OseeCoreException {
      boolean isInUse = false;
      for (AttributeType attrType : AttributeTypeManager.getAllTypes()) {
         if (typeToDelete.getEnumTypeId() == attrType.getOseeEnumTypeId()) {
            isInUse = true;
         }
      }
      if (isInUse) {
         throw new OseeStateException(String.format(
               "Osee Enum Type: [%s] with id: [%s] is in use by enumerated attributes", typeToDelete.getEnumTypeName(),
               typeToDelete.getEnumTypeId()));
      }
      ConnectionHandler.runPreparedUpdate(DELETE_ENUM_TYPE, typeToDelete.getEnumTypeId());
      typeToDelete.internalSetDeleted(true);
      //  TODO signal to other clients - Event here
   }

   public static void removeEntries(final OseeEnumType enumType, final OseeEnumEntry... entries) throws OseeCoreException {
      checkNull(entries);
      if (entries.length > 0) {
         final List<OseeEnumEntry> itemsToRemove = Arrays.asList(entries);
         final List<ObjectPair<String, Integer>> newEntries = new ArrayList<ObjectPair<String, Integer>>();
         for (OseeEnumEntry entry : enumType.values()) {
            if (!itemsToRemove.contains(entry)) {
               newEntries.add(entry.asObjectPair());
            }
         }
         UpdateEnumTx updateEnumTx = new UpdateEnumTx(enumType, newEntries);
         updateEnumTx.execute();

         enumType.internalRemoveEnums(entries);
         // TODO Signal to other clients - Event here 
      }
   }

   public static void addEntries(final OseeEnumType oseeEnumType, final ObjectPair<String, Integer>... entries) throws OseeCoreException {
      addEntries(oseeEnumType, Collections.getAggregate(entries));
   }

   public static void addEntries(final OseeEnumType oseeEnumType, final List<ObjectPair<String, Integer>> entries) throws OseeCoreException {
      checkNull(oseeEnumType);
      final List<ObjectPair<String, Integer>> newEntries = getCombinedEntries(oseeEnumType, entries);

      UpdateEnumTx updateEnumTx = new UpdateEnumTx(oseeEnumType, newEntries);
      updateEnumTx.execute();

      boolean wasCreated = false;
      if (!instance.enumTypeByIdMap.containsKey(oseeEnumType.getEnumTypeId())) {
         instance.cache(oseeEnumType);
         wasCreated = true;
      }
      for (ObjectPair<String, Integer> entry : entries) {
         oseeEnumType.internalAddEnum(entry);
      }
      // TODO Signal to other clients - Event here
      if (wasCreated) {
         // TODO Signal newly created - Event here
      }
   }

   private static List<ObjectPair<String, Integer>> getCombinedEntries(final OseeEnumType enumType, final List<ObjectPair<String, Integer>> entries) {
      final List<ObjectPair<String, Integer>> combinedList = new ArrayList<ObjectPair<String, Integer>>();
      if (entries != null) {
         combinedList.addAll(entries);
      }
      for (OseeEnumEntry entry : enumType.values()) {
         combinedList.add(entry.asObjectPair());
      }
      return combinedList;
   }

   private static final class UpdateEnumTx extends DbTransaction {
      private final List<ObjectPair<String, Integer>> entries;
      private final OseeEnumType enumType;

      public UpdateEnumTx(final OseeEnumType enumType, final List<ObjectPair<String, Integer>> entries) throws OseeCoreException {
         super();
         this.enumType = enumType;
         this.entries = entries;
      }

      @Override
      protected void handleTxWork(OseeConnection connection) throws OseeCoreException {
         checkNull(enumType);
         checkEntryIntegrity(enumType.getEnumTypeName(), entries);

         Integer oseeEnumTypeId = enumType.getEnumTypeId();
         List<Object[]> data = new ArrayList<Object[]>();
         for (ObjectPair<String, Integer> entry : entries) {
            data.add(new Object[] {oseeEnumTypeId, entry.object1, entry.object2});
         }
         ConnectionHandler.runPreparedUpdate(connection, DELETE_ENUM_TYPE_ENTRIES, oseeEnumTypeId);
         if (!data.isEmpty()) {
            ConnectionHandler.runBatchUpdate(connection, INSERT_ENUM_TYPE_DEF, data);
         }
      }
   };
}