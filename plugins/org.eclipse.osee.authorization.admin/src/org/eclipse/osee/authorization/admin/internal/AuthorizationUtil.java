/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
