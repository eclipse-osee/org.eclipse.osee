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
package org.eclipse.osee.ats.api.workdef;

import java.util.List;

/**
 * @author Donald G. Dunne
 */
public interface IAtsCompositeLayoutItem extends IAtsLayoutItem {

   public abstract void setName(String string);

   public abstract int getNumColumns();

   public abstract void setNumColumns(int numColumns);

   public abstract List<IAtsLayoutItem> getaLayoutItems();

   @Override
   public abstract String toString();

}