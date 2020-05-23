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

package org.eclipse.osee.framework.ui.swt;

import org.eclipse.core.runtime.IAdaptable;

/**
 * @author Robert A. Fisher
 */
public interface ITreeNode extends IAdaptable {

   Object[] getChildren();

   /**
    * @param objChildren The children to set.
    */
   void setChildren(Object[] objChildren);

   /**
    * @return Returns the backingData.
    */
   Object getBackingData();

   /**
    * @return Returns the parent.
    */
   ITreeNode getParent();

}