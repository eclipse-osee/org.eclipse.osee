package org.eclipse.osee.orcs.rest.model.transaction;

import static org.eclipse.osee.orcs.rest.model.transaction.TransferTupleTypes.ExportedBranch;
import static org.eclipse.osee.orcs.rest.model.transaction.TransferTupleTypes.TransferFile;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.search.TupleQuery;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Huy A. Tran
 */

public class TransactionTransferManifest {
   public TransactionId exportId = null;
   public TransactionId buildId = null;
   public List<TransferBranch> transferBranchList;
   public String path;
   private final XResultData results;
   private final int PURGE_TXS_LIMIT = 100;

   public TransactionTransferManifest() {
      this.results = new XResultData();
   }

   public XResultData Parse(String dirName, TupleQuery tupleQuery) {
      transferBranchList = new ArrayList<>();

      this.path = dirName;
      File manifestFile = new File(String.format("%s%s%s", path, File.separator, "manifest.md"));
      try {
         String manifest = Lib.fileToString(manifestFile);
         String[] mdLines = manifest.split("\n");
         TransferBranch transList = null;
         TransferTransaction trans = null;
         boolean noCurTx = false;
         for (int i = 0; i < mdLines.length; i++) {
            if (mdLines[i].contains("BuildID")) {
               this.buildId = TransactionId.valueOf(mdLines[i].split(":")[1].trim());
               continue;
            }

            if (mdLines[i].contains("ExportID")) {
               this.exportId = TransactionId.valueOf(mdLines[i].split(":")[1].trim());
               continue;
            }

            if (mdLines[i].contains("PrevTX")) {
               if (noCurTx) {
                  transList.setCurTX(trans.sourceTransId);
                  this.transferBranchList.add(transList);
               }
               transList = new TransferBranch(BranchId.valueOf(mdLines[i].split("\\|")[1].trim()));
               noCurTx = true;
               transList.setPrevTX(TransactionId.valueOf(mdLines[i].split("\\|")[2].trim()));

               trans = new TransferTransaction(BranchId.valueOf(mdLines[i].split("\\|")[1].trim()),
                  TransactionId.valueOf(mdLines[i].split("\\|")[2].trim()),
                  TransactionId.valueOf(mdLines[i].split("\\|")[3].trim()), TransferOpType.PREV_TX);

               if (transList.txList.contains(trans)) {
                  results.error(String.format("Transaction %s has a duplication.", trans.sourceTransId.toString()));
                  break;
               }
               transList.txList.add(trans);
               continue;
            }

            if (mdLines[i].contains("ADD")) {
               trans = new TransferTransaction(BranchId.valueOf(mdLines[i].split("\\|")[1].trim()),
                  TransactionId.valueOf(mdLines[i].split("\\|")[2].trim()),
                  TransactionId.valueOf(mdLines[i].split("\\|")[3].trim()), TransferOpType.ADD);

               if (transList.txList.contains(trans)) {
                  results.error(String.format("Transaction %s has a duplication.", trans.sourceTransId.toString()));
                  break;
               }
               transList.txList.add(trans);
               continue;
            }

            if (mdLines[i].contains("EMPTY")) {
               trans = new TransferTransaction(BranchId.valueOf(mdLines[i].split("\\|")[1].trim()),
                  TransactionId.valueOf(mdLines[i].split("\\|")[2].trim()),
                  TransactionId.valueOf(mdLines[i].split("\\|")[3].trim()), TransferOpType.EMPTY);

               if (transList.txList.contains(trans)) {
                  results.error(String.format("Transaction %s has a duplication.", trans.sourceTransId.toString()));
                  break;
               }
               transList.txList.add(trans);
               continue;
            }

            if (mdLines[i].contains("CurTX")) {
               trans = new TransferTransaction(BranchId.valueOf(mdLines[i].split("\\|")[1].trim()),
                  TransactionId.valueOf(mdLines[i].split("\\|")[2].trim()),
                  TransactionId.valueOf(mdLines[i].split("\\|")[3].trim()), TransferOpType.CUR_TX);

               if (transList.txList.contains(trans)) {
                  results.error(String.format("Transaction %s has a duplication.", trans.sourceTransId.toString()));
                  break;
               }
               transList.txList.add(trans);
               transList.setCurTX(trans.sourceTransId);
               this.transferBranchList.add(transList);
               noCurTx = false;
               continue;
            }

            if (mdLines[i].contains("List of Directories")) {
               break;
            }
         }

      } catch (IOException e) {
         results.errorf("%s", String.format("IO Exception while reading manifest. %s", e.getMessage()));
      }

      if (!results.isFailed()) {
         Validate(tupleQuery);
      }

      return results;
   }

