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

import java.util.List;
import java.util.Set;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.database.operation.Address;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.io.xml.AbstractSaxHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Ryan D. Brooks
 */
public class V0_9_2TxsConsolidateParser extends AbstractSaxHandler {
   private final int targetBranchId;
   private final String targetBranchIdStr;
   private final Set<Long> netGammaIds;
   private final HashCollection<Long, Address> addressMap;
   private final XMLStreamWriter writer;
   private final boolean isWriteAllowed;
   private final List<Integer> branchIdsToTarget;
   private boolean deferWrite;

   public V0_9_2TxsConsolidateParser(XMLStreamWriter writer, int index, List<Integer> branchIdsToTarget, Set<Long> netGammaIds, HashCollection<Long, Address> addressMap) {
      this.branchIdsToTarget = branchIdsToTarget;
      this.targetBranchId = branchIdsToTarget.get(index);
      this.targetBranchIdStr = String.valueOf(targetBranchId);
      this.netGammaIds = netGammaIds;
      this.addressMap = addressMap;
      this.writer = writer;
      this.isWriteAllowed = index == 0;
   }

   @Override
   public void startElementFound(String uri, String localName, String qName, Attributes attributes) throws Exception {
      deferWrite = false;

      if (localName.equals("entry")) {
         if (targetBranchIdStr.equals(attributes.getValue("branch_id"))) {
            long gammaId = Long.parseLong(attributes.getValue("gamma_id"));
            if (netGammaIds.contains(gammaId)) {
               int modType = Integer.parseInt(attributes.getValue("mod_type"));
               ModificationType modificationType = ModificationType.getMod(modType);
               int transactionId = Integer.parseInt(attributes.getValue("transaction_id"));
               TxChange txCurrent = TxChange.getChangeType(Integer.parseInt(attributes.getValue("tx_current")));

               Address address =
                     new Address(false, targetBranchId, -1, transactionId, gammaId, modificationType, txCurrent);
               addressMap.put(gammaId, address);
            }
         } else if (isWriteAllowed) {
            if (branchIdsToTarget.contains(targetBranchId)) {
               deferWrite = true;
            } else {
               writeToFile(uri, localName, qName, attributes);
            }
         }
      }
   }

   private void writeToFile(String uri, String localName, String qName, Attributes attributes) throws Exception {
      writer.writeStartElement(localName);
      for (int i = 0; i < attributes.getLength(); i++) {
         writer.writeAttribute(attributes.getLocalName(i), attributes.getValue(i));
      }
   }

   @Override
   public void endElementFound(String uri, String localName, String qName) throws Exception {
      if (isWriteAllowed && !deferWrite) {
         try {
            writer.writeCharacters(getContents());
            writer.writeEndElement();
         } catch (XMLStreamException ex) {
            throw new SAXException(ex);
         }
      }
   }

}