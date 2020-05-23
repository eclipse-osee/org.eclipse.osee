/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.ats.api.task.create;

import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * @author Donald G. Dunne
 */
public class ChangeReportTaskNameProviderToken extends NamedIdBase {

   public static ChangeReportTaskNameProviderToken DefaultChangeReportOptionsNameProvider =
      valueOf(989898L, "DefaultChangeReportOptionsNameProvider");

   public ChangeReportTaskNameProviderToken() {
      // for jax-rs
   }

   public ChangeReportTaskNameProviderToken(Long id, String name) {
      super(id, name);
   }

   public static ChangeReportTaskNameProviderToken valueOf(Long id, String name) {
      return new ChangeReportTaskNameProviderToken(id, name);
   }
}
