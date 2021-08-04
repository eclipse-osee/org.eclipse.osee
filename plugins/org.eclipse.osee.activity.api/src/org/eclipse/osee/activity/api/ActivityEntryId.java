/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.activity.api;

import org.eclipse.osee.framework.jdk.core.type.BaseId;
import org.eclipse.osee.framework.jdk.core.type.Id;

/**
 * @author Ryan D. Brooks
 */
public class ActivityEntryId extends BaseId {

   public ActivityEntryId() {
      // for jax-rs instantiation
      super(Id.SENTINEL);
   }

   public ActivityEntryId(Long id) {
      super(id);
   }

   public static ActivityEntryId valueOf(Long id) {
      return new ActivityEntryId(id);
   }
}