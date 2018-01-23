/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.exchange.transform;

import java.io.File;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.jdk.core.text.rules.ReplaceAll;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.HashCollectionSet;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.db.internal.exchange.ExchangeUtil;
import org.eclipse.osee.orcs.db.internal.exchange.TxCurrentsOpFactory;
import org.eclipse.osee.orcs.db.internal.exchange.handler.ExportItem;
import org.eclipse.osee.orcs.db.internal.util.Address;
import org.osgi.framework.Version;

/**
 * @author Roberto E. Escobar
 */
public class V0_9_2Transformer implements IOseeExchangeVersionTransformer {
   private static final Version MAX_VERSION = new Version("0.9.2");

   private final Map<ModificationType, ModificationType[]> allowedStates =
      new HashMap<ModificationType, ModificationType[]>();

   public V0_9_2Transformer() {
      ModificationType[] FROM_NEW_OR_INTRODUCED =
         new ModificationType[] {ModificationType.DELETED, ModificationType.MERGED};
      ModificationType[] END_STATE = new ModificationType[0];
      allowedStates.put(ModificationType.NEW, FROM_NEW_OR_INTRODUCED);
      allowedStates.put(ModificationType.INTRODUCED, FROM_NEW_OR_INTRODUCED);
      allowedStates.put(ModificationType.MERGED, new ModificationType[] {ModificationType.DELETED});
      allowedStates.put(ModificationType.DELETED, END_STATE);
      allowedStates.put(ModificationType.MODIFIED, END_STATE);
   }

   @Override
   public Version applyTransform(ExchangeDataProcessor processor, Log logger) {
      List<Long> branchUuids = convertBranchTable(processor);

      Map<Long, Long> artifactGammaToNetGammaId = convertArtifactAndConflicts(processor);
      consolidateTxsAddressing(processor, ExportItem.OSEE_TXS_DATA, branchUuids, artifactGammaToNetGammaId);

      HashCollection<String, String> tableToColumns = new HashCollection<>();
      tableToColumns.put("osee_artifact", "<column id=\"gamma_id\" type=\"NUMERIC\" />\n");
      tableToColumns.put("osee_branch", "<column id=\"baseline_transaction_id\" type=\"INTEGER\" />\n");
      processor.transform(ExportItem.EXPORT_DB_SCHEMA, new DbSchemaRuleAddColumn(tableToColumns));

      processor.transform(ExportItem.EXPORT_MANIFEST,
         new ReplaceAll("<entry id=\"osee.artifact.version.data.xml[^<]+", ""));
      processor.deleteExportItem("osee.artifact.version.data.xml");
      return getMaxVersion();
   }

   @Override
   public Version getMaxVersion() {
      return MAX_VERSION;
   }

   @Override
   public void finalizeTransform(Log logger, OrcsSession session, JdbcClient jdbcClient, ExchangeDataProcessor processor) {
      try {
         TxCurrentsOpFactory.createTxCurrentsAndModTypesOp(logger, session, jdbcClient, false).call();
         TxCurrentsOpFactory.createTxCurrentsAndModTypesOp(logger, session, jdbcClient, true).call();
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   private List<Long> convertBranchTable(ExchangeDataProcessor processor) {
      Map<Long, Integer> branchToBaseTx = new HashMap<>(10000);
      processor.parse(ExportItem.OSEE_TX_DETAILS_DATA, new V0_9_2TxDetailsHandler(branchToBaseTx));
      processor.transform(ExportItem.OSEE_BRANCH_DATA, new V0_9_2BranchTransformer(branchToBaseTx));
      return new ArrayList<Long>(branchToBaseTx.keySet());
   }

   private Map<Long, Long> convertArtifactAndConflicts(ExchangeDataProcessor processor) {
      V0_9_2ArtifactVersionHandler handler = new V0_9_2ArtifactVersionHandler();
      processor.parse("osee.artifact.version.data.xml", handler);
      Map<Long, Long> artifactGammaToNetGammaId = handler.getArtifactGammaToNetGammaId();
      Map<Integer, Long> artIdToNetGammaId = handler.getArtIdToNetGammaId();

      processor.transform(ExportItem.OSEE_ARTIFACT_DATA, new V0_9_2ArtifactDataTransformer(artIdToNetGammaId));
      processor.transform(ExportItem.OSEE_CONFLICT_DATA, new V0_9_2ConflictTransformer(artifactGammaToNetGammaId));

      return artifactGammaToNetGammaId;
   }

   private void consolidateTxsAddressing(ExchangeDataProcessor processor, ExportItem exportItem, List<Long> branchUuids, Map<Long, Long> artifactGammaToNetGammaId) {
      File targetFile = processor.getDataProvider().getFile(exportItem);
      File tempFile = new File(Lib.changeExtension(targetFile.getPath(), "temp"));
      Writer fileWriter = null;
      HashCollectionSet<Long, Address> addressMap = new HashCollectionSet<>(TreeSet::new);
      V0_9_2TxsConsolidateParser transformer = new V0_9_2TxsConsolidateParser(artifactGammaToNetGammaId, addressMap);
      try {
         fileWriter = processor.startTransform(targetFile, tempFile, transformer);
         ExchangeUtil.readExchange(tempFile, transformer);

         for (long branchUuid : branchUuids) {
            transformer.setBranchId(branchUuid);
            ExchangeUtil.readExchange(tempFile, transformer);

            for (Long gammaId : addressMap.keySet()) {
               Collection<Address> addresses = addressMap.getValues(gammaId);
               fixAddressing(addresses);
               writeAddresses(transformer.getWriter(), addresses);
            }
            addressMap.clear();
         }
         tempFile.delete();
      } catch (Exception ex) {
         OseeCoreException.wrapAndThrow(ex);
      } finally {
         try {
            transformer.finish();
         } catch (Exception ex) {
            OseeCoreException.wrapAndThrow(ex);
         } finally {
            Lib.close(fileWriter);
         }
      }
   }

   private void writeAddresses(XMLStreamWriter writer, Collection<Address> addresses) throws XMLStreamException {
      for (Address address : addresses) {
         writer.writeStartElement("entry");
         writer.writeAttribute("branch_id", String.valueOf(address.getBranchId()));
         writer.writeAttribute("gamma_id", String.valueOf(address.getGammaId()));
         writer.writeAttribute("tx_current", String.valueOf(address.getCorrectedTxCurrent()));
         writer.writeAttribute("mod_type", address.getModType().getIdString());
         writer.writeAttribute("transaction_id", String.valueOf(address.getTransactionId()));
         writer.writeEndElement();
      }
   }

   private void fixAddressing(Collection<Address> addresses) {
      Iterator<Address> iterator = addresses.iterator();

      Address previousAddress = iterator.next();
      if (previousAddress.getModType() == ModificationType.MODIFIED) {
         previousAddress.setModType(ModificationType.NEW);
      }

      while (iterator.hasNext()) {
         previousAddress.setCorrectedTxCurrent(TxChange.NOT_CURRENT);
         Address address = iterator.next();
         ModificationType[] nextValidStates = getNextPossibleStates(previousAddress.getModType());
         if (!address.getModType().matches(nextValidStates)) {
            iterator.remove();
         }
         previousAddress = address;
      }
      previousAddress.setCorrectedTxCurrent(TxChange.getCurrent(previousAddress.getModType()));
   }

   private ModificationType[] getNextPossibleStates(ModificationType state) {
      ModificationType[] nextAllowed = allowedStates.get(state);
      if (nextAllowed == null) {
         throw new OseeStateException("Unexcepted modification type [%s]", state.toString());
      }
      return nextAllowed;
   }
}
