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
package org.eclipse.osee.framework.ui.skynet.widgets.xviewer;

import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.CustomizeData;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.IXViewerCustomizations;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.IXViewerCustomizeDefaults;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.XViewerCustomize;

/**
 * @author Andrew M. Finkbeiner
 */
public interface IXViewerFactory {
   /**
    * Returns the default sorter to use for this xviewer
    * 
    * @param viewer
    * @return
    */
   XViewerSorter createNewXSorter(XViewer viewer);

   /**
    * Returns the default table customization for this viewer including column definitions, sorting and filtering (if
    * any)
    * 
    * @param xViewer
    * @return
    */
   CustomizeData getDefaultTableCustomizeData(XViewer xViewer);

   /**
    * Returns the default column definition for the given id
    * 
    * @param id
    * @return
    */
   XViewerColumn getDefaultXViewerColumn(String id);

   /**
    * Provides the storage mechanism for save/load of users default customization
    * 
    * @return
    */
   IXViewerCustomizeDefaults getXViewerCustomizeDefaults();

   /**
    * Provides the storage mechanism for save/load of personal/global customizations
    * 
    * @param xViewer
    * @return
    */
   IXViewerCustomizations getXViewerCustomizations(XViewer xViewer);

   /**
    * Provides custom menu for XViewer
    * 
    * @return
    */
   XViewerCustomize getXViewerCustomMenu();
}
