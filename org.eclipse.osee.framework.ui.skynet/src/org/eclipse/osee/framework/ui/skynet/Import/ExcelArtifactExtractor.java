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
package org.eclipse.osee.framework.ui.skynet.Import;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.DoubleKeyHashMap;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelSaxHandler;
import org.eclipse.osee.framework.jdk.core.util.io.xml.RowProcessor;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Ryan D. Brooks
 */
public class ExcelArtifactExtractor extends AbstractArtifactExtractor implements RowProcessor {
   private String[] headerRow;
   private ArtifactType primaryDescriptor;
   private boolean importingRelations;
   private int rowCount;
   private DoubleKeyHashMap<String, Integer, RoughArtifact> relationHelper =
         new DoubleKeyHashMap<String, Integer, RoughArtifact>();
   private static final Pattern guidPattern = Pattern.compile("(\\d*);(.*)");
   private final Matcher guidMatcher = guidPattern.matcher("");
   private Branch branch;

   public String getDescription() {
      return "Extract each row as an artifact - header <section #, atrribute1, atrribute2 ...>";
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.define.artifact.Import.RowProcessor#processHeaderRow(java.lang.String[])
    */
   public void processHeaderRow(String[] headerRow) {
      rowCount++;
      this.headerRow = headerRow.clone();
      for (int i = 0; i < this.headerRow.length; i++) {
         if (headerRow[i] != null && headerRow[i].trim().length() == 0) {
            this.headerRow[i] = null;
         }
      }
   }

   /**
    * import Artifacts
    * 
    * @param row
    */
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
            OseeLog.log(SkynetGuiPlugin.class, Level.WARNING,
                  "we failed to add a relation because at least on of its guids are null");
         }
         addRoughRelation(new RoughRelation(row[0], guida, guidb, row[5], Integer.parseInt(row[3]),
               Integer.parseInt(row[4])));
      } else {
         RoughArtifact roughArtifact = new RoughArtifact(RoughArtifactKind.PRIMARY, branch);
         for (int i = 0; i < row.length; i++) {
            if (headerRow[i] == null) continue;
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
         addRoughArtifact(roughArtifact);

         relationHelper.put(primaryDescriptor.getName(), new Integer(rowCount), roughArtifact);
      }
   }

   /**
    * @param string
    * @throws Exception
    */
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

   /* (non-Javadoc)
    * @see osee.define.artifact.Import.ArtifactExtractor#discoverArtifactAndRelationData(java.io.File)
    */
   public void discoverArtifactAndRelationData(File artifactsFile, Branch branch) throws Exception {
      this.branch = branch;
      XMLReader xmlReader = XMLReaderFactory.createXMLReader();
      xmlReader.setContentHandler(new ExcelSaxHandler(this, true));
      xmlReader.parse(new InputSource(new InputStreamReader(new FileInputStream(artifactsFile), "UTF-8")));
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.define.artifact.Import.RowProcessor#processEmptyRow()
    */
   public void processEmptyRow() {
      rowCount++;
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.define.artifact.Import.RowProcessor#processCommentRow(java.lang.String[])
    */
   public void processCommentRow(String[] row) {
      rowCount++;
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.define.artifact.Import.RowProcessor#reachedEndOfWorksheet()
    */
   public void reachedEndOfWorksheet() {
   }

   /* (non-Javadoc)
    * @see osee.define.artifact.Import.RowProcessor#detectedTotalRowCount(int)
    */
   public void detectedRowAndColumnCounts(int rowCount, int columnCount) {
   }

   /* (non-Javadoc)
    * @see osee.define.artifact.Import.RowProcessor#foundStartOfWorksheet(java.lang.String)
    */
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
         throw new IllegalArgumentException("The sheet name: " + sheetName + " is not a valid artifact type name: ", ex);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.Import.ArtifactExtractor#getFileFilter()
    */
   public FileFilter getFileFilter() {
      return new FileFilter() {
         public boolean accept(File file) {
            return file.isDirectory() || (file.isFile() && file.getName().endsWith(".xml"));
         }
      };
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.Import.ArtifactExtractor#getName()
    */
   @Override
   public String getName() {
      return "Excel XML Artifacts";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.Import.ArtifactExtractor#usesTypeList()
    */
   @Override
   public boolean usesTypeList() {
      return false;
   }
}