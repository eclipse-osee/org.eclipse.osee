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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * @author Stephen J. Molaro
 * @author Jaden W. Puckett
 */
public class EnumToken extends NamedIdBase {
   public static final EnumToken SENTINEL = new EnumToken(-1, Named.SENTINEL);
   private final List<NamespaceToken> namespaces = new ArrayList<>(); //supports multiple specified namespace(s)

   public EnumToken(int id, String name, NamespaceToken... namespaces) {
      super(id, name);
      if (namespaces.length > 0) {
         for (NamespaceToken namespace : namespaces) {
            this.namespaces.add(namespace);
         }
      } else {
         this.namespaces.add(NamespaceToken.SENTINEL);
      }
   }

   public List<NamespaceToken> getNamespaces() {
      return this.namespaces;
   }
}