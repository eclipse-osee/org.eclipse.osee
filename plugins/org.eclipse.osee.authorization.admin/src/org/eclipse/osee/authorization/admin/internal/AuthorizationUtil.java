/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.authorization.admin.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public final class AuthorizationUtil {

   private AuthorizationUtil() {
      //
   }

   public static String normalize(String value) {
      return Strings.isValid(value) ? value.toLowerCase().trim() : value;
   }

   public static Iterable<String> unmodifiableSortedIterable(Collection<String> source) {
      List<String> list = new ArrayList<>();
      list.addAll(source);
      Collections.sort(list);
      return Collections.unmodifiableList(list);
   }

}
