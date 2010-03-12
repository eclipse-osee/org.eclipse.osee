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
import org.eclipse.osee.framework.jdk.core.util.io.xml.AbstractSaxHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Ryan D. Brooks
 */
public class V0_9_0TxDetailsHandler extends AbstractSaxHandler {
   private final HashMap<Integer, Integer> branchIdMap = new HashMap<Integer, Integer>(10000);

   @Override
   public void endElementFound(String uri, String localName, String qName) throws SAXException {
   }

   @Override
   public void startElementFound(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      if (localName.equals("entry")) {
         branchIdMap.put(Integer.parseInt(attributes.getValue("transaction_id")),
               Integer.parseInt(attributes.getValue("branch_id")));
      }
   }

   public HashMap<Integer, Integer> getBranchIdMap() {
      return branchIdMap;
   }
}