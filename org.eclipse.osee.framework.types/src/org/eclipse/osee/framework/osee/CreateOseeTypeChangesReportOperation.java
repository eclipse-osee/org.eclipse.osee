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
package org.eclipse.osee.framework.osee;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.cache.ArtifactTypeCache;
import org.eclipse.osee.framework.core.data.TableData;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.AbstractOseeType;
import org.eclipse.osee.framework.core.model.ArtifactType;
import org.eclipse.osee.framework.core.model.AttributeType;
import org.eclipse.osee.framework.core.model.OseeEnumEntry;
import org.eclipse.osee.framework.core.model.OseeEnumType;
import org.eclipse.osee.framework.core.model.RelationType;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.internal.InternalTypesActivator;
import org.eclipse.osee.framework.jdk.core.util.Collections;

/**
 * @author Roberto E. Escobar
 */
public class CreateOseeTypeChangesReportOperation extends AbstractOperation {
   private final OseeTypeCache cache;
   private final List<TableData> tabs;

   public CreateOseeTypeChangesReportOperation(OseeTypeCache cache, List<TableData> tabs) {
      super("Report Osee Type Changes", InternalTypesActivator.PLUGIN_ID);
      this.cache = cache;
      this.tabs = tabs;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      createOseeEnumTypeReport(tabs, cache.getEnumTypeCache().getAllDirty());
      createAttributeTypeReport(tabs, cache.getAttributeTypeCache().getAllDirty());
      createArtifactTypeReport(tabs, cache.getArtifactTypeCache());
      createRelationTypeReport(tabs, cache.getRelationTypeCache().getAllDirty());
   }

   private String[] getColumns(Collection<?> types) {
      List<String> columns = new ArrayList<String>();
      columns.add("Name");
      columns.add("ModType");
      if (!types.isEmpty()) {
         AbstractOseeType type = (AbstractOseeType) types.iterator().next();
         columns.addAll(type.getFieldNames());
      }
      return columns.toArray(new String[columns.size()]);
   }

   private void createArtifactTypeReport(List<TableData> tabs, ArtifactTypeCache cache) throws OseeCoreException {
      List<String[]> rows = new ArrayList<String[]>();
      Collection<ArtifactType> types = cache.getAllDirty();
      String[] columns = getColumns(types);
      for (ArtifactType type : types) {
         List<String> data = new ArrayList<String>(columns.length);
         data.add(type.getName());
         data.add(type.getModificationType().getDisplayName());
         for (String fieldName : type.getFieldNames()) {
            boolean isDirty = type.isFieldDirty(fieldName);
            if (isDirty && ArtifactType.ARTIFACT_INHERITANCE_FIELD_KEY.equals(fieldName)) {
               data.add(type.getSuperArtifactTypes().toString());
            } else if (isDirty && ArtifactType.ARTIFACT_TYPE_ATTRIBUTES_FIELD_KEY.equals(fieldName)) {
               data.add(type.getLocalAttributeTypes().toString());
            } else {
               data.add(String.valueOf(isDirty));
            }
         }
         rows.add(data.toArray(new String[data.size()]));
      }
      tabs.add(new TableData("Artifact Types", columns, rows));
   }

   private void createAttributeTypeReport(List<TableData> tabs, Collection<AttributeType> types) throws OseeCoreException {
      List<String[]> rows = new ArrayList<String[]>();
      String[] columns = getColumns(types);
      for (AttributeType type : types) {
         List<String> data = new ArrayList<String>();
         data.add(type.getName());
         data.add(type.getModificationType().getDisplayName());
         for (String fieldName : type.getFieldNames()) {
            data.add(String.valueOf(type.isFieldDirty(fieldName)));
         }
         rows.add(data.toArray(new String[data.size()]));
      }
      tabs.add(new TableData("Attribute Types", columns, rows));
   }

   private void createRelationTypeReport(List<TableData> tabs, Collection<RelationType> types) throws OseeCoreException {
      List<String[]> rows = new ArrayList<String[]>();
      String[] columns = getColumns(types);
      for (RelationType type : types) {
         List<String> data = new ArrayList<String>();
         data.add(type.getName());
         data.add(type.getModificationType().getDisplayName());
         for (String fieldName : type.getFieldNames()) {
            data.add(String.valueOf(type.isFieldDirty(fieldName)));
         }
         rows.add(data.toArray(new String[data.size()]));
      }
      tabs.add(new TableData("Relation Types", columns, rows));
   }

   private void createOseeEnumTypeReport(List<TableData> tabs, Collection<OseeEnumType> types) throws OseeCoreException {
      List<String[]> rows = new ArrayList<String[]>();
      String[] columns = getColumns(types);
      for (OseeEnumType type : types) {
         List<String> data = new ArrayList<String>();
         data.add(type.getName());
         data.add(type.getModificationType().getDisplayName());
         for (String fieldName : type.getFieldNames()) {
            boolean isDirty = type.isFieldDirty(fieldName);
            if (isDirty && OseeEnumType.OSEE_ENUM_TYPE_ENTRIES_FIELD.equals(fieldName)) {
               List<String> dirtyItems = new ArrayList<String>();
               for (OseeEnumEntry entry : type.values()) {
                  if (entry.isDirty()) {
                     dirtyItems.add(String.format("*{%s}", entry.toString()));
                  } else {
                     dirtyItems.add(entry.toString());
                  }
               }
               data.add(Collections.toString(dirtyItems, ","));
            } else {
               data.add(String.valueOf(isDirty));
            }
         }
         rows.add(data.toArray(new String[data.size()]));
      }
      tabs.add(new TableData("OseeEnum Types", columns, rows));
   }
}
