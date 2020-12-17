/*******************************************************************************
 * Copyright (c) 2020 Boeing.
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
package org.eclipse.osee.framework.core.data;

import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * @author Donald G. Dunne
 */
public class DisplayHint extends NamedIdBase {

   public static DisplayHint SingleLine = new DisplayHint(1L, "Single Line");
   public static DisplayHint MultiLine = new DisplayHint(2L, "Multiline");
   public static DisplayHint NoGeneralEdit = new DisplayHint(3L, "No General Edit");

   private DisplayHint(Long id, String name) {
      super(id, name);
   }
}