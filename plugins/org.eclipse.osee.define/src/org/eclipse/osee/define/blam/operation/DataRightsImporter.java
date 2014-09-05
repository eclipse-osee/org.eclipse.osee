/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.blam.operation;

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.CodeUnit;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.DataRightsBasis;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.DataRightsClassification;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.SubjectMatterExpert;
import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelSaxHandler;
import org.eclipse.osee.framework.jdk.core.util.io.xml.RowProcessor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Ryan Rader
 */
public class DataRightsImporter extends AbstractBlam {

   public Branch branch;
   public SkynetTransaction transaction;

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Util");
   }

   @Override
   public String getXWidgetsXml() {
      return "<XWidgets><XWidget xwidgetType=\"XBranchSelectWidget\" horizontalLabel=\"true\" displayName=\"Branch\" /><XWidget xwidgetType=\"XText\" displayName=\"Path to DataRights XML\" /></XWidgets>";
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      // Get branch by name from blam
      branch = variableMap.getBranch("Branch");
      String xmlPath = variableMap.getString("Path to DataRights XML");
      if (branch == null) {
         log(String.format("A branch needs to be defined."));
         return;
      }

      if (!Strings.isValid(xmlPath)) {
         log(String.format("A path needs to be defined."));
         return;
      }

      transaction = TransactionManager.createTransaction(branch, "Data Rights Importer");

      File path = new File(xmlPath);
      XMLReader xmlReader = XMLReaderFactory.createXMLReader();
      xmlReader.setContentHandler(new ExcelSaxHandler(new DataRightsProcessor(), true));
      xmlReader.parse(new InputSource(new FileInputStream(path)));

      transaction.execute();
   }

   @Override
   public String getName() {
      return "Data Rights Importer";
   }

   public class DataRightsProcessor implements RowProcessor {

      private boolean ignore = false;
      private int smeIndex;
      private int dataRightsIndex;
      private int fileNameIndex;
      private int dataRightsBasisIndex;

      @Override
      public void processRow(String[] row) throws Exception {
         if (!ignore) {
            String SME = row[smeIndex];
            String dataRightsClassification = row[dataRightsIndex];
            String fileName = row[fileNameIndex];
            String dataRightsBasis = row[dataRightsBasisIndex];

            Artifact artifact =
               ArtifactQuery.getArtifactFromTypeAndName(CodeUnit, fileName, branch, QueryOption.CONTAINS_MATCH_OPTIONS);

            if (artifact == null) {
               log("artifact [" + fileName + "] does not exist");

            } else {
               artifact.setSoleAttributeValue(DataRightsClassification, dataRightsClassification);
               artifact.setSoleAttributeValue(DataRightsBasis, dataRightsBasis);
               artifact.setSoleAttributeValue(SubjectMatterExpert, SME);

               artifact.persist(transaction);
            }
         }
      }

      @Override
      public void processHeaderRow(String[] row) {
         for (int i = 0; i < row.length; i++) {
            if (row[i] != null) {
               if (row[i].equals("SME")) {
                  smeIndex = i;
               } else if (row[i].equals("Classification")) {
                  dataRightsIndex = i;
               } else if (row[i].equals("Code Unit")) {
                  fileNameIndex = i;
               } else if (row[i].equals("Basis")) {
                  dataRightsBasisIndex = i;
               }
            }
         }
      }

      @Override
      public void reachedEndOfWorksheet() {
         ignore = true;
      }

      @Override
      public void processEmptyRow() {
         // do nothing
      }

      @Override
      public void processCommentRow(String[] row) {
         // do nothing
      }

      @Override
      public void foundStartOfWorksheet(String sheetName) throws Exception {
         // do nothing
      }

      @Override
      public void detectedRowAndColumnCounts(int rowCount, int columnCount) {
         // do nothing
      }

   }
}
