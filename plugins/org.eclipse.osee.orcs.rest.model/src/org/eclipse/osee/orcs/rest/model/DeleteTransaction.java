/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.model;

import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Roberto E. Escobar
 */
@XmlRootElement
public class DeleteTransaction {

   private List<Integer> transactions;

   public List<Integer> getTransactions() {
      return transactions != null ? transactions : Collections.<Integer> emptyList();
   }

   public void setTransactions(List<Integer> transactions) {
      this.transactions = transactions;
   }

   public boolean isEmpty() {
      return getTransactions().isEmpty();
   }

   @Override
   public String toString() {
      return "DeleteTransaction [transactions=" + getTransactions() + "]";
   }

}
