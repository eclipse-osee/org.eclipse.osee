/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.framework.core.data;

/**
 * @author Donald G Dunne
 */
public interface IAttribute<T> extends AttributeId {

   T getValue();

   GammaId getGammaId();

   AttributeTypeToken getAttributeType();

   String getDisplayableString();

   TransactionDetails getLatestTxDetails();

   default String getError() {
      return "";
   }

   default void setError(String error) {
      throw new UnsupportedOperationException("Invalid Set");
   }

}