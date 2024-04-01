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

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import org.eclipse.osee.ats.ide.util.ServiceUtil;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.orcs.rest.model.TransactionEndpoint;
import org.eclipse.osee.orcs.rest.model.transaction.BranchLocation;
import org.eclipse.osee.orcs.rest.model.transaction.TransferDBType;
import org.eclipse.osee.orcs.rest.model.transaction.TransferInitData;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

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

   @Rule
   public ExpectedException expectedEx = ExpectedException.none();

   @Test
   public void testEmptyManifest() {
      expectedEx.expect(OseeCoreException.class);
      expectedEx.expectMessage("BuildId not found in:");

      try (InputStream inputStream = OsgiUtil.getResourceAsStream(getClass(), emptyManifest);) {
         transactionEndpoint.uploadTransferFile(inputStream);
      } catch (IOException ex) {
         throw new OseeCoreException("IOException has occured: " + ex);
      }

   }

   @Test
   public void testEmptyTransferFile() {
      expectedEx.expect(OseeCoreException.class);
      expectedEx.expectMessage("Exception while verifying manifest and transaction files");

      try (InputStream inputStream = OsgiUtil.getResourceAsStream(getClass(), emptyFile);) {
         transactionEndpoint.uploadTransferFile(inputStream);
      } catch (IOException ex) {
         throw new OseeCoreException("IOException has occured: " + ex);
      }

   }

   @Test
   public void testIncorrectExportId() {
      expectedEx.expect(OseeCoreException.class);
      expectedEx.expectMessage("The export ID is not valid. This id is not matched or there is more than one in db.");
      try (InputStream inputStream = OsgiUtil.getResourceAsStream(getClass(), incorrectExportId);) {
         transactionEndpoint.uploadTransferFile(inputStream);
      } catch (IOException ex) {
         throw new OseeCoreException("IOException has occured: " + ex);
      }
   }

   @Test
   public void testIncorrectManifest() {
      expectedEx.expect(OseeCoreException.class);
      //Expects error to say this import is missing a file
      expectedEx.expectMessage("Missing ");

      try (InputStream inputStream = OsgiUtil.getResourceAsStream(getClass(), incorrectManifest);) {
         transactionEndpoint.uploadTransferFile(inputStream);
      } catch (IOException ex) {
         throw new OseeCoreException("IOException has occured: " + ex);
      }

   }

   @Test
   public void testIncorrectFolderFormat() {
      expectedEx.expect(OseeCoreException.class);
      //Expects error to say this import is missing a file
      expectedEx.expectMessage("Missing ");
      try (InputStream inputStream = OsgiUtil.getResourceAsStream(getClass(), incorrectFileFormat);) {
         transactionEndpoint.uploadTransferFile(inputStream);
      } catch (IOException ex) {
         throw new OseeCoreException("IOException has occured: " + ex);
      }

   }

   private static TransactionId getMaxTransaction(BranchId branchId) {
      return TransactionManager.getTransactionsForBranch(branchId).get(0);
   }
}