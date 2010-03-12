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

import java.util.HashMap;
import org.eclipse.osee.framework.jdk.core.util.io.xml.SaxTransformer;
import org.xml.sax.Attributes;

/**
 * @author Ryan D. Brooks
 */
public class V0_9_0TxsTransformer extends SaxTransformer {
   private final HashMap<Integer, Integer> branchIdMap;

   public V0_9_0TxsTransformer(HashMap<Integer, Integer> branchIdMap) {
      this.branchIdMap = branchIdMap;
   }

   @Override
   public void startElementFound(String uri, String localName, String qName, Attributes attributes) throws Exception {
      super.startElementFound(uri, localName, qName, attributes);
      if (localName.equals("entry")) {
         writer.writeAttribute("branch_id",
               String.valueOf(branchIdMap.get(Integer.parseInt(attributes.getValue("transaction_id")))));
      }
   }
}