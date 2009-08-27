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
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.DoubleKeyHashMap;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelSaxHandler;
import org.eclipse.osee.framework.jdk.core.util.io.xml.RowProcessor;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
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

   public String getDescription() {
      return "Extract each row as an artifact header <Section #, Attribute 1, Attribute 2, ...>";
   }

   public FileFilter getFileFilter() {
      return new FileFilter() {
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
   protected void extractFromSource(URI source, RoughArtifactCollector collector) throws Exception {
      XMLReader xmlReader = XMLReaderFactory.createXMLReader();
      xmlReader.setContentHandler(new ExcelSaxHandler(new ExcelRowProcessor(collector), true));
      xmlReader.parse(new InputSource(new InputStreamReader(source.toURL().openStream(), "UTF-8")));
   }

   private final class ExcelRowProcessor implements RowProcessor {

      private final DoubleKeyHashMap<String, Integer, RoughArtifact> relationHelper =
            new DoubleKeyHashMap<String, Integer, RoughArtifact>();

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

      /*
       * (non-Javadoc)
       * @see org.eclipse.osee.framework.jdk.core.util.io.xml.RowProcessor#detectedRowAndColumnCounts(int, int)
       */
      @Override
      public void detectedRowAndColumnCounts(int rowCount, int columnCount) {
      }

      @Override
      public void foundStartOfWorksheet(String sheetName) {
         rowCount = 0;
         try {
            if (sheetName.equals("relations")) {
               importingRelations = true;
               return;
            }
            primaryDescriptor = ArtifactTypeManager.getType(sheetName);
            if (primaryDescriptor == null) {
               throw new OseeArgumentException("The sheet name: " + sheetName + " is not a valid artifact type name.");
            }
         } catch (OseeCoreException ex) {
            throw new IllegalArgumentException("The sheet name: " + sheetName + " is not a valid artifact type name: ",
                  ex);
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
            if (headerRow[i] != null && headerRow[i].trim().length() == 0) {
               this.headerRow[i] = null;
            }
         }
      }

      @Override
      public void processRow(String[] row) {
         rowCount++;
         if (importingRelations) {
            String guida = null;
            String guidb = null;
            try {
               guida = getGuid(row[1]);
               guidb = getGuid(row[2]);
            } catch (Exception ex) {
               throw new IllegalStateException(ex);
            }

            if (guida == null || guidb == null) {
               OseeLog.log(Activator.class, Level.WARNING,
                     "we failed to add a relation because at least on of its guids are null");
            }
            // TODO Add relation order
            //         int aOrder = Integer.parseInt(row[3]);
            //         int bOrder = Integer.parseInt(row[4]);
            collector.addRoughRelation(new RoughRelation(row[0], guida, guidb, row[5]));
         } else {
            RoughArtifact roughArtifact = new RoughArtifact(RoughArtifactKind.PRIMARY);
            for (int i = 0; i < row.length; i++) {
               if (headerRow[i] != null) {
                  if (headerRow[i].equalsIgnoreCase("Outline Number")) {
                     if (row[i] == null) {
                        throw new IllegalArgumentException("Outline Number must not be blank");
                     }
                     roughArtifact.setSectionNumber(row[i]);
                  } else if (headerRow[i].equalsIgnoreCase("GUID")) {
                     roughArtifact.setGuid(row[i]);
                  } else if (headerRow[i].equalsIgnoreCase("Human Readable Id")) {
                     roughArtifact.setHumandReadableId(row[i]);
                  } else {
                     roughArtifact.addAttribute(headerRow[i], row[i]);
                  }
               }
            }
            collector.addRoughArtifact(roughArtifact);

            relationHelper.put(primaryDescriptor.getName(), new Integer(rowCount), roughArtifact);
         }
      }

      private String getGuid(String string) {
         if (GUID.isValid(string)) {//it may be real guid
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
      }

   }

}