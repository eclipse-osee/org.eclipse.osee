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

import java.util.Map;
import org.eclipse.osee.framework.jdk.core.util.io.xml.SaxTransformer;
import org.xml.sax.Attributes;

/**
 * @author Roberto E. Escobar
 */
public class V0_9_2BranchTransformer extends SaxTransformer {
   private final Map<Integer, Integer> branchToBaseTx;

   public V0_9_2BranchTransformer(Map<Integer, Integer> branchToBaseTx) {
      this.branchToBaseTx = branchToBaseTx;
   }

   @Override
   public void startElementFound(String uri, String localName, String qName, Attributes attributes) throws Exception {
      super.startElementFound(uri, localName, qName, attributes);
      if (localName.equals("entry")) {
         Integer branchId = Integer.parseInt(attributes.getValue("branch_id"));
         Integer baselineTransactionId = branchToBaseTx.get(branchId);
         writer.writeAttribute("baseline_transaction_id", String.valueOf(baselineTransactionId));
      }
   }
}