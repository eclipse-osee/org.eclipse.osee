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
package org.eclipse.osee.define.traceability.blam;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.define.traceability.data.RequirementData;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeWrappedException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.StringFormat;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.relation.IRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.utility.AIFile;
import org.eclipse.osee.framework.skynet.core.utility.OseeData;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.swt.program.Program;

/**
 * @author Roberto E. Escobar
 */
public class UiToTestBlam extends AbstractBlam {
   private static final String EMPTY_STRING = "";

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam#getName()
    */
   @Override
   public String getName() {
      return "UI To Test Report";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getCategories()
    */
   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Define.Trace");
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam#getDescriptionUsage()
    */
   @Override
   public String getDescriptionUsage() {
      return "Usage Info here";
   }

   public String getXWidgetsXml() {
      StringBuilder builder = new StringBuilder();
      builder.append("<xWidgets>");
      builder.append("<XWidget xwidgetType=\"XFileSelectionDialog\" displayName=\"Select UI List File\" />");
      builder.append("<XWidget xwidgetType=\"XBranchSelectWidget\" displayName=\"Requirements Branch\" />");
      builder.append("</xWidgets>");
      return builder.toString();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.VariableMap, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      String fileName = variableMap.getString("Select UI List File");
      Branch branch = variableMap.getBranch("Requirements Branch");

      if (branch == null) {
         throw new OseeArgumentException("Branch cannot be null");
      }

      XResultData resultData = new XResultData();
      resultData.log("UI To Test Traceability");

      Set<Artifact> toTrace = new HashSet<Artifact>();
      String input = null;

      monitor.beginTask("UI To Test Trace Report", IProgressMonitor.UNKNOWN);
      try {
         monitor.subTask("Gathering Requirements");
         if (Strings.isValid(fileName)) {
            input = getUIsFromFile(monitor, fileName);
         }

         if (Strings.isValid(input)) {
            for (String ui : input.split("\n")) {
               String toGet = ui.trim();
               toTrace.addAll(ArtifactQuery.getArtifactsFromName(toGet, branch, false));
            }
         } else {
            RequirementData requirements = new RequirementData(branch);
            requirements.initialize(monitor);
            toTrace.addAll(requirements.getAllSwRequirements());
         }

         StringWriter appendable = new StringWriter();

         monitor.subTask("Gathering Test Unit Trace");

         if (toTrace.isEmpty()) {
            addRow(appendable, "No Requirements found");
         } else {
            addRow(appendable, String.format("[%s] Requirements Found", toTrace.size()));

            List<String> headers = new ArrayList<String>();
            for (Column column : Column.values()) {
               headers.add(column.name());
            }
            addRow(appendable, headers.toArray(new String[headers.size()]));
            for (Artifact requirement : toTrace) {
               processTrace(appendable, requirement, "Verified By", CoreRelationEnumeration.Verification__Verifier);
               processTrace(appendable, requirement, "Used By", CoreRelationEnumeration.Uses__TestUnit);
            }
         }

         if (appendable.getBuffer().length() > 0) {
            String outFileName = "UI_To_TestUnit." + Lib.getDateTimeString() + ".csv";
            IFile iFile = OseeData.getIFile(outFileName);
            AIFile.writeToFile(iFile, appendable.toString());
            Program.launch(iFile.getLocation().toOSString());
         }
      } finally {
         monitor.subTask("Done");
         System.gc();
      }
   }

   private void addRow(Appendable appendable, String... data) throws OseeCoreException {
      if (data != null && data.length > 0) {
         try {
            for (int index = 0; index < data.length; index++) {
               appendable.append("\"");
               appendable.append(data[index]);
               appendable.append("\"");
               if (index + 1 < data.length) {
                  appendable.append(",");
               }
            }
            appendable.append("\n");
         } catch (Exception ex) {
            throw new OseeWrappedException(ex);
         }
      }
   }

   private String getUIsFromFile(IProgressMonitor monitor, String filePath) throws Exception {
      String input;
      File file = new File(filePath);
      if (file == null || !file.exists()) {
         throw new OseeArgumentException("UI list file not accessible");
      }
      IFileStore fileStore = EFS.getStore(file.toURI());
      InputStream inputStream = null;
      try {
         inputStream = new BufferedInputStream(fileStore.openInputStream(EFS.NONE, monitor));
         input = Lib.inputStreamToString(inputStream);
      } finally {
         if (inputStream != null) {
            inputStream.close();
         }
      }
      return input;
   }

   private String[] asArray(String... data) {
      String[] toReturn = new String[Column.values().length];
      if (data != null && data.length > 0) {
         for (int index = 0; index < toReturn.length; index++) {
            if (data.length > index) {
               toReturn[index] = data[index];
            }
         }
      }
      return toReturn;
   }

   private void processTrace(Appendable appendable, Artifact requirement, String testType, IRelationEnumeration relationType) throws OseeCoreException {
      String uiTitle = requirement.getDescriptiveName();
      String uiType = requirement.getArtifactTypeName();

      List<Artifact> testUnits = requirement.getRelatedArtifacts(relationType);
      if (testUnits.isEmpty()) {
         addRow(appendable, asArray(uiTitle, uiType, "NONE", "NONE", EMPTY_STRING, EMPTY_STRING, EMPTY_STRING,
               EMPTY_STRING));
      } else {
         for (Artifact testUnit : testUnits) {
            String testUnitName = testUnit.getDescriptiveName();
            String testUnitType = testUnit.getArtifactTypeName();
            List<String> verified = getTrace(testUnit, CoreRelationEnumeration.Verification__Requirement);
            List<String> used = getTrace(testUnit, CoreRelationEnumeration.Uses__Requirement);
            String verifyStr = StringFormat.listToValueSeparatedString(verified, ",\r\n");
            String usesStr = StringFormat.listToValueSeparatedString(used, ",\r\n");

            addRow(appendable, asArray(uiTitle, uiType, testType, testUnitName, testUnitType, verifyStr, usesStr,
                  EMPTY_STRING));
         }
      }
   }

   private List<String> getTrace(Artifact testUnit, IRelationEnumeration relation) throws OseeCoreException {
      List<String> toReturn = new ArrayList<String>();
      for (Artifact item : testUnit.getRelatedArtifacts(relation)) {
         toReturn.add(item.getDescriptiveName());
      }
      if (toReturn.isEmpty()) {
         toReturn.add("NONE");
      }
      return toReturn;
   }

   private enum Column {
      UI_Title,
      Requirement_Type,
      Relates_To_Test_Unit,
      Test_Unit_Name,
      Test_Unit_Type,
      Relates_To_Requirements,
      UI_Name,
      UI_Type;

      public String asLabel() {
         return this.name().replaceAll("_", " ");
      }
   }

}
