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

/**
 * @author Andrew M. Finkbeiner
 */
public interface IXViewerFactory {
   XViewerSorter createNewXSorter(XViewer viewer);

   CustomizeData getDefaultTableCustomizeData(XViewer xViewer);

   XViewerColumn getDefaultXViewerColumn(String name);

   IXViewerCustomizeDefaults getXViewerCustomizeDefaults();

   IXViewerCustomizations getXViewerCustomizations(XViewer xViewer);
}
