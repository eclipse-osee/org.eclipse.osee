/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.orcs.rest.model.transaction;

import static org.eclipse.osee.orcs.rest.model.transaction.TransferTupleTypes.ExportedBranch;
import static org.eclipse.osee.orcs.rest.model.transaction.TransferTupleTypes.TransferFile;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.search.TupleQuery;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Huy A. Tran
 */

public class TransactionTransferManifest {
   private TransactionId exportId = null;
   private TransactionId buildId = null;
   private List<TransferBranch> transferBranchList;
   public String path;
   private final XResultData results;
   private final int PURGE_TXS_LIMIT = 100;

   public TransactionTransferManifest() {
      this.results = new XResultData();
   }

   public List<TransferBranch> getTransferBranches() {
      if (transferBranchList == null) {
         transferBranchList = new ArrayList<>();
      }
      return transferBranchList;
   }

   public void addTransferBranch(TransferBranch tb) {
      if (transferBranchList == null) {
         transferBranchList = new ArrayList<>();
      }
      transferBranchList.add(tb);
   }

   public XResultData parse(String dirName) {
      transferBranchList = new ArrayList<>();

      this.path = dirName;
      File manifestFile = new File(String.format("%s%s%s", path, File.separator, "manifest.md"));
      try {
         String manifest = Lib.fileToString(manifestFile);
         String[] mdLines = manifest.split("\n");
         if (mdLines[0].toLowerCase().contains("buildid")) {
            this.buildId = TransactionId.valueOf(mdLines[0].split(":")[1].trim());
         } else {
            results.errorf("BuildId not found in: %s ", mdLines[0]);
            return results;
         }
         if (mdLines[1].toLowerCase().contains("exportid")) {
            this.exportId = TransactionId.valueOf(mdLines[1].split(":")[1].trim());
         } else {
            results.errorf("ExportId not found in: %s ", mdLines[1]);
            return results;
         }
         // skip lines 3,4 since they should be the table header
         boolean done = false;
         int i = 4;
         TransferBranch transBranch = null;
         while (!done) {
            if (mdLines[i].toLowerCase().contains("prev")) {
               if (transBranch != null) {
                  // finish off previous branch
                  this.transferBranchList.add(transBranch);
               }
               transBranch = createTransferBranchFromRow(mdLines[i]);
            } else if (mdLines[i].toLowerCase().contains("cur")) {
               results.errorf("not accepting this type %s", mdLines[i]);
            } else if (mdLines[i].toLowerCase().contains("add")) {
               addRowToTransferBranch(transBranch, mdLines[i], TransferOpType.ADD);
            } else if (mdLines[i].toLowerCase().contains("empty")) {
               addRowToTransferBranch(transBranch, mdLines[i], TransferOpType.EMPTY);
            } else if (mdLines[i].length() < 3) {
               done = true;
               this.transferBranchList.add(transBranch);
            } else {
               results.errorf("unhandled type in row : %s ", mdLines[i]);
               return results;
            }
            ++i;
         }
         // TODO possibly handle directories/contents
      } catch (Exception e) {
         results.errorf("%s",
            String.format("IO Exception while verifying manifest and transaction files. %s ", e.getMessage()));
      }

      return results;
   }

   private TransferBranch createTransferBranchFromRow(String row) {
      String[] mdCols = row.split("\\|");
      TransferBranch transBranch = new TransferBranch(BranchId.valueOf(mdCols[1].trim()));
      transBranch.setUniqueTx(TransactionId.valueOf(mdCols[3].trim()));

      TransferTransaction trans = new TransferTransaction(transBranch.getBranchId(), transBranch.getPrevTx(),
         TransactionId.valueOf(mdCols[3].trim()), TransferOpType.PREV_TX);
      transBranch.addTransferTransaction(trans);
      return transBranch;
   }

   private void addRowToTransferBranch(TransferBranch transBranch, String row, TransferOpType type) {
      String[] mdCols = row.split("\\|");
      TransactionId sourceTx = TransactionId.valueOf(mdCols[2].trim());
      TransactionId uniqueTx = TransactionId.valueOf(mdCols[3].trim());
      TransferTransaction trans = new TransferTransaction(transBranch.getBranchId(), sourceTx, uniqueTx, type);
      transBranch.addTransferTransaction(trans);
   }

