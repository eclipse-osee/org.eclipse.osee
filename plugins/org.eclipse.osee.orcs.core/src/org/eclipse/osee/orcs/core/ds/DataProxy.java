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

package org.eclipse.osee.orcs.core.ds;

import org.eclipse.osee.framework.core.data.GammaId;

/**
 * @author Roberto E. Escobar
 */
public interface DataProxy<T> {

   public void setResolver(ResourceNameResolver resolver);

   public String getDisplayableString();

   public void setDisplayableString(String toDisplay);

   public void setData(T value, String uri);

   public T getRawValue();

   public String getUri();

   public void persist();

   public void purge();

   boolean isInMemory();

   void setGamma(GammaId gammaId, boolean isNewGammaId);

   GammaId getGammaId();

   void rollBack();

   public void setAttribute(Attribute<T> attribute);

   public String getStorageString();
}