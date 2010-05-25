/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.branch.management.exchange.transform;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.eclipse.osee.framework.branch.management.exchange.handler.ExportItem;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.database.operation.Address;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.osgi.framework.Version;
import org.xml.sax.ContentHandler;

/**
 * @author Roberto E. Escobar
 */
public class V0_9_2Transformer implements IOseeExchangeVersionTransformer {
   private static final Version MAX_VERSION = new Version("0.9.2");

   private static final Map<ModificationType, ModificationType[]> ALLOWED_STATES =
         new HashMap<ModificationType, ModificationType[]>();

   public V0_9_2Transformer() {
   }

   @Override
   public String applyTransform(ExchangeDataProcessor processor) throws OseeCoreException {
      List<Integer> branchIds = convertBranchTable(processor);

      Set<Long> artifactGammmIds = new HashSet<Long>();

      convertArtifactAndConflicts(processor, artifactGammmIds);
      consolidateTxsAddressing(processor, ExportItem.OSEE_TXS_DATA, branchIds, artifactGammmIds);
      consolidateTxsAddressing(processor, ExportItem.OSEE_TXS_ARCHIVED_DATA, branchIds, artifactGammmIds);

      processor.deleteExportItem("osee.artifact.data.xml");
      processor.renameExportItem("osee.arts.data.xml", "osee.artifact.data.xml");
      processor.deleteExportItem("osee.artifact.version.data.xml");

      return getMaxVersion().toString();
   }

   @Override
   public Version getMaxVersion() {
      return MAX_VERSION;
   }

   @Override
   public void finalizeTransform(ExchangeDataProcessor processor) throws Exception {
   }

   private List<Integer> convertBranchTable(ExchangeDataProcessor processor) throws OseeCoreException {
      Map<Integer, Integer> branchToBaseTx = new HashMap<Integer, Integer>(10000);
      processor.parse(ExportItem.OSEE_TX_DETAILS_DATA, new V0_9_2TxDetailsHandler(branchToBaseTx));
      processor.transform(ExportItem.OSEE_BRANCH_DATA, new V0_9_2BranchTransformer(branchToBaseTx));
      return new ArrayList<Integer>(branchToBaseTx.keySet());
   }

   private void convertArtifactAndConflicts(ExchangeDataProcessor processor, Set<Long> netGammaIds) throws OseeCoreException {
      Map<Long, Long> obsoleteGammaToNetGammaId = new HashMap<Long, Long>();

      Map<Integer, Long> artIdToNetGammaId = new HashMap<Integer, Long>(14000);
      processor.parse("osee.artifact.version.data.xml", new V0_9_2ArtifactVersionHandler(artIdToNetGammaId,
            obsoleteGammaToNetGammaId));
      processor.copyExportItem("osee.artifact.data.xml", "osee.arts.data.xml");
      processor.transform("osee.arts.data.xml", new V0_9_2ArtifactDataTransformer(artIdToNetGammaId));

      processor.transform(ExportItem.OSEE_CONFLICT_DATA, new V0_9_2ConflictTransformer(artIdToNetGammaId));

      processor.transform(ExportItem.OSEE_TXS_DATA, new V0_9_2TxsNetGammaTransformer(obsoleteGammaToNetGammaId));

      netGammaIds.addAll(obsoleteGammaToNetGammaId.values());
   }

   private void consolidateTxsAddressing(ExchangeDataProcessor processor, ExportItem exportItem, List<Integer> branchIds, Set<Long> artifactGammmIds) throws OseeCoreException {
      String temporaryFile = Lib.changeExtension(exportItem.getFileName(), "temp");
      processor.renameExportItem(exportItem.getFileName(), temporaryFile);
      try {
         XMLOutputFactory factory = XMLOutputFactory.newInstance();
         XMLStreamWriter writer = factory.createXMLStreamWriter(new FileWriter(exportItem.getFileName()));
         writer.writeStartDocument();
         writer.writeStartElement("data");

         for (Integer branchId : branchIds) {
            HashCollection<Long, Address> addressMap = new HashCollection<Long, Address>(false, TreeSet.class);

            ContentHandler handler =
                  new V0_9_2TxsConsolidateParser(writer, branchId, branchIds, artifactGammmIds, addressMap);
            processor.parse(temporaryFile, handler);

            for (Long key : addressMap.keySet()) {
               Collection<Address> addresses = addressMap.getValues(key);
               fixAddressing(addresses);
               writeAddresses(writer, addresses);
            }
         }

         writer.writeEndElement();
         writer.writeEndDocument();
         processor.deleteExportItem(temporaryFile);
      } catch (Exception ex) {
         processor.renameExportItem(temporaryFile, exportItem.getFileName());
         OseeExceptions.wrapAndThrow(ex);
      }
   }

   private void writeAddresses(XMLStreamWriter writer, Collection<Address> addresses) throws XMLStreamException {
      for (Address address : addresses) {
         writer.writeStartElement("entry");
         writer.writeAttribute("branch_id", String.valueOf(address.getBranchId()));
         writer.writeAttribute("gamma_id", String.valueOf(address.getGammaId()));
         writer.writeAttribute("tx_current", String.valueOf(address.getCorrectedTxCurrent()));
         writer.writeAttribute("mod_type", String.valueOf(address.getModType()));
         writer.writeAttribute("transaction_id", String.valueOf(address.getTransactionId()));
         writer.writeEndElement();
      }
   }

   private void fixAddressing(Collection<Address> addresses) throws OseeStateException {
      Iterator<Address> iterator = addresses.iterator();

      Address previousAddress = iterator.next();
      if (previousAddress.getModType() == ModificationType.MODIFIED) {
         previousAddress.setModType(ModificationType.NEW);
      }

      while (iterator.hasNext()) {
         Address address = iterator.next();
         ModificationType[] nextValidStates = getNextPossibleStates(previousAddress.getModType());
         if (!address.getModType().matches(nextValidStates)) {
            iterator.remove();
         }
      }
   }

   private ModificationType[] getNextPossibleStates(ModificationType state) throws OseeStateException {
      if (ALLOWED_STATES.isEmpty()) {
         final ModificationType[] FROM_NEW_OR_INTRODUCED =
               new ModificationType[] {ModificationType.DELETED, ModificationType.MERGED};
         ALLOWED_STATES.put(ModificationType.NEW, FROM_NEW_OR_INTRODUCED);
         ALLOWED_STATES.put(ModificationType.INTRODUCED, FROM_NEW_OR_INTRODUCED);
         ALLOWED_STATES.put(ModificationType.MERGED, new ModificationType[] {ModificationType.DELETED});
         ALLOWED_STATES.put(ModificationType.DELETED, new ModificationType[0]);
      }
      ModificationType[] nextAllowed = ALLOWED_STATES.get(state);
      if (nextAllowed == null) {
         throw new OseeStateException(String.format("Unexcepted modification type [%s]", state.toString()));
      }
      return nextAllowed;
   }
}