/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.framework.core.model.tabledataframework;

import java.util.Iterator;
import java.util.List;

/**
 * @author Shawn F. Cook
 */
public interface KeyColumn extends Iterator<Object> {
   public Object getCurrent();

   public List<Object> getAll();

   public void reset();
}
