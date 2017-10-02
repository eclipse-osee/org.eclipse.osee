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

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.osee.framework.ui.skynet.widgets.util.IDynamicWidgetLayoutListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;

/**
 * @author Donald G. Dunne
 */
public interface IWorldEditorParameterProvider extends IWorldEditorProvider {

   public String getParameterXWidgetXml();

   public IDynamicWidgetLayoutListener getDynamicWidgetLayoutListener();

   /**
    * Create extra controls and return title if it changed
    */
   void createParametersSectionCompleted(IManagedForm managedForm, Composite mainComp);

   public void createToolbar(IToolBarManager toolBarManager);

}
