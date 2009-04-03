/*
 * Created on Apr 2, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.define.traceability.operations;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.define.traceability.ITestUnitLocator;
import org.eclipse.osee.define.traceability.ITraceParser;
import org.eclipse.osee.define.traceability.TestUnitTraceExtensionManager;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeWrappedException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;

/**
 * @author Roberto E. Escobar
 */
public class TestUnitFromResourceOperation {

   public static Set<String> getTestUnitTraceIds() throws OseeCoreException {
      return TestUnitTraceExtensionManager.getInstance().getTestUnitTraceIds();
   }

   private static ResourceToTestUnit getResourceToTestUnit(URI source, boolean isRecursive, String... testUnitTraceIds) throws OseeCoreException {
      checkSourceArgument(source);
      checktestUnitTraceIdsArgument(testUnitTraceIds);

      ResourceToTestUnit testUnitOperation = new ResourceToTestUnit(source, isRecursive);
      TestUnitTraceExtensionManager traceManager = TestUnitTraceExtensionManager.getInstance();
      for (String testUnitTraceId : testUnitTraceIds) {

         ITestUnitLocator locator = traceManager.getTestUnitLocatorById(testUnitTraceId);
         ITraceParser parser = traceManager.getTraceParserById(testUnitTraceId);
         if (locator != null && parser != null) {
            testUnitOperation.addTestUnitHandler(locator, parser);
         }
      }
      return testUnitOperation;
   }

   public static void printTraceFromTestUnits(IProgressMonitor monitor, URI source, boolean isRecursive, String... testUnitTraceIds) throws OseeCoreException {
      ResourceToTestUnit testUnitOperation = getResourceToTestUnit(source, isRecursive, testUnitTraceIds);
      if (monitor == null) monitor = new NullProgressMonitor();
      testUnitOperation.addTraceProcessor(new PrintTestUnitTraceProcessor());
      testUnitOperation.execute(monitor);
   }

   public static void importTraceFromTestUnits(IProgressMonitor monitor, URI source, boolean isRecursive, Branch requirementsBranch, Branch importToBranch, String... testUnitTraceIds) throws OseeCoreException {
      checkBranchArguments(requirementsBranch, importToBranch);

      ResourceToTestUnit testUnitOperation = getResourceToTestUnit(source, isRecursive, testUnitTraceIds);
      if (monitor == null) monitor = new NullProgressMonitor();
      testUnitOperation.addTraceProcessor(new TestUnitToArtifactProcessor(requirementsBranch, importToBranch));
      testUnitOperation.execute(monitor);
   }

   private static void checktestUnitTraceIdsArgument(String... testUnitTraceIds) throws OseeCoreException {
      if (testUnitTraceIds == null) {
         throw new OseeArgumentException("Test unit trace ids was null");
      }
      if (testUnitTraceIds.length == 0) {
         throw new OseeArgumentException("Test unit trace ids was empty");
      }

      try {
         Set<String> ids = getTestUnitTraceIds();
         List<String> notFound = Collections.setComplement(Arrays.asList(testUnitTraceIds), ids);
         if (!notFound.isEmpty()) {
            throw new OseeArgumentException(String.format("Invalid test unit trace id(s) [%s]", notFound));
         }
      } catch (Exception ex) {
         throw new OseeWrappedException(ex);
      }
   }

   private static void checkSourceArgument(URI source) throws OseeArgumentException {
      if (source == null) {
         throw new OseeArgumentException("Source was null");
      }
      try {
         IFileStore fileStore = EFS.getStore(source);
         IFileInfo fileInfo = fileStore.fetchInfo();
         if (!fileInfo.exists()) {
            throw new OseeArgumentException(String.format("Unable to access source: [%s]", source));
         }
      } catch (Exception ex) {
         throw new OseeArgumentException(String.format("Unable to access source: [%s]", source));
      }
   }

   private static void checkBranchArguments(Branch requirementsBranch, Branch importToBranch) throws OseeArgumentException {
      if (requirementsBranch == null) {
         throw new OseeArgumentException("Requirements branch was null");
      }
      if (importToBranch == null) {
         throw new OseeArgumentException("Branch to import into was null");
      }
      if (!importToBranch.isOfType(BranchType.WORKING)) {
         throw new OseeArgumentException(String.format("Branch to import into was not a working branch: [%s]",
               importToBranch));
      }
   }
}
