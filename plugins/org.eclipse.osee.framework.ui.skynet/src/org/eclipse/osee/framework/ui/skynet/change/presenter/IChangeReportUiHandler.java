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

package org.eclipse.osee.framework.ui.skynet.change.presenter;

import org.eclipse.osee.framework.core.model.TransactionDelta;
import org.eclipse.osee.framework.ui.skynet.change.ChangeUiData;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

public interface IChangeReportUiHandler {

   public KeyedImage getActionImage();

   public String getActionName();

   public KeyedImage getScenarioImage(ChangeUiData changeUiData);

   public String getScenarioDescriptionHtml(ChangeUiData changeUiData);

   public String getActionDescription();

   public String getName(TransactionDelta txDelta);

   public void appendTransactionInfoHtml(StringBuilder sb, ChangeUiData changeUiData);

}
