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

package org.eclipse.osee.framework.skynet.core.attribute.providers;

import org.eclipse.osee.framework.core.data.GammaId;

/**
 * @author Roberto E. Escobar
 */
public interface IAttributeDataProvider {

   public String getDisplayableString();

   public void setDisplayableString(String toDisplay);

   public void loadData(Object... objects);

   public Object getValue();

   public Object[] getData();

   public void persist(GammaId storageId);

   public void purge();
}
