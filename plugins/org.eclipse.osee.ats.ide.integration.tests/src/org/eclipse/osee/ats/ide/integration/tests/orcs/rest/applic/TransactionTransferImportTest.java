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
import java.io.InputStream;
import java.util.Arrays;
import javax.ws.rs.core.Response;
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
import org.junit.Test;

/**
 * @author Torin Grenda
 */
public class TransactionTransferImportTest {

   private static TransactionEndpoint transactionEndpoint;
   private static TransferInitData testInitJson = new TransferInitData();
   private static String incorrectFileFormat =
      "support/TransferTestFiles/OSEEIncorrectFormatTransfer-20230727145800-259274267223164579_1.zip";
   private static String emptyFile =
      "support/TransferTestFiles/OSEEEmptyTransfer-20230727145800-259274267223164579_1.zip";
   private static String emptyManifest =
      "support/TransferTestFiles/OSEEEmptyManifestTransfer-20230727145800-259274267223164579_1.zip";
   private static String incorrectManifest =
      "support/TransferTestFiles/OSEEIncorrectManifestTransfer-20230727145800-259274267223164579_1.zip";
   private static String incorrectExportId =
      "support/TransferTestFiles/OSEEIncorrectExportIdTransfer-20230727145800-259274267223164579_1.zip";
   private static String successfulFile =
      "support/TransferTestFiles/OSEESuccessfulTransfer-20230727145800-259274267223164579_1.zip";

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
      testInitJson.setExportId(TransactionId.valueOf(259274267223164579L)); //Random Number
      testInitJson.setTransferDBType(TransferDBType.SOURCE);
      transactionEndpoint.initTransactionTransfer(testInitJson);
   }

   @Test
   public void testEmptyManifest() {
      try (InputStream inputStream = OsgiUtil.getResourceAsStream(getClass(), emptyManifest)) {
         //Call import on a export file containing just the Json files
         Response response = transactionEndpoint.uploadTransferFile(inputStream);
         //Assert failure for incorrect folder format
         assertTrue(response.toString().contains("Import failed: Manifest is missing data."));
      } catch (Exception ex) {
         throw new OseeCoreException("Failed to fetch Transfer file as input stream");
      }
   }

   @Test
   public void testIncorrectManifest() {
      try (InputStream inputStream = OsgiUtil.getResourceAsStream(getClass(), incorrectManifest)) {
         //Call import on a export file containing just the Json files
         Response response = transactionEndpoint.uploadTransferFile(inputStream);
         //Assert failure for incorrect folder format
         assertTrue(response.toString().contains("Import failed: Number of files does not match with Manifest."));
      } catch (Exception ex) {
         throw new OseeCoreException("Failed to fetch Transfer file as input stream");
      }
   }

   @Test
   public void testIncorrectFolderFormat() {
      try (InputStream incorrectInputStream = OsgiUtil.getResourceAsStream(getClass(), incorrectFileFormat)) {
         //Call import on a export file containing just the Json files
         Response response = transactionEndpoint.uploadTransferFile(incorrectInputStream);
         //Assert failure for incorrect folder format
         assertTrue(response.toString().contains("Import failed: Import file is not formatted correctly"));
      } catch (Exception ex) {
         throw new OseeCoreException("Failed to fetch Transfer file as input stream");
      }

      try (InputStream emptyInputStream = OsgiUtil.getResourceAsStream(getClass(), emptyFile)) {
         //Call import on a empty export file
         Response response = transactionEndpoint.uploadTransferFile(emptyInputStream);
         //Assert failure for empty file
         assertTrue(response.toString().contains("Import failed: Import file is not formatted correctly"));
      } catch (Exception ex) {
         throw new OseeCoreException("Failed to fetch Transfer file as input stream");
      }
   }

   @Test
   public void testIncorrectExportId() {
      try (InputStream inputStream = OsgiUtil.getResourceAsStream(getClass(), incorrectExportId)) {
         //Call import on a export file containing a incorrect export id
         Response response = transactionEndpoint.uploadTransferFile(inputStream);
         //Assert failure for incorrect export id
         assertTrue(response.toString().contains("Import failed: Incorrect export id supplied"));
      } catch (Exception ex) {
         throw new OseeCoreException("Failed to fetch Transfer file as input stream");
      }
   }

   @Test
   public void testDuplicateImport() {
      try (InputStream inputStream = OsgiUtil.getResourceAsStream(getClass(), successfulFile)) {
         //Call import on sample export file
         Response response = transactionEndpoint.uploadTransferFile(inputStream);
         //Assert the first transfer goes through successfully
         assertTrue(response.toString().contains("Successfully transferred the transaction ids"));
         //Attempt to call import on the same sample export file
         Response responseDuplicate = transactionEndpoint.uploadTransferFile(inputStream);
         //Assert failure for duplicate exportId
         assertTrue(
            responseDuplicate.toString().contains("Import failed: Import file has already previously been imported"));
         //Remove all newly added data
         databaseRollback();
      } catch (Exception ex) {
         throw new OseeCoreException("Failed to fetch Transfer file as input stream");
      }
   }

   @Test
   public void testSuccessfulImport() {
      try (InputStream inputStream = OsgiUtil.getResourceAsStream(getClass(), successfulFile)) {
         //Call import on sample export file
         Response response = transactionEndpoint.uploadTransferFile(inputStream);
         //Check the newly imported data
         assertTrue(response.toString().contains("Successfully transferred the transaction ids"));
         assertTrue(TransactionManager.getTransaction(2030L).isValid());
         assertTrue(TransactionManager.getTransaction(2031L).isValid());
         assertTrue(TransactionManager.getTransaction(2032L).isValid());
         assertTrue(TransactionManager.getTransaction(2033L).isValid());
         assertTrue(TransactionManager.getTransaction(2042L).isValid());
         assertTrue(TransactionManager.getTransaction(2041L).isValid());
         //Remove all newly added data
         databaseRollback();
      } catch (Exception ex) {
         throw new OseeCoreException("Failed to fetch Transfer file as input stream");
      }
   }

   private static TransactionId getMaxTransaction(BranchId branchId) {
      return TransactionManager.getTransactionsForBranch(branchId).get(0);
   }

   //NEEDS TO BE IMPLEMENTED
   private void databaseRollback() {
      //Not sure how to currently clean the database after each test that actually imports data
   }
}