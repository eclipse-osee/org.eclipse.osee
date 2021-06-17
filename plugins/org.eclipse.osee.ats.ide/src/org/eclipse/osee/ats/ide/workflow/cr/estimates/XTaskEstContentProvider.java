/*******************************************************************************
 * Copyright (c) 2021 Boeing.
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
package org.eclipse.osee.ats.ide.workflow.cr.estimates;

import java.util.Collection;
import org.eclipse.osee.ats.ide.world.WorldContentProvider;
import org.eclipse.osee.ats.ide.world.WorldXViewer;

/**
 * @author Donald G. Dunne
 */
public class XTaskEstContentProvider extends WorldContentProvider {

   public XTaskEstContentProvider(WorldXViewer WorldXViewer) {
      super(WorldXViewer);
   }

   @Override
   public Object[] getChildren(Object parentElement) {
      if (parentElement instanceof Collection<?>) {
         return ((Collection<?>) parentElement).toArray();
      }
      return org.eclipse.osee.framework.jdk.core.util.Collections.EMPTY_ARRAY;
   }

   @Override
   public boolean hasChildren(Object element) {
      if (element instanceof Collection<?>) {
         return true;
      }
      return false;
   }

   @Override
   public Object getParent(Object element) {
      return null;
   }

   @Override
   public Object[] getElements(Object inputElement) {
      if (inputElement instanceof String) {
         return new Object[] {inputElement};
      }
      return getChildren(inputElement);
   }

}
