/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.nebula.widgets.xviewer;

/**
 * @author Donald G. Dunne
 */
public abstract class XViewerPreComputedColumnAdapter implements IXViewerPreComputedColumn {

   @Override
   public String getText(Object obj, Long key, String cachedValue) {
      return null;
   }

}
