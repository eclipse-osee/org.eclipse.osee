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
package org.eclipse.osee.define.ide.blam.operation;

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.CodeUnit;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.DataRightsBasis;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.DataRightsClassification;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.SubjectMatterExpert;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelSaxHandler;
import org.eclipse.osee.framework.jdk.core.util.io.xml.RowProcessor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.artifact.search.QueryBuilderArtifact;
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
   private Map<String, Artifact> nameToArtifact;

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Util");
   }

   @Override
   public String getXWidgetsXml() {
      StringBuffer buffer = new StringBuffer("<xWidgets>");
      buffer.append("<XWidget xwidgetType=\"XBranchSelectWidget\" horizontalLabel=\"true\" displayName=\"Branch\" />");
      buffer.append("<XWidget xwidgetType=\"XFileSelectionDialog\" displayName=\"Path to DataRights XML\" />");
      buffer.append("</xWidgets>");
      return buffer.toString();
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      BranchId branch = variableMap.getBranch("Branch");
      String xmlPath = variableMap.getString("Path to DataRights XML");

      if (branch == null) {
         log(String.format("A branch needs to be defined."));
      } else if (!Strings.isValid(xmlPath)) {
         log(String.format("A path needs to be defined."));
      } else {
         log("path [" + xmlPath + "]");

         if (nameToArtifact == null) {
            QueryBuilderArtifact builder = ArtifactQuery.createQueryBuilder(branch);
            builder.andIsOfType(CodeUnit);
            ResultSet<Artifact> results = builder.getResults();
            nameToArtifact = new HashMap<>();
            for (Artifact artifact : results) {
               nameToArtifact.put(artifact.getName(), artifact);
            }
         }

         XMLReader xmlReader = XMLReaderFactory.createXMLReader();
         DataRightsProcessor processor = null;
         InputStream inputStream = null;
         try {
            inputStream = new FileInputStream(xmlPath);
            processor = new DataRightsProcessor(branch);
            xmlReader.setContentHandler(new ExcelSaxHandler(processor, true));
            xmlReader.parse(new InputSource(inputStream));
         } finally {
            Lib.close(inputStream);
         }
         processor.persist();
      }
   }

   @Override
   public String getName() {
      return "Data Rights Importer";
   }

   @Override
   public String getDescriptionUsage() {
      return "This BLAM imports data rights attributes (SME, Classification and Basis) from a XML spreadsheet into existing Artifacts in ATS";
   }

   private final class DataRightsProcessor implements RowProcessor {

      private final BranchId branch;

      private boolean ignore;
      private int smeIndex;
      private int dataRightsIndex;
      private int fileNameIndex;
      private int dataRightsBasisIndex;
      private boolean changesAvailable;
      private SkynetTransaction transaction;

      public DataRightsProcessor(BranchId branch) {
         super();
         this.branch = branch;
      }

      private SkynetTransaction getTransaction() {
         if (transaction == null) {
            transaction = TransactionManager.createTransaction(branch, "Data Rights Importer");
            changesAvailable = true;
         }
         return transaction;
      }

      @Override
      public void processRow(String[] row) throws Exception {
         if (!ignore) {
            String SME = row[smeIndex];
            String dataRightsClassification = row[dataRightsIndex];
            String fileName = row[fileNameIndex];
            String dataRightsBasis = row[dataRightsBasisIndex];
            if (!Strings.isValid(SME)) {
               SME = "Unspecified";
            }
            if (!Strings.isValid(dataRightsClassification)) {
               dataRightsClassification = "Unspecified";
            }
            if (!Strings.isValid(dataRightsBasis)) {
               dataRightsBasis = "Unspecified";
            }
            try {
               Artifact artifact = getArtifactByName(fileName);
               if (artifact == null) {
                  log("artifact [" + fileName + "] does not exist");
               } else {
                  artifact.setSoleAttributeValue(DataRightsClassification, dataRightsClassification);
                  artifact.setSoleAttributeValue(DataRightsBasis, dataRightsBasis);
                  artifact.setSoleAttributeValue(SubjectMatterExpert, SME);

                  SkynetTransaction transaction = getTransaction();
                  artifact.persist(transaction);
               }
            } catch (Exception ex) {
               log("dataRightsClassification [" + dataRightsClassification + "] does not exist");
               log("dataRightsBasis [" + dataRightsBasis + "] does not exist");
               log("SME [" + SME + "] does not exist");
               log("artifact [" + fileName + "] does not exist");
               log(ex);
            }

         }
      }

      public Artifact getArtifactByName(String name) {
         return nameToArtifact.get(name);
      }

      @Override
      public void processHeaderRow(String[] row) {
         for (int index = 0; index < row.length; index++) {
            String header = row[index];
            if ("SME".equals(header)) {
               smeIndex = index;
            } else if ("Classification".equals(header)) {
               dataRightsIndex = index;
            } else if ("Virtual Path (formula)".equals(header)) {
               fileNameIndex = index;
            } else if ("Basis".equals(header)) {
               dataRightsBasisIndex = index;
            }
         }
      }

      @Override
      public void reachedEndOfWorksheet() {
         ignore = true;

      }

      @Override
      public void processEmptyRow() {
         ignore = true;
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

      public void persist() {
         if (changesAvailable) {
            getTransaction().execute();
         }
      }

   }
}
