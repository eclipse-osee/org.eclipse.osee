/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.orcs.db.internal.exchange.transform;

import java.util.Map;
import javax.xml.stream.XMLStreamException;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TxCurrent;
import org.eclipse.osee.framework.jdk.core.type.HashCollectionSet;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.io.xml.SaxTransformer;
import org.eclipse.osee.orcs.db.internal.util.Address;
import org.xml.sax.Attributes;

/**
 * @author Ryan D. Brooks
 */
public class V0_9_2TxsConsolidateParser extends SaxTransformer {
   private long targetBranchId;
   private String targetBranchIdStr;
   private final Map<Long, Long> artifactGammaToNetGammaId;
   private final HashCollectionSet<Long, Address> addressMap;
   private boolean isWriteAllowed = true;
   private boolean skipWrite;

   public V0_9_2TxsConsolidateParser(Map<Long, Long> artifactGammaToNetGammaId, HashCollectionSet<Long, Address> addressMap) {
      this.artifactGammaToNetGammaId = artifactGammaToNetGammaId;
      this.addressMap = addressMap;
   }

   public void setBranchId(long targetBranchId) {
      this.isWriteAllowed = false;
      this.targetBranchId = targetBranchId;
      this.targetBranchIdStr = String.valueOf(targetBranchId);
   }

   @Override
   public void startElementFound(String uri, String localName, String qName, Attributes attributes) throws XMLStreamException {
      skipWrite = false;

      GammaId gammaId = null;
      if (isWriteAllowed) {
         if (localName.equals("entry")) {
            gammaId = GammaId.valueOf(artifactGammaToNetGammaId.get(Long.valueOf(attributes.getValue("gamma_id"))));
         }
         if (gammaId == null) {
            super.startElementFound(uri, localName, qName, attributes);
         } else {
            skipWrite = true;
         }
      } else if (localName.equals("entry")) {
         if (targetBranchIdStr.equals(attributes.getValue("branch_id"))) {
            gammaId = GammaId.valueOf(artifactGammaToNetGammaId.get(Long.valueOf(attributes.getValue("gamma_id"))));
            if (gammaId != null) {
               addressMap.put(gammaId.getId(), createAddress(attributes, gammaId));
            }
         }
      }
   }

   @Override
   public void endElementFound(String uri, String localName, String qName) throws XMLStreamException {
      if (isWriteAllowed && !skipWrite && !localName.equals("data")) {
         super.endElementFound(uri, localName, qName);
      }
   }

   private Address createAddress(Attributes attributes, GammaId gammaId) throws XMLStreamException {
      try {
         int modType = Integer.parseInt(attributes.getValue("mod_type"));
         ModificationType modificationType = ModificationType.valueOf(modType);
         int transactionId = Integer.parseInt(attributes.getValue("transaction_id"));
         TxCurrent txCurrent = TxCurrent.valueOf(Integer.parseInt(attributes.getValue("tx_current")));

         return new Address(false, targetBranchId, Id.valueOf(Id.SENTINEL), transactionId, gammaId, modificationType,
            ApplicabilityId.valueOf(1L), txCurrent);
      } catch (OseeCoreException ex) {
         throw new XMLStreamException(ex);
      }
   }
}