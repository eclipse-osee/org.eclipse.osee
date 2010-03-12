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
package org.eclipse.osee.framework.ui.skynet.update;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.DecoratingStyledCellLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.ui.PlatformUI;

/**
 * @author Jeff C. Phillips
 */
public class ReflectDecoratingLabelProvider extends DecoratingStyledCellLabelProvider implements ILabelProvider {
   
   public ReflectDecoratingLabelProvider(RevertLabelProvider labelProvider) {
      super(labelProvider, PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator(), null);
   }

   public void initialize(ColumnViewer viewer, ViewerColumn column) {
      setOwnerDrawEnabled(true);

      super.initialize(viewer, column);
   }

   protected StyleRange prepareStyleRange(StyleRange styleRange, boolean applyColors) {
      if (!applyColors && styleRange.background != null) {
         styleRange = super.prepareStyleRange(styleRange, applyColors);
         styleRange.borderStyle = SWT.BORDER_DASH;
         return styleRange;
      }
      return super.prepareStyleRange(styleRange, applyColors);
   }

   public String getText(Object element) {
      return getStyledText(element).getString();
   }
}
