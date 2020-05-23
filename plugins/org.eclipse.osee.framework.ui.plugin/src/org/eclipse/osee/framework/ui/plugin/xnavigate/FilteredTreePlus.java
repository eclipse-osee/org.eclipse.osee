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

package org.eclipse.osee.framework.ui.plugin.xnavigate;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

/**
 * @author Donald G. Dunne
 */
public class FilteredTreePlus extends FilteredTree {

   public FilteredTreePlus(Composite parent, int treeStyle, PatternFilter filter, boolean useNewLook) {
      super(parent, treeStyle, filter, useNewLook);
   }

   public void setFilterTextPlus(String filterText) {
      setFilterText(filterText);
   }
}
