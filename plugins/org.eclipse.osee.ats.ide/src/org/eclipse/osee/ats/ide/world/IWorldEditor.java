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
