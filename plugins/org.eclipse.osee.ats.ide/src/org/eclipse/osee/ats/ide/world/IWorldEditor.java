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
package org.eclipse.osee.ats.ide.world;

import org.eclipse.swt.widgets.Menu;

/**
 * @author Donald G. Dunne
 */
public interface IWorldEditor {

   void reflow();

   void setTableTitle(final String title, final boolean warning);

   void reSearch();

   IWorldEditorProvider getWorldEditorProvider();

   void createToolBarPulldown(Menu menu);

   String getCurrentTitleLabel();

}
