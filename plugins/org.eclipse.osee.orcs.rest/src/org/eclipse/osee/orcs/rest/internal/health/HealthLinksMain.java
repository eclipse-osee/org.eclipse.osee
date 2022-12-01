/*********************************************************************
 * Copyright (c) 2022 Boeing
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
package org.eclipse.osee.orcs.rest.internal.health;

import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.orcs.health.HealthLinks;
import org.eclipse.osee.orcs.health.HealthLinksDefault;

/**
 * @author Donald G. Dunne
 */
public class HealthLinksMain {

   public static void main(String[] args) {
      HealthLinks links = HealthLinksDefault.get();
      String json = JsonUtil.toJson(links);
      System.err.println(json);
   }

}
