/*********************************************************************
 * Copyright (c) 2024 Boeing
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
package org.eclipse.osee.ats.api.action;

import java.util.Collection;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.workflow.CreateNewActionField;

/**
 * @author Ryan T. Baldwin
 */
public interface ICreateNewActionFieldsProvider {

   Collection<CreateNewActionField> getCreateNewActionFields(AtsApi atsApi);

   boolean actionableItemHasFields(AtsApi atsApi, Collection<IAtsActionableItem> ais);

}
