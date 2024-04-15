/*********************************************************************
 * Copyright (c) 2023 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.ats.ide.integration.tests.orcs.rest.applic;

import static org.junit.Assert.assertTrue;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import org.eclipse.osee.ats.ide.util.ServiceUtil;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.orcs.rest.model.TransactionEndpoint;
import org.eclipse.osee.orcs.rest.model.transaction.BranchLocation;
import org.eclipse.osee.orcs.rest.model.transaction.TransferDBType;
import org.eclipse.osee.orcs.rest.model.transaction.TransferInitData;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Torin Grenda
 */
public class TransactionTransferImportTest {

   private static TransactionEndpoint transactionEndpoint;
   private static TransferInitData testInitJson = new TransferInitData();
   private static String emptyFile = "support/TransferTestFiles/EmptyTransfer.zip";
   private static String emptyManifest = "support/TransferTestFiles/EmptyManifestTransfer.zip";
   private static String incorrectExportId = "support/TransferTestFiles/IncorrectExportId.zip";
   private static String incorrectManifest = "support/TransferTestFiles/IncorrectManifestTransfer.zip";
   private static String incorrectFileFormat = "support/TransferTestFiles/IncorrectFormatTransfer.zip";

   @BeforeClass
   public static void testSetup() {
      transactionEndpoint = ServiceUtil.getOseeClient().getTransactionEndpoint();

      //Setup initial initJson
      BranchLocation bl = new BranchLocation();
      BranchLocation blCommon = new BranchLocation();
      blCommon.setBaseTxId(getMaxTransaction(CoreBranches.COMMON));
      blCommon.setBranchId(CoreBranches.COMMON);
      bl.setBaseTxId(getMaxTransaction(DemoBranches.SAW_PL));
      bl.setBranchId(DemoBranches.SAW_PL);
      testInitJson.setBranchLocations(Arrays.asList(blCommon, bl));
      testInitJson.setExportId(TransactionId.valueOf(259274267223164579L));
      testInitJson.setTransferDBType(TransferDBType.DESTINATION);
      transactionEndpoint.initTransactionTransfer(testInitJson);
   }

   @Test
   public void testEmptyManifest() {
      try (InputStream inputStream = OsgiUtil.getResourceAsStream(getClass(), emptyManifest);) {
         checkResults(transactionEndpoint.uploadTransferFile(inputStream), "Error: BuildId not found in:");
      } catch (IOException ex) {
         throw new OseeCoreException("IOException has occured: " + ex);
      }
   }

   @Test
   public void testEmptyTransferFile() {
      try (InputStream inputStream = OsgiUtil.getResourceAsStream(getClass(), emptyFile);) {
         checkResults(transactionEndpoint.uploadTransferFile(inputStream),
            "Exception while verifying manifest and transaction files");
      } catch (IOException ex) {
         throw new OseeCoreException("IOException has occured: " + ex);
      }
   }

   @Test
   public void testIncorrectExportId() {
      try (InputStream inputStream = OsgiUtil.getResourceAsStream(getClass(), incorrectExportId);) {
         checkResults(transactionEndpoint.uploadTransferFile(inputStream),
            "The export ID is not valid. This id is not matched or there is more than one in db.");
      } catch (IOException ex) {
         throw new OseeCoreException("IOException has occured: " + ex);
      }
   }

   @Test
   public void testIncorrectManifest() {
      //Expects error to say this import is missing a file
      try (InputStream inputStream = OsgiUtil.getResourceAsStream(getClass(), incorrectManifest);) {
         checkResults(transactionEndpoint.uploadTransferFile(inputStream), "Missing ");
      } catch (IOException ex) {
         throw new OseeCoreException("IOException has occured: " + ex);
      }
   }

   @Test
   public void testIncorrectFolderFormat() {
      //Expects error to say this import is missing a file
      try (InputStream inputStream = OsgiUtil.getResourceAsStream(getClass(), incorrectFileFormat);) {
         checkResults(transactionEndpoint.uploadTransferFile(inputStream), "Missing ");
      } catch (IOException ex) {
         throw new OseeCoreException("IOException has occured: " + ex);
      }
   }

   private static TransactionId getMaxTransaction(BranchId branchId) {
      return TransactionManager.getTransactionsForBranch(branchId).get(0);
   }

   private void checkResults(XResultData results, String teststr) {
      boolean pass = false;
      for (String str : results.getResults()) {
         if (str.contains(teststr)) {
            pass = true;
            break;
         }
      }
      assertTrue(pass);
   }
}