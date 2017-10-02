/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.change.presenter;

import org.eclipse.osee.framework.core.model.TransactionDelta;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.change.ChangeUiData;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

public interface IChangeReportUiHandler {

   public KeyedImage getActionImage();

   public String getActionName();

   public KeyedImage getScenarioImage(ChangeUiData changeUiData);

   public String getScenarioDescriptionHtml(ChangeUiData changeUiData) ;

   public String getActionDescription();

   public String getName(TransactionDelta txDelta);

   public void appendTransactionInfoHtml(StringBuilder sb, ChangeUiData changeUiData) ;

}
