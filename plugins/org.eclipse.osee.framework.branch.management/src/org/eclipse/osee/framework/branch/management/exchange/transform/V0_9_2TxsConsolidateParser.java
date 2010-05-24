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

import java.util.Set;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.jdk.core.util.io.xml.AbstractSaxHandler;
import org.xml.sax.Attributes;

/**
 * @author Ryan D. Brooks
 */
public class V0_9_2TxsConsolidateParser extends AbstractSaxHandler {
   private final Integer targetBranchId;
   private final Set<Long> netGammaIds;

   public V0_9_2TxsConsolidateParser(Integer targetBranchId, Set<Long> netGammaIds) {
      this.targetBranchId = targetBranchId;
      this.netGammaIds = netGammaIds;
   }

   @Override
   public void startElementFound(String uri, String localName, String qName, Attributes attributes) throws Exception {
      if (localName.equals("entry")) {
         Integer currentBranchId = Integer.parseInt(attributes.getValue("branch_id"));
         if (targetBranchId.equals(currentBranchId)) {
            Long gammaId = Long.parseLong(attributes.getValue("gamma_id"));
            if (netGammaIds.contains(gammaId)) {

               int modType = Integer.parseInt(attributes.getValue("mod_type"));
               ModificationType modificationType = ModificationType.getMod(modType);
               Integer transaction = Integer.parseInt(attributes.getValue("transaction_id"));

               // Do Something Here;
            }
         }
      }
   }

   @Override
   public void endElementFound(String uri, String localName, String qName) throws Exception {
   }

}