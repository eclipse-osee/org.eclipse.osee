/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.workflow.cr.estimates.demo;

import org.eclipse.nebula.widgets.xviewer.IXViewerFactory;
import org.eclipse.osee.ats.ide.workflow.cr.estimates.sibling.XTaskEstSiblingWorldWidget;
import org.eclipse.osee.ats.ide.world.WorldXViewer;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;

/**
 * @author Donald G. Dunne
 */
public class XTaskEstSiblingWorldDemoWidget extends XTaskEstSiblingWorldWidget {

   public static final String WIDGET_ID = XTaskEstSiblingWorldDemoWidget.class.getSimpleName();

   @Override
   public WorldXViewer createWorldXViewer(Composite tableComp, IXViewerFactory xViewerFactory) {
      return new XTaskEstSiblingWorldXViewer(tableComp, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION, xViewerFactory,
         null);
   }

   @Override
   public ToolBar createActionBar(Composite tableComp) {
      final XTaskEstSiblingWorldWidget fWidget = this;
      tableComp.addDisposeListener(new DisposeListener() {

         @Override
         public void widgetDisposed(DisposeEvent e) {
            OseeEventManager.removeListener(fWidget);
         }
      });
      XTaskEstSiblingDemoActionBar actionBar = new XTaskEstSiblingDemoActionBar(this);
      ToolBar toolBar = actionBar.createTaskActionBar(tableComp);
      return toolBar;
   }

}
