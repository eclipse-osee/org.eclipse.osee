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
package org.eclipse.osee.framework.ui.data.model.editor.input;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.core.runtime.IPath;
import org.eclipse.osee.framework.core.data.OseeInfo;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.CompositeKey;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.ObjectPair;
import org.eclipse.osee.framework.ui.data.model.editor.model.DataTypeCache;
import org.eclipse.osee.framework.ui.data.model.editor.model.DataTypeSource;

/**
 * @author Roberto E. Escobar
 */
public class OseeDataTypeFactory {
   private static OseeDataTypeFactory instance = new OseeDataTypeFactory();

   private List<IOseeDataTypeHandler> fileHandlers;

   private OseeDataTypeFactory() {
      if (fileHandlers == null) {
         fileHandlers = new ArrayList<IOseeDataTypeHandler>();
         fileHandlers.add(new ExcelXmlODMFileHandler());
      }
   }

   public static DataTypeSource loadFromFile(IPath file) throws OseeCoreException {
      DataTypeSource dataTypeSource = null;
      for (IOseeDataTypeHandler handler : instance.fileHandlers) {
         if (handler.isValid(file)) {
            dataTypeSource = handler.toODMDataTypeSource(file);
            if (dataTypeSource != null) {
               break;
            }
         }
      }
      return dataTypeSource;
   }

   public static void addTypesFromDataStore(DataTypeCache cache) throws OseeCoreException {
      String sourceId = OseeInfo.getValue("osee.db.guid");
      DataTypeSource dataTypeSource = new DataTypeSource(sourceId);
      dataTypeSource.addAll(OseeDataTypeDatastore.getArtifactDataTypes());
      dataTypeSource.addAll(OseeDataTypeDatastore.getAttributeTypes());
      dataTypeSource.addAll(OseeDataTypeDatastore.getRelationDataTypes());
      Map<String, String> entries = OseeDataTypeDatastore.getArtifactToAttributeEntries();
      for (Entry<String, String> entry : entries.entrySet()) {
         dataTypeSource.addAttributeToArtifact(entry.getKey(), entry.getValue());
      }
      CompositeKeyHashMap<String, String, ObjectPair<Integer, Integer>> relationsMap =
            OseeDataTypeDatastore.getArtifactToRelationEntries();

      for (CompositeKey<String, String> key : relationsMap.keySet()) {
         dataTypeSource.addRelationToArtifact(key.getKey1(), key.getKey2());
      }
      cache.addDataTypeSource(dataTypeSource);
   }
}
