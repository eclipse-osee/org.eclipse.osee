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
package org.eclipse.osee.framework.core.data;

import org.eclipse.osee.framework.jdk.core.type.Identifiable;
import org.eclipse.osee.framework.jdk.core.type.NamedIdentity;

/**
 * @author Andrew M. Finkbeiner
 */
public interface RelationSorter extends Identifiable<String> {

   public static RelationSorter create(String id, String name) {
      final class RelationSorterIdImpl extends NamedIdentity<String> implements RelationSorter {
         public RelationSorterIdImpl(String guid, String name) {
            super(guid, name);
         }
      }
      return new RelationSorterIdImpl(id, name);
   }
}