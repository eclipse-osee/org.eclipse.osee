/*********************************************************************
 * Copyright (c) 2017 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

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
