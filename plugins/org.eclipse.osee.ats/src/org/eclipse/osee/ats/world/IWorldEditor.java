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
package org.eclipse.osee.ats.world;

import org.eclipse.swt.widgets.Menu;

/**
 * @author Donald G. Dunne
 */
public interface IWorldEditor {

   public abstract void reflow();

   public void setTableTitle(final String title, final boolean warning);

   public void reSearch();

   public IWorldEditorProvider getWorldEditorProvider();

   public void createToolBarPulldown(Menu menu);

   public String getCurrentTitleLabel();

}
