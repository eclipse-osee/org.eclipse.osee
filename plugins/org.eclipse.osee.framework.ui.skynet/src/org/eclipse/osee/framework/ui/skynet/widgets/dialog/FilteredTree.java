/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets.dialog;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * @author Donald G. Dunne
 */
public class FilteredTree extends org.eclipse.ui.dialogs.FilteredTree {

   private static final String CLEAR_ICON = "org.eclipse.ui.internal.dialogs.CLEAR_ICON"; //$NON-NLS-1$
   private static final String DISABLED_CLEAR_ICON = "org.eclipse.ui.internal.dialogs.DCLEAR_ICON"; //$NON-NLS-1$

   public FilteredTree(Composite parent, int treeStyle, PatternFilter filter, boolean useNewLook) {
      super(parent, treeStyle, filter, useNewLook);
   }

   @Override
   protected Composite createFilterControls(Composite parent) {
      ImageDescriptor descriptor =
         AbstractUIPlugin.imageDescriptorFromPlugin(PlatformUI.PLUGIN_ID, "$nl$/icons/full/etool16/clear_co.gif"); //$NON-NLS-1$
      if (descriptor != null) {
         JFaceResources.getImageRegistry().put(CLEAR_ICON, descriptor);
      }
      descriptor =
         AbstractUIPlugin.imageDescriptorFromPlugin(PlatformUI.PLUGIN_ID, "$nl$/icons/full/dtool16/clear_co.gif"); //$NON-NLS-1$
      if (descriptor != null) {
         JFaceResources.getImageRegistry().put(DISABLED_CLEAR_ICON, descriptor);
      }
      return super.createFilterControls(parent);
   }

}
