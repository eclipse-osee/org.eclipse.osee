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
package org.eclipse.osee.ote.ui.test.manager.batches;

import java.util.logging.Level;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.ote.ui.test.manager.OteTestManagerImage;
import org.eclipse.osee.ote.ui.test.manager.TestManagerPlugin;
import org.eclipse.osee.ote.ui.test.manager.batches.util.SelectionUtil;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IDecoratorManager;

/**
 * @author Roberto E. Escobar
 */
public class TestBatchDecorator extends LabelProvider implements ILightweightLabelDecorator {
   private static final String DECORATOR_ID = "org.eclipse.osee.ote.ui.test.manager.decorator";
   private static final ImageDescriptor IMAGE = ImageManager.getImageDescriptor(OteTestManagerImage.TEST);

   public void decorate(Object element, IDecoration decoration) {
      try {
         IProject project = null;
         if (element instanceof IJavaProject) {
            project = ((IJavaProject) element).getProject();
         } else {
            project = (IProject) element;
         }
         if (project.isOpen() && project.hasNature(TestBatchProjectNature.NATURE_ID)) {
            decoration.addOverlay(IMAGE);
         }
      } catch (Exception ex) {
         OseeLog.log(TestManagerPlugin.class, Level.SEVERE, SelectionUtil.getStatusMessages(ex));
      }
   }

   private void startDecoratorUpdate(IProject project) {
      final LabelProviderChangedEvent evnt = new LabelProviderChangedEvent(this, project);
      Display.getDefault().asyncExec(new Runnable() {
         public void run() {
            fireLabelProviderChanged(evnt);
         }
      });
   }

   public static void performLabelDecoratorUpdate(IProject project) {
      IDecoratorManager decoratorManager = TestManagerPlugin.getInstance().getWorkbench().getDecoratorManager();
      IBaseLabelProvider provider = decoratorManager.getBaseLabelProvider(DECORATOR_ID);
      if (provider != null) {
         TestBatchDecorator decorator = (TestBatchDecorator) provider;
         decorator.startDecoratorUpdate(project);
      }
   }
}
