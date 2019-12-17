/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
