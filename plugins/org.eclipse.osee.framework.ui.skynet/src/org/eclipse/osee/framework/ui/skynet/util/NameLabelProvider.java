/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.util;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osee.framework.jdk.core.type.Named;

/**
 * @author David W. Miller
 */
public class NameLabelProvider extends LabelProvider {

   @Override
   public String getText(Object element) {
      if (element instanceof Named) {
         return ((Named) element).getName();
      }
      return element.toString();
   }
}
