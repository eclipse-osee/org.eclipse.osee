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
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.PatternFilter;

/**
 * @author Donald G. Dunne
 */
public class FilteredTree extends org.eclipse.ui.dialogs.FilteredTree {

   private static final String CLEAR_ICON = "org.eclipse.ui.internal.dialogs.CLEAR_ICON"; //$NON-NLS-1$
   private static final String DISABLED_CLEAR_ICON = "org.eclipse.ui.internal.dialogs.DCLEAR_ICON"; //$NON-NLS-1$

   public FilteredTree(Composite parent, int treeStyle, PatternFilter filter, boolean useNewLook) {
      super(parent, treeStyle, filter, useNewLook);
   }

   static {
      ImageDescriptor imageDescriptor = ImageManager.getImageDescriptor(FrameworkImage.CLEAR_CO);
      JFaceResources.getImageRegistry().put(CLEAR_ICON, imageDescriptor);
      JFaceResources.getImageRegistry().put(DISABLED_CLEAR_ICON, imageDescriptor);
   }
}