   /*
    * verify all add transactions having the json files verify all prevTx are matched with from db.
    */
   public XResultData validate(TupleQuery tupleQuery) {
      ArrayList<String> dirs = Lib.readListFromDir(path, null);
      try {
         //verify exportID
         if (!isExportIdValid(tupleQuery, this.exportId)) {
            results.error("The export ID is not valid. This id is not matched or there is more than one in db. ");
            return results;
         }

         //verify transactions and the json files
         for (TransferBranch tb : transferBranchList) {
            BranchId branchId = tb.getBranchId();
            //looking for directory
            int index = -1;
            for (int j = 0; j < dirs.size(); j++) {
               if (branchId.toString().equals(dirs.get(j))) {
                  index = j;
                  break;
               }
            }
            if (index == -1) {
               results.error(String.format("Missing %s directory. ", branchId.toString()));
               return results;
            }

            ArrayList<String> tempfiles =
               Lib.readListFromDir(String.format("%s%s%s", path, File.separator, dirs.get(index)), null);
            for (TransferTransaction transTx : tb.getTxList()) {
               if (transTx.getTransferOp().equals(TransferOpType.ADD)) {
                  String fileString = transTx.getSourceTransId().toString();
                  if (!tempfiles.contains(fileString)) {
                     results.errorf("Missing %s.json under %s ", fileString, tb.getBranchId().toString());
                     return results;
                  }
               }
            }

            //Verify prevTX
            // get max tx from tuple table and check to see if it matches the previous tx from the manifest
            BranchLocation branchLoc = getMaxTransactionIdFromBranchLocations(tupleQuery, branchId);
            if (!branchLoc.getUniqueTxId().equals(tb.getUniqueTx())) {
               results.errorf("PrevTX of branch %s is not matched. ", tb.getBranchId().toString());
               return results;
            }
         }

      } catch (Exception e) {
         results.errorf("%s",
            String.format("IO Exception while verifying manifest and transaction files. %s ", e.getMessage()));
      }

      return results;
   }

   public ArrayList<String> getAllImportedTransIds() {
      ArrayList<String> ids = new ArrayList<>();
      for (TransferBranch tb : transferBranchList) {
         for (TransferTransaction transTx : tb.getTxList()) {
            TransactionId tx = transTx.getImportedTransId();
            if (tx != null && tx.isValid()) {
               ids.add(tx.getIdString());
            }
         }
      }
      return ids;
   }

   public XResultData purgeAllImportedTransaction(OrcsApi orcsApi) {
      ArrayList<String> transIdList = getAllImportedTransIds();
      StringBuilder transIds = new StringBuilder("");
      //Purge up to PURGE_TXS_LIMIT
      try {
         for (int i = 0; i < transIdList.size(); i++) {
            if (i % PURGE_TXS_LIMIT == 0 && transIds.length() != 0) {
               orcsApi.getTransactionFactory().purgeTxs(transIds.toString());
               results.log(String.format("Purged succesfully the transaction ids: %s. ", transIds.toString()));
               //transIds.setLength(0);
               transIds = new StringBuilder("");
            }
            if (transIds.length() == 0) {
               transIds.append(transIdList.get(i));
            } else {
               transIds.append(",").append(transIdList.get(i));
            }
         }
         if (transIds.length() != 0) {
            orcsApi.getTransactionFactory().purgeTxs(transIds.toString()); //for test must be removed
            results.log(String.format("Purged succesfully the transaction ids: %s. ", transIds.toString()));
         }
      } catch (Exception e) {
         results.errorf("%s", String.format("Roll back failed while purging transaction ids %s. ", e.getMessage()));
      }

      return results;
   }

