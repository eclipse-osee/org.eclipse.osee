/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.config;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;

/**
 * @author Donald G. Dunne
 */
public interface IActionableItemFactory {

   IAtsActionableItem createActionableItem(String name, long uuid, IAtsChangeSet changes, AtsApi atsApi);

   IAtsActionableItem createActionableItem(String name, IAtsChangeSet changes, AtsApi atsApi);

}
