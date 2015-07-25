/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.writer;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.xml.RowProcessor;
import org.eclipse.osee.orcs.writer.model.reader.OwArtifact;
import org.eclipse.osee.orcs.writer.model.reader.OwArtifactToken;
import org.eclipse.osee.orcs.writer.model.reader.OwArtifactType;
import org.eclipse.osee.orcs.writer.model.reader.OwAttribute;
import org.eclipse.osee.orcs.writer.model.reader.OwAttributeType;
import org.eclipse.osee.orcs.writer.model.reader.OwCollector;
import org.eclipse.osee.orcs.writer.model.reader.OwRelation;
import org.eclipse.osee.orcs.writer.model.reader.OwRelationType;

/**
 * @author Donald G. Dunne
 */
public class OrcsWriterSheetProcessorForCreateUpdate implements RowProcessor {

   private final OwCollector collector;
   private final Map<Integer, OwAttributeType> columnToAttributeType = new HashMap<>();
   private final Map<Integer, OwRelationType> columnToRelationType = new HashMap<>();
   private Integer artTokenColumn = null, nameColumn = null;
   private int rowCount = 0;
   private final OrcsWriterFactory factory;
   private final boolean createSheet;

   public OrcsWriterSheetProcessorForCreateUpdate(OwCollector collector, XResultData result, boolean createSheet) {
      this.collector = collector;
      this.createSheet = createSheet;
      this.factory = new OrcsWriterFactory(collector);
   }

   @Override
   public void detectedRowAndColumnCounts(int rowCount, int columnCount) {
      // do nothing
   }

   @Override
   public void foundStartOfWorksheet(String sheetName) throws OseeCoreException {
      // do nothing
   }

   @Override
   public void processCommentRow(String[] row) {
      // do nothing
   }

   @Override
   public void processEmptyRow() {
      // do nothing
   }

   boolean isAttributeColumn(int x) {
      return columnToAttributeType.containsKey(x);
   }

   boolean isRelationColumn(int x) {
      return columnToRelationType.containsKey(x);
   }

   @Override
   public void processHeaderRow(String[] headerRow) {
      rowCount++;
      for (int colCount = 0; colCount < headerRow.length; colCount++) {
         String value = headerRow[colCount];
         if (value != null) {
            if (value.toLowerCase().equals("name")) {
               if (nameColumn != null) {
                  throw new OseeArgumentException("Can't have multiple Name columns on %s sheet", getSheetName());
               }
               nameColumn = colCount;
            } else if (value.toLowerCase().equals("attribute")) {
               OwAttributeType attrType = new OwAttributeType();
               attrType.setData("Column " + colCount);
               columnToAttributeType.put(colCount, attrType);
               collector.getAttrTypes().add(attrType);
            } else if (value.toLowerCase().equals("relation")) {
               OwRelationType relType = new OwRelationType();
               relType.setData("Column " + colCount);
               columnToRelationType.put(colCount, relType);
               collector.getRelTypes().add(relType);
            } else if (value.toLowerCase().startsWith("new art token")) {
               if (artTokenColumn != null) {
                  throw new OseeArgumentException("Can't have multiple \"New Art Token\" columns on %s sheet",
                     getSheetName());
               }
               artTokenColumn = colCount;
            }
         }
      }
      if (nameColumn == null) {
         throw new OseeArgumentException("Name column must be present on %s sheet", getSheetName());
      }
      if (artTokenColumn == null) {
         throw new OseeArgumentException("Artifact Token column must be present on %s sheet", getSheetName());
      }
   }

   private String getSheetName() {
      return createSheet ? "CREATE" : "UPDATE";
   }

