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

import org.eclipse.jface.viewers.StyledString;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.util.XViewerException;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

public interface IXViewerValueColumn {

   Image getColumnImage(Object element, XViewerColumn column, int columnIndex) throws XViewerException;

   String getColumnText(Object element, XViewerColumn column, int columnIndex) throws XViewerException;

   /**
    * Returns the backing data object for operations like sorting
    */
   Object getBackingData(Object element, XViewerColumn xCol, int columnIndex) throws Exception;

   Color getBackground(Object element, XViewerColumn xCol, int columnIndex) throws XViewerException;

   Color getForeground(Object element, XViewerColumn xCol, int columnIndex) throws XViewerException;

   //This method will only be called be the XViewerStyledTextLabelProvider
   StyledString getStyledText(Object element, XViewerColumn viewerColumn, int columnIndex) throws XViewerException;

   //This method will only be called be the XViewerStyledTextLabelProvider
   Font getFont(Object element, XViewerColumn viewerColumn, int columnIndex) throws XViewerException;

}
