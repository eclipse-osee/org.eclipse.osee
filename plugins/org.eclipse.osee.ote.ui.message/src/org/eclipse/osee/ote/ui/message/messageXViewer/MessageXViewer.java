/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.ui.message.messageXViewer;

import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;

/**
 * @author Andrew M. Finkbeiner
 */
public class MessageXViewer extends XViewer {

   /**
    * @param parent
    */
   public MessageXViewer(Composite parent, int style) {
      super(parent, style, new MessageXViewerFactory());
   }

   public Menu getPopupMenu() {
      return this.getMenuManager().getMenu();
   }
}
