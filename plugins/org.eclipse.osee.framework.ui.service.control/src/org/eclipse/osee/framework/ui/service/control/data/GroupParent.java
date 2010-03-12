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
package org.eclipse.osee.framework.ui.service.control.data;

import org.eclipse.osee.framework.jdk.core.type.TreeParent;
import org.eclipse.osee.framework.ui.service.control.renderer.IRenderer;
import org.eclipse.osee.framework.ui.swt.FormattedText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Roberto E. Escobar
 */
public class GroupParent extends TreeParent implements IRenderer {

   public GroupParent() {
      super();
   }

   public GroupParent(String name) {
      super(name);
   }

   public Control renderInComposite(Composite parent) {
      if (parent instanceof FormattedText) {
         ((FormattedText) parent).clearTextArea();
      }
      return parent;
   }
}
