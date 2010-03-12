/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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