   @Override
   public void processRow(String[] row) throws OseeCoreException {
      rowCount++;
      OwArtifact artifact = new OwArtifact();
      artifact.setData("Row " + rowCount);
      if (rowCount == 2) {
         for (int colCount = 0; colCount < row.length; colCount++) {
            if (colCount > 2) {
               if (isAttributeColumn(colCount)) {
                  OwAttributeType attrType = columnToAttributeType.get(colCount);
                  String value = row[colCount];
                  factory.processAttributeType(attrType, value);
               } else if (isRelationColumn(colCount)) {
                  OwRelationType relType = columnToRelationType.get(colCount);
                  String value = row[colCount];
                  factory.processRelationType(relType, value);
               }
            }
         }
      } else if (rowCount > 2) {
         if (createSheet) {
            collector.getCreate().add(artifact);
         } else {
            collector.getUpdate().add(artifact);
         }
         for (int colCount = 0; colCount < row.length; colCount++) {
            if (colCount == 0) {
               String value = row[0];
               // First column of create sheet is artifact type token
               if (createSheet) {
                  if (!Strings.isValid(value)) {
                     throw new OseeArgumentException(
                        "First column must contain artifact type.  row number on CREATE sheet" + rowCount);
                  } else {
                     OwArtifactType artType = factory.getOrCreateArtifactType(value);
                     if (artType == null) {
                        throw new OseeArgumentException(
                           "Invalid Artifact Type row %d value [%s]; expected [name]-[uuid] on CREATE sheet", rowCount,
                           value);
                     }
                     artifact.setType(artType);
                  }
               }
               // Else, first column of update sheet is artifact token
               else {
                  if (!Strings.isValid(value)) {
                     throw new OseeArgumentException(
                        "First column must contain artifact token.  row number %d on UPDATE sheet", rowCount);
                  } else {
                     OwArtifactToken artifactToken = factory.getOrCreateToken(value);
                     if (artifactToken == null) {
                        throw new OseeArgumentException(
                           "Invalid Artifact Token row %d value [%s]; expected [name]-[uuid] on UPDATE sheet", rowCount,
                           value);
                     }
                     artifact.setUuid(artifactToken.getUuid());
                  }
               }
            }
            if (artTokenColumn == colCount) {
               String value = row[colCount];
               if (Strings.isValid(value)) {
                  OwArtifactToken token = factory.getOrCreateToken(value);
                  if (token.getUuid() > 0L) {
                     artifact.setUuid(token.getUuid());
                  } else {
                     throw new OseeStateException("Unexpected string [%s] at %s; expected [name]-[uuid] on sheet",
                        value, OrcsWriterUtil.getRowColumnStr(colCount, colCount), getSheetName());
                  }
               }
            }
            if (nameColumn == colCount) {
               String value = row[colCount];
               if (Strings.isValid(value)) {
                  artifact.setName(value);
               } else {
                  throw new OseeStateException("Unexpected Name [%s] at %s on %s sheet", value,
                     OrcsWriterUtil.getRowColumnStr(colCount, colCount), getSheetName());
               }
            }
            if (colCount > 2) {
               if (isAttributeColumn(colCount)) {
                  OwAttributeType attrType = columnToAttributeType.get(colCount);
                  if (attrType.getName().equals(CoreAttributeTypes.Name.getName())) {
                     throw new OseeStateException("Name cannot also exist as attribute column at %s on %s sheet",
                        OrcsWriterUtil.getRowColumnStr(rowCount, colCount), getSheetName());
                  }
                  String value = row[colCount];
                  OwAttribute attr = factory.getOrCreateAttribute(artifact, attrType);
                  attr.getValues().add(value);
                  attr.setData(OrcsWriterUtil.getData(rowCount, colCount, attr.getData()));
               } else if (isRelationColumn(colCount)) {
                  OwRelationType relType = columnToRelationType.get(colCount);
                  String value = row[colCount];
                  if (Strings.isValid(value)) {
                     OwRelation relation = factory.createRelationType(relType, value);
                     relation.setData(OrcsWriterUtil.getData(rowCount, colCount, relation.getData()));
                     artifact.getRelations().add(relation);
                  }
               }
            }
         }
      }
   }

   @Override
   public void reachedEndOfWorksheet() {
      // do nothing
   }

}
