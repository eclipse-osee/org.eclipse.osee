/*********************************************************************
 * Copyright (c) 2024 Boeing
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
package org.eclipse.osee.framework.ui.skynet.widgets;

import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

public class WidgetHint extends NamedIdBase {

   public static WidgetHint EnableAll = new WidgetHint(1L, "Enable All");

   private WidgetHint(Long id, String name) {
      super(id, name);
   }
}