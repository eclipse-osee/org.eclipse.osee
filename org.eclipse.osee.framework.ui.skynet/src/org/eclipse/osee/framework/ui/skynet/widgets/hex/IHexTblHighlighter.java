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
package org.eclipse.osee.framework.ui.skynet.widgets.hex;

import org.eclipse.swt.graphics.Color;

/**
 * @author Ken J. Aguilar
 */
public interface IHexTblHighlighter {
   void setRange(int start, int length);

   void setColor(Color color);

   void highlight();

   void setDisplayAsAscii(boolean displayAsAscii);
}
