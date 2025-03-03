/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.skynet.util;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;

/**
 * @author Donald G. Dunne
 */
public class TransactionIdLabelProvider extends LabelProvider {

   @Override
   public String getText(Object element) {
      TransactionRecord tx = (TransactionRecord) element;
      return String.format("%s - %s (%s) - %s", element.toString(), tx.getComment(), tx.getIdString(),
         DateUtil.getMMDDYYHHMMSS(tx.getTimeStamp()));
   }

}
