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
package org.eclipse.osee.ats.core.task.demo;

import org.eclipse.osee.ats.api.task.related.AutoGenVersion;

/**
 * @author Donald G. Dunne
 */
public class AutoGenVersionDemo extends AutoGenVersion {

   public static AutoGenVersion Demo = new AutoGenVersion(598045701, "Demo");

   public AutoGenVersionDemo() {
   }

   public AutoGenVersionDemo(long id, String name) {
      super(id, name);
   }

}
