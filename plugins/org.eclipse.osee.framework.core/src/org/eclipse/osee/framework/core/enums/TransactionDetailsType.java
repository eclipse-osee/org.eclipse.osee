/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.framework.core.enums;

import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * @author Jeff C. Phillips
 */
public class TransactionDetailsType extends NamedIdBase {
   public static final TransactionDetailsType INVALID = new TransactionDetailsType(-1, "INVALID");
   public static final TransactionDetailsType NonBaselined = new TransactionDetailsType(0, "NonBaselined");
   public static final TransactionDetailsType Baselined = new TransactionDetailsType(1, "Baselined");
   public static final TransactionDetailsType reverted = new TransactionDetailsType(2, "reverted");
   public static final TransactionDetailsType[] values =
      new TransactionDetailsType[] {INVALID, NonBaselined, Baselined, reverted};

   private TransactionDetailsType(int id, String name) {
      super(Long.valueOf(id), name);
   }

   public boolean isBaseline() {
      return this == TransactionDetailsType.Baselined;
   }

   public static TransactionDetailsType valueOf(int id) {
      return NamedIdBase.valueOf(Long.valueOf(id), values);
   }
}