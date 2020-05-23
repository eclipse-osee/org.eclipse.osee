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

package org.eclipse.osee.framework.ui.skynet.results.table;

import java.util.Collection;
import java.util.Collections;

/**
 * @author Donald G. Dunne
 */
public interface IResultsXViewerRow {

   public String getValue(int col);

   public String[] values();

   public Object getData();

   default public boolean hasChildren() {
      return false;
   }

   default public Collection<IResultsXViewerRow> getChildren() {
      return Collections.emptyList();
   }

   default IResultsXViewerRow getParent() {
      return null;
   }

}
