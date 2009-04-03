package org.eclipse.osee.define.traceability;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.define.traceability.operations.TestUnitFromResourceOperation;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam;

/**
 * @author Donald G. Dunne
 */
public class ImportTestScriptsBlam extends AbstractBlam {

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam#getName()
    */
   @Override
   public String getName() {
      return "Import Test Scripts";
   }

   private String getOperationsCheckBoxes(String value) {
      StringBuilder builder = new StringBuilder();
      builder.append("<XWidget xwidgetType=\"XCheckBox\" displayName=\"");
      builder.append(value);
      builder.append("\" labelAfter=\"true\" horizontalLabel=\"true\"/>");
      return builder.toString();
   }

   /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getXWidgetXml()
     */
   @Override
   public String getXWidgetsXml() {
      StringBuilder builder = new StringBuilder();
      builder.append("<xWidgets>");
      builder.append("<XWidget xwidgetType=\"XDirectorySelectionDialog\" displayName=\"Select Test Script Folder\" defaultValue=\"L:/root/lba_users/rbrooks/v13scripts/\" />");

      builder.append("<XWidget xwidgetType=\"XBranchSelectWidget\" displayName=\"Requirements Branch\" />");
      builder.append("<XWidget xwidgetType=\"XBranchSelectWidget\" displayName=\"Import Into Branch\" />");

      builder.append("<XWidget xwidgetType=\"XLabel\" displayName=\"Select Test Unit Types:\"/>");
      Collection<String> testUnitIds = getTestUnitTraceIds().values();
      if (testUnitIds.isEmpty()) {
         builder.append("<XWidget xwidgetType=\"XLabel\" displayName=\"        *** No Test Unit Types Available ***\"/>");
      } else {
         for (String name : testUnitIds) {
            builder.append(getOperationsCheckBoxes(name));
         }
      }
      builder.append("<XWidget xwidgetType=\"XCheckBox\" displayName=\"Is Recursive\" labelAfter=\"true\" horizontalLabel=\"true\" />");
      builder.append("<XWidget xwidgetType=\"XCheckBox\" displayName=\"Persist Changes\" labelAfter=\"true\" horizontalLabel=\"true\"/>");
      builder.append("</xWidgets>");
      return builder.toString();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.VariableMap,
    *      org.eclipse.core.runtime.IProgressMonitor)
    */
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      try {
         Branch requirementsBranch = variableMap.getBranch("Requirements Branch");
         Branch importToBranch = variableMap.getBranch("Import Into Branch");
         boolean isRecursive = variableMap.getBoolean("Is Recursive");
         boolean isPersistChanges = variableMap.getBoolean("Persist Changes");
         URI source = null;
         String testScriptFolder = variableMap.getString("Select Test Script Folder");
         if (Strings.isValid(testScriptFolder)) {
            File file = new File(testScriptFolder);
            if (file != null) {
               source = file.toURI();
            }
         }

         List<String> testUnitIds = new ArrayList<String>();
         Map<String, String> mappedIds = getTestUnitTraceIds();
         for (String id : TestUnitFromResourceOperation.getTestUnitTraceIds()) {
            String name = mappedIds.get(id);
            if (name != null && variableMap.getBoolean(name)) {
               testUnitIds.add(id);
            }
         }

         if (isPersistChanges) {
            TestUnitFromResourceOperation.importTraceFromTestUnits(monitor, source, isRecursive, requirementsBranch,
                  importToBranch, testUnitIds.toArray(new String[testUnitIds.size()]));
         } else {
            TestUnitFromResourceOperation.printTraceFromTestUnits(monitor, source, isRecursive,
                  testUnitIds.toArray(new String[testUnitIds.size()]));
         }
      } finally {
         monitor.subTask("Done");
         System.gc();
      }
   }

   private Map<String, String> getTestUnitTraceIds() {
      Map<String, String> idMap = new HashMap<String, String>();
      try {
         for (String id : TestUnitFromResourceOperation.getTestUnitTraceIds()) {
            String value = id;
            int index = id.lastIndexOf(".");
            if (index > -1) {
               value = value.substring(index + 1, value.length());
            }
            idMap.put(id, value);
         }
      } catch (Exception ex) {
         // Do Nothing
      }
      return idMap;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam#getDescriptionUsage()
    */
   @Override
   public String getDescriptionUsage() {
      return "This BLAM will create/update test script and their requirement traceability\n" +
      //
      "NOTE: Upon running this BLAM, all new traces will be added into OSEE and traces not in the test scripts will be removed from OSEE." +
      //
      "Thus, any manually added traceability in OSEE will be removed.";
   }

   public Collection<String> getCategories() {
      return Arrays.asList("Define.Trace");
   }
}
