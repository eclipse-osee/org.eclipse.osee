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
