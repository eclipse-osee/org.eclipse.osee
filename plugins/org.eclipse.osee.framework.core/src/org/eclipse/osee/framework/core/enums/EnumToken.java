/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.framework.core.enums;

import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * @author Stephen J. Molaro
 */
public class EnumToken extends NamedIdBase {
   public static final EnumToken SENTINEL = new EnumToken(-1, Named.SENTINEL);

   public EnumToken(int id, String name) {
      super(id, name);
   }
}