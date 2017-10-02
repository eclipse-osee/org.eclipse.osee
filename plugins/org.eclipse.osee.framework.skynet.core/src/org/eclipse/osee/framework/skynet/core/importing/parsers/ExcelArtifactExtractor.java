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
package org.eclipse.osee.framework.skynet.core.importing.parsers;

import java.io.File;
import java.io.FileFilter;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.jdk.core.type.DoubleKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelSaxHandler;
import org.eclipse.osee.framework.jdk.core.util.io.xml.RowProcessor;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifact;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifactKind;
import org.eclipse.osee.framework.skynet.core.importing.RoughRelation;
import org.eclipse.osee.framework.skynet.core.importing.operations.RoughArtifactCollector;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Ryan D. Brooks
 */
public class ExcelArtifactExtractor extends AbstractArtifactExtractor {

   private static final Pattern guidPattern = Pattern.compile("(\\d*);(.*)");

   @Override
   public String getDescription() {
      return "Extract each row as an artifact, with header format <Attribute Type 1, Attribute Type 2, ...>";
   }

   @Override
   public FileFilter getFileFilter() {
      return new FileFilter() {
         @Override
         public boolean accept(File file) {
            return file.isDirectory() || file.isFile() && file.getName().endsWith(".xml");
         }
      };
   }

   @Override
   public String getName() {
      return "Excel XML Artifacts";
   }

   @Override
   public boolean usesTypeList() {
      return false;
   }

   @Override
   protected void extractFromSource(OperationLogger logger, URI source, RoughArtifactCollector collector) throws Exception {
      XMLReader xmlReader = XMLReaderFactory.createXMLReader();
      xmlReader.setContentHandler(new ExcelSaxHandler(new ExcelRowProcessor(collector), true));
      xmlReader.parse(new InputSource(new InputStreamReader(source.toURL().openStream(), "UTF-8")));
   }

   private static final class ExcelRowProcessor implements RowProcessor {

      private final DoubleKeyHashMap<String, Integer, RoughArtifact> relationHelper =
         new DoubleKeyHashMap<String, Integer, RoughArtifact>();

      private static enum RowTypeEnum {
         PARAGRAPH_NO(CoreAttributeTypes.ParagraphNumber.getName()),
         ARTIFACT_NAME(CoreAttributeTypes.Name.getName()),
         GUID("GUID"),
         OTHER("");

         private final static Map<String, RowTypeEnum> rawStringToRowType = new HashMap<>();

         public String _rowType;

         RowTypeEnum(String rowType) {
            _rowType = rowType;
         }

         public static synchronized RowTypeEnum fromString(String value) {
            if (rawStringToRowType.isEmpty()) {
               for (RowTypeEnum enumStatus : RowTypeEnum.values()) {
                  RowTypeEnum.rawStringToRowType.put(enumStatus._rowType, enumStatus);
               }
            }
            RowTypeEnum returnVal = rawStringToRowType.get(value);
            return returnVal != null ? returnVal : OTHER;
         }
      }
      private final Map<Integer, RowTypeEnum> rowIndexToRowTypeMap = new HashMap<>();

      private final Matcher guidMatcher;
      private final RoughArtifactCollector collector;

      private int rowCount;
      private String[] headerRow;
      private ArtifactType primaryDescriptor;
      private boolean importingRelations;

      public ExcelRowProcessor(RoughArtifactCollector collector) {
         this.guidMatcher = guidPattern.matcher("");
         this.collector = collector;
         rowCount = 0;
         importingRelations = false;
      }

      @Override
      public void detectedRowAndColumnCounts(int rowCount, int columnCount) {
         // do nothing
      }

      @Override
      public void foundStartOfWorksheet(String sheetName)  {
         rowCount = 0;
         if (sheetName.equals("relations")) {
            importingRelations = true;
            return;
         }
         primaryDescriptor = ArtifactTypeManager.getType(sheetName);
         if (primaryDescriptor == null) {
            throw new OseeArgumentException("The sheet [%s] is not a valid artifact type name.", sheetName);
         }
      }

      @Override
      public void processCommentRow(String[] row) {
         rowCount++;
      }

      @Override
      public void processEmptyRow() {
         rowCount++;
      }

      @Override
      public void processHeaderRow(String[] headerRow) {
         rowCount++;
         this.headerRow = headerRow.clone();
         for (int i = 0; i < this.headerRow.length; i++) {
            String value = headerRow[i];
            if (value != null) {
               value = value.trim();
            }
            if (!Strings.isValid(value)) {
               this.headerRow[i] = null;
            } else {
               RowTypeEnum rowTypeEnum = RowTypeEnum.fromString(value);
               rowIndexToRowTypeMap.put(i, rowTypeEnum);
            }
         }
      }

      @Override
      public void processRow(String[] row)  {
         rowCount++;
         if (importingRelations) {
            String guida = null;
            String guidb = null;
            guida = getGuid(row[1]);
            guidb = getGuid(row[2]);

            if (guida == null || guidb == null) {
               OseeLog.log(Activator.class, Level.WARNING,
                  "we failed to add a relation because at least on of its guids are null");
            }
            collector.addRoughRelation(new RoughRelation(row[0], guida, guidb, row[5]));
         } else {
            RoughArtifact roughArtifact = new RoughArtifact(RoughArtifactKind.PRIMARY);
            if (!rowIndexToRowTypeMap.isEmpty()) {
               for (int rowIndex = 0; rowIndex < row.length; rowIndex++) {
                  RowTypeEnum rowType = rowIndexToRowTypeMap.get(rowIndex);

                  String rowValue = row[rowIndex];

                  if (Strings.isValid(rowValue)) {
                     switch (rowType) {
                        case PARAGRAPH_NO:
                           roughArtifact.setSectionNumber(rowValue);
                           roughArtifact.addAttribute(CoreAttributeTypes.ParagraphNumber, rowValue);
                           break;
                        case ARTIFACT_NAME:
                           roughArtifact.setName(rowValue);
                           break;
                        case GUID:
                           roughArtifact.setGuid(rowValue);
                           break;
                        case OTHER:
                           roughArtifact.addAttribute(headerRow[rowIndex], rowValue);
                           break;
                     }
                  } else {
                     //complain only if row value invalid and parsing paragraph numbers
                     if (rowType == RowTypeEnum.PARAGRAPH_NO) {
                        throw new OseeArgumentException("%s must not be blank", CoreAttributeTypes.ParagraphNumber);
                     }
                  }

               }
            }

            collector.addRoughArtifact(roughArtifact);
            relationHelper.put(primaryDescriptor.getName(), rowCount, roughArtifact);
         }
      }

      private String getGuid(String string) {
         if (GUID.isValid(string)) {
            return string;
         }
         guidMatcher.reset(string);
         if (guidMatcher.matches()) {
            Integer row = Integer.parseInt(guidMatcher.group(1));
            String sheet = guidMatcher.group(2);
            RoughArtifact artifact = relationHelper.get(sheet, row);
            return artifact.getGuid();
         }
         return null;
      }

      @Override
      public void reachedEndOfWorksheet() {
         // do nothing
      }
   }
}
