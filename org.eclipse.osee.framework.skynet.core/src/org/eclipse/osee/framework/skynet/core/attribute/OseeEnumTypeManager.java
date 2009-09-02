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
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.skynet.core.types.OseeTypeManager;
import org.eclipse.osee.framework.skynet.core.types.OseeTypeCache.OseeEnumTypeCache;

/**
 * @author Roberto E. Escobar
 */
public class OseeEnumTypeManager {

   private OseeEnumTypeManager() {
   }

   public static OseeEnumType getType(int enumTypeId, boolean includeDeleted) throws OseeCoreException {
      OseeEnumType oseeEnumType = OseeTypeManager.getCache().getEnumTypeData().getTypeById(enumTypeId);
      if (oseeEnumType == null || !includeDeleted && oseeEnumType.getModificationType().isDeleted()) {
         throw new OseeTypeDoesNotExist(String.format("Osee Enum Type with id:[%s] does not exist.", enumTypeId));
      }
      return oseeEnumType;
   }

   public static OseeEnumType getUniqueType(String enumTypeName, boolean includeDeleted) throws OseeCoreException {
      OseeEnumType itemsFound = OseeTypeManager.getCache().getEnumTypeData().getTypeByName(enumTypeName);
      boolean wasFound = false;
      if (itemsFound != null) {
         if (includeDeleted || !itemsFound.getModificationType().isDeleted()) {
            wasFound = true;
         }
      }
      if (!wasFound) {
         throw new OseeTypeDoesNotExist(
               String.format("OSEE enum types matching [%s] name does not exist", enumTypeName));
      }
      return itemsFound;
   }

   public static Collection<OseeEnumType> getAllTypes(boolean includeDeleted) throws OseeCoreException {
      List<OseeEnumType> items = new ArrayList<OseeEnumType>();
      for (OseeEnumType types : OseeTypeManager.getCache().getEnumTypeData().getAllTypes()) {
         if (includeDeleted || !types.getModificationType().isDeleted()) {
            items.add(types);
         }
      }
      return items;
   }

   public static Collection<String> getAllTypeNames(boolean includeDeleted) throws OseeCoreException {
      List<String> items = new ArrayList<String>();
      for (OseeEnumType types : getAllTypes(includeDeleted)) {
         items.add(types.getName());
      }
      return items;
   }

   public static boolean typeExist(String enumTypeName, boolean includeDeleted) throws OseeCoreException {
      OseeEnumType itemsFound = OseeTypeManager.getCache().getEnumTypeData().getTypeByName(enumTypeName);
      boolean wasFound = false;
      if (itemsFound != null) {
         if (includeDeleted || !itemsFound.getModificationType().isDeleted()) {
            wasFound = true;
         }
      }
      return wasFound;
   }

   public static OseeEnumType getType(int enumTypeId) throws OseeCoreException {
      return getType(enumTypeId, false);
   }

   public static OseeEnumType getUniqueType(String enumTypeName) throws OseeCoreException {
      return getUniqueType(enumTypeName, false);
   }

   public static Collection<String> getAllTypeNames() throws OseeCoreException {
      return getAllTypeNames(false);
   }

   public static Collection<OseeEnumType> getAllTypes() throws OseeCoreException {
      return getAllTypes(false);
   }

   public static boolean typeExist(String enumTypeName) throws OseeCoreException {
      return typeExist(enumTypeName, false);
   }

   public static int getDefaultEnumTypeId() {
      return -1;
   }

   public static OseeEnumEntry createEnumEntry(String guid, String name, int ordinal) throws OseeCoreException {
      return OseeTypeManager.getTypeFactory().createEnumEntry(guid, name, ordinal, OseeTypeManager.getCache());
   }

   public static OseeEnumType createEnumType(String guid, String enumTypeName) throws OseeCoreException {
      OseeEnumTypeCache dataCache = OseeTypeManager.getCache().getEnumTypeData();
      OseeEnumType oseeEnumType = dataCache.getTypeByGuid(guid);
      if (oseeEnumType == null) {
         oseeEnumType = OseeTypeManager.getTypeFactory().createEnumType(guid, enumTypeName, OseeTypeManager.getCache());
      } else {
         dataCache.decacheType(oseeEnumType);
         oseeEnumType.setName(enumTypeName);
      }
      dataCache.cacheType(oseeEnumType);
      return oseeEnumType;
   }

   public static void persist() throws OseeCoreException {
      OseeTypeManager.getCache().getEnumTypeData().storeAllModified();
   }

   //   public static OseeEnumType createEnumTypeFromXml(String attributeTypeName, String xmlDefinition) throws OseeCoreException {
   //      List<Pair<String, Integer>> entries = new ArrayList<Pair<String, Integer>>();
   //      String enumTypeName = "";
   //
   //      if (!Strings.isValid(xmlDefinition)) {
   //         throw new OseeArgumentException("The enum xml definition must not be null or empty");
   //      }
   //
   //      Document document;
   //      try {
   //         document = Jaxp.readXmlDocument(xmlDefinition);
   //      } catch (Exception ex) {
   //         throw new OseeWrappedException(ex);
   //      }
   //      enumTypeName = attributeTypeName;
   //      Element choicesElement = document.getDocumentElement();
   //      NodeList enumerations = choicesElement.getChildNodes();
   //      Set<String> choices = new LinkedHashSet<String>();
   //
   //      for (int i = 0; i < enumerations.getLength(); i++) {
   //         Node node = enumerations.item(i);
   //         if (node.getNodeName().equalsIgnoreCase("Enum")) {
   //            choices.add(node.getTextContent());
   //         } else {
   //            throw new OseeArgumentException("Validity Xml not of excepted enum format");
   //         }
   //      }
   //
   //      int ordinal = 0;
   //      for (String choice : choices) {
   //         entries.add(new Pair<String, Integer>(choice, ordinal++));
   //      }
   //      OseeEnumType enumType =
   //            OseeTypeManager.getTypeFactory().createEnumType(GUID.create(), enumTypeName, OseeTypeManager.getCache());
   //      enumType.addEntries(entries);
   //      return enumType;
   //   }
}