   /*
    * verify all add transactions having the json files verify all prevTx are matched with from db.
    */

   public XResultData Validate(TupleQuery tupleQuery) {
      ArrayList<String> dirs = Lib.readListFromDir(path, null);
      try {
         for (int i = 0; i < transferBranchList.size(); i++) {
            BranchId branchId = transferBranchList.get(i).branchId;
            //looking for directory
            int index = -1;
            for (int j = 0; j < dirs.size(); j++) {
               if (transferBranchList.get(i).getBranchId().toString().equals(dirs.get(j))) {
                  index = j;
                  break;
               }
            }
            if (index == -1) {
               results.error(String.format("Missing %s directory", transferBranchList.get(i).getBranchId().toString()));
               return results;
            }
            //looking for json files
            ArrayList<String> tempfiles =
               Lib.readListFromDir(String.format("%s%s%s", path, File.separator, dirs.get(index)), null);
            for (int j = 0; j < transferBranchList.get(i).txList.size(); j++) {
               if (!tempfiles.contains(transferBranchList.get(i).txList.get(
                  j).sourceTransId.toString()) && transferBranchList.get(i).txList.get(j).transferOp.equals(
                     TransferOpType.ADD)) {
                  results.error(String.format("Missing %s.json under %s",
                     transferBranchList.get(i).txList.get(j).sourceTransId.toString(),
                     transferBranchList.get(i).branchId.toString()));
                  return results;
               }
            }

            //Verify prevTX, ImportId,
            List<TransactionId> txIds = new ArrayList<>();
            //tupleQuery.getTuple4E3E4FromE1E2(TupleType, branchId, e1, e2, consumer);
            tupleQuery.getTuple4E3E4FromE1E2(TransferFile, CoreBranches.COMMON, branchId, TransferOpType.PREV_TX,
               (E3, E4) -> {
                  txIds.add(E3);
               });
            if (txIds.isEmpty()) {
               //if there is no preTx add the new prevTx so that it can pass the validation
               //txIds.add(TransationId.valueOf(strManBaseTxId));
               results.errorf("%s", String.format("Can not get Prev_TX of %s from database.", branchId.toString()));
               return results;
            }
            if (!txIds.get(0).toString().equals(transferBranchList.get(i).prevTx.toString())) {
               results.errorf("%s",
                  String.format("Previous Tx %s of %s does not match with current value: %s in database.",
                     transferBranchList.get(i).prevTx.toString(), branchId.toString(), txIds.get(0).toString()));
               return results;
            }

            List<GammaId> tuples = new ArrayList<>();
            //tupleQuery.getTuple4GammaFromE1E2(TransferFile, CoreBranches.COMMON, branchId, TransferOpType.PREV_TX, tuples::add);
            tupleQuery.getTuple4GammaFromE1E2(TransferFile, branchId, branchId, TransferOpType.PREV_TX, tuples::add);

            if (tuples.isEmpty()) {
               //results.errorf("%s", String.format("Can not get gamma Id from %s and Prev_TX.", branchId.toString()));
               //return results; must add it back
               results.log(String.format("Can not get gamma Id from %s.", branchId.toString()));
            } else {
               transferBranchList.get(i).setGammaID(tuples.get(0));
            }

            //verify export id
            //get export id should be queried by system level and return only one not for every branch ???
            txIds.clear();
            tupleQuery.getTuple4E3E4FromE1E2(ExportedBranch, branchId, exportId, branchId, (E3, E4) -> {
               txIds.add(E3);
            });

            if (txIds.isEmpty()) {
               results.log(String.format("Can not get exportId from database of branch %s.", branchId.toString()));
               //return results; must add it back
            }

         }
      } catch (Exception e) {
         results.errorf("%s",
            String.format("IO Exception while verifying manifest and transaction files. %s", e.getMessage()));
      }

      return results;
   }

   public ArrayList<String> GetAllImportedTransIds() {
      ArrayList<String> ids = new ArrayList<String>();
      for (int i = 0; i < transferBranchList.size(); i++) {
         for (int j = 0; j < transferBranchList.get(i).txList.size(); j++) {
            if (transferBranchList.get(i).txList.get(j).importedTransId != null) {
               ids.add(transferBranchList.get(i).txList.get(j).importedTransId.toString());
            }
         }
      }
      return ids;
   }

   public XResultData PurgeAllImportedTransaction(OrcsApi orcsApi) {
      ArrayList<String> transIdList = GetAllImportedTransIds();
      StringBuilder transIds = new StringBuilder("");
      //Purge up to PURGE_TXS_LIMIT
      try {
         for (int i = 0; i < transIdList.size(); i++) {
            if (i % PURGE_TXS_LIMIT == 0 && transIds.length() != 0) {
               orcsApi.getTransactionFactory().purgeTxs(transIds.toString());
               results.log(String.format("Purged succesfully the transaction ids: %s.", transIds.toString()));
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
            results.log(String.format("Purged succesfully the transaction ids: %s.", transIds.toString()));
         }
      } catch (Exception e) {
         results.errorf("%s", String.format("Roll back failed while purging transaction ids %s.", e.getMessage()));
      }

      return results;
   }

   public XResultData ImportAllTransactions(OrcsApi orcsApi, IResourceManager resourceManager) {
      //import transactions to branchIds
      try {
         for (int i = 0; i < transferBranchList.size(); i++) {
            BranchId branchId = transferBranchList.get(i).branchId;

            int j = 0;
            try {
               for (j = 0; j < transferBranchList.get(i).txList.size(); j++) {
                  if (transferBranchList.get(i).txList.get(j).transferOp.equals(
                     TransferOpType.PREV_TX) || transferBranchList.get(i).txList.get(j).transferOp.equals(
                        TransferOpType.EMPTY)) {
                     continue;
                  }
                  File transFile =
                     new File(String.format("%s%s%s%s%s.json", this.path, File.separator, branchId.toString(),
                        File.separator, transferBranchList.get(i).txList.get(j).sourceTransId.toString()));
                  if (!transFile.exists() && transferBranchList.get(i).txList.get(j).transferOp.equals(
                     TransferOpType.CUR_TX)) {
                     continue;
                  }

                  String transStr = Lib.fileToString(transFile);
                  TransactionBuilderDataFactory txBdf = new TransactionBuilderDataFactory(orcsApi, resourceManager);
                  TransactionBuilder trans = txBdf.loadFromJson(transStr);

                  //trans.createArtifact(CoreArtifactTypes.AcronymPlainText, "Test Artifact");

                  //trans.addkeyValueOps(id, name);
                  //make small change to get the transaction id
                  TransactionToken token = trans.commit();
                  if (token.getId() < 1) {
                     results.errorf("Failed at %s - %s.json: Transaction id was %s.", branchId.toString(),
                        transferBranchList.get(i).txList.get(j).sourceTransId.toString(), token.getIdString());
                     break;
                  }
                  //transIdList.add(token.getIdString());
                  transferBranchList.get(i).txList.get(j).setImportedTransId(
                     TransactionId.valueOf(token.getIdString()));
               }
            } catch (Exception e) {
               results.errorf("Failed at %s - %s.json: %s.", branchId.toString(),
                  transferBranchList.get(i).txList.get(j).sourceTransId.toString(), e.getMessage());
            }

            if (results.isFailed()) {
               break;
            }
         }

      } catch (Exception e) {
         results.errorf("%s", e.getMessage());
      }
      return results;
   }

   public XResultData UpdatePrevTXs(OrcsApi orcsApi) {
      try {
         TransactionBuilder txDel =
            orcsApi.getTransactionFactory().createTransaction(CoreBranches.COMMON, "Delete Tuple4 Prev TX");
         for (int i = 0; i < transferBranchList.size(); i++) {
            BranchId branchId = transferBranchList.get(i).branchId;
            GammaId id = transferBranchList.get(i).gammaId;
            if (id != null) {
               txDel.deleteTuple4(id);
            }
         }
         txDel.commit();

         TransactionBuilder txUpdt =
            orcsApi.getTransactionFactory().createTransaction(CoreBranches.COMMON, "Update Tuple4 Prev TX");
         for (int i = 0; i < transferBranchList.size(); i++) {
            if (transferBranchList.get(i).curTx.isValid()) {
               BranchId branchId = transferBranchList.get(i).branchId;
               txUpdt.addTuple4(TransferFile, branchId, TransferOpType.PREV_TX, transferBranchList.get(i).curTx,
                  TransactionId.valueOf(Lib.generateUuid()));
            }
         }
         txUpdt.commit();
      } catch (Exception e) {
         results.error(String.format("Error while Updating PrevTX to database %s", e.getMessage()));
      }
      return results;
   }

   public void setExportID(TransactionId exportId) {
      this.exportId = exportId;
   }

   public void setBuildID(TransactionId buildId) {
      this.buildId = buildId;
   }
}