   public XResultData importAllTransactions(OrcsApi orcsApi, IResourceManager resourceManager) {
      //import transactions to branchIds
      try {
         for (TransferBranch tb : transferBranchList) {
            BranchId branchId = tb.getBranchId();

            String current = "";
            try {
               for (TransferTransaction transTx : tb.getTxList()) {
                  TransferOpType op = transTx.getTransferOp();
                  if (TransferOpType.ADD.equals(op)) {
                     File transFile = new File(String.format("%s%s%s%s%s.json", this.path, File.separator,
                        branchId.toString(), File.separator, transTx.getSourceTransId().toString()));
                     if (transFile.exists()) {
                        current = transFile.getName();
                        String transStr = Lib.fileToString(transFile);
                        TransactionBuilderDataFactory txBdf =
                           new TransactionBuilderDataFactory(orcsApi, resourceManager);
                        TransactionBuilder trans = txBdf.loadFromJson(transStr);
                        TransactionToken token = trans.commit();
                        if (token.isInvalid()) {
                           results.errorf("Failed at %s - %s. ", branchId.toString(), current);
                           break;
                        }
                        transTx.setImportedTransId(token);
                     }
                  }
               }
            } catch (Exception e) {
               results.errorf("Failed at %s - %s.json: %s. ", branchId.toString(), current, e.getMessage());
            }

            if (results.isFailed()) {
               break;
            }
         }

      } catch (Exception e) {
         results.error(e.getMessage());
      }
      return results;
   }

   // Query Branch Base Transaction based off supplied branch given
   // For the branch configured in the export ID return the maximum base transaction ID for that branch.
   private final BranchLocation getMaxTransactionIdFromBranchLocations(TupleQuery tupleQuery, BranchId branchid) {
      Pair<TransactionId, TransactionId> pair = new Pair<>(null, null);
      List<BranchLocation> fileBranchLocs = new ArrayList<>();
      tupleQuery.getTuple4E2E3E4FromE1(TransferFile, CoreBranches.COMMON, branchid, (E2, E3, E4) -> {
         BranchLocation bl = new BranchLocation();
         bl.setBranchId(branchid);
         bl.setTransferOp(E2);
         bl.setBaseTxId(E3);
         bl.setUniqueTxId(E4);
         fileBranchLocs.add(bl);
      });

      for (BranchLocation bl : fileBranchLocs) {
         if (pair.getFirst() == null) {
            if (bl.getTransferOp().equals(TransferOpType.PREV_TX)) {
               pair = new Pair<>(bl.getBaseTxId(), bl.getBaseTxId());
            } else {
               pair = new Pair<>(bl.getBaseTxId(), bl.getUniqueTxId());
            }
         } else {
            TransactionId test = pair.getFirst();
            //in case initial prevtx from source is greater than imported transactions
            if (test.isLessThan(bl.getBaseTxId()) && !bl.getTransferOp().equals(TransferOpType.PREV_TX)) {
               pair = new Pair<>(bl.getBaseTxId(), bl.getUniqueTxId());
            }
         }
      }

      BranchLocation bl = new BranchLocation();
      bl.setBranchId(branchid);
      bl.setBaseTxId(pair.getFirst());
      bl.setUniqueTxId(pair.getSecond());

      return bl;
   }

   /*
    * check only this destination export id exists in db
    */
   private boolean isExportIdValid(TupleQuery tupleQuery, TransactionId exportId) {
      List<TransactionId> exportIdList = new ArrayList<>();
      int intDbType = TransferDBType.DESTINATION.ordinal();
      tupleQuery.getTuple4E1FromTupleType(ExportedBranch, TransferTupleTypes.LongExportedDBType,
         Long.valueOf(intDbType), exportIdList::add);

      for (TransactionId id : exportIdList) {
         if (!id.equals(exportId)) {
            return false;
         }
      }
      return (exportIdList.size() == 1 ? true : false);
   }

   public XResultData addAllImportedTransToTupleTable(OrcsApi orcsApi) {
      try {
         TransactionBuilder txTransTuple =
            orcsApi.getTransactionFactory().createTransaction(CoreBranches.COMMON, "Add imported transaction tuples.");
         for (TransferBranch tb : transferBranchList) {
            for (TransferTransaction txTrans : tb.getTxList()) {
               if (TransferOpType.ADD.equals(txTrans.getTransferOp())) {
                  txTransTuple.addTuple4(TransferFile, tb.getBranchId(), txTrans.getTransferOp(),
                     txTrans.getImportedTransId(), txTrans.getSourceUniqueTrans());
               }
            }
         }
         txTransTuple.commit();
      } catch (Exception e) {
         results.error(String.format("Error while Adding transaction tuples to database %s ", e.getMessage()));
      }
      return results;
   }

   public TransactionId getExportID() {
      return this.exportId;
   }

   public void setExportID(TransactionId exportId) {
      this.exportId = exportId;
   }

   public TransactionId getBuildID() {
      return this.buildId;
   }

   public void setBuildID(TransactionId buildId) {
      this.buildId = buildId;
   }
}
