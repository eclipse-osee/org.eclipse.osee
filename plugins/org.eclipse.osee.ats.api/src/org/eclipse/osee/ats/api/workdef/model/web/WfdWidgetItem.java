/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.osee.ats.api.workdef.model.web;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.eclipse.osee.framework.jdk.core.type.NamedBase;

/**
 * @author Donald G. Dunne
 */
public class WfdWidgetItem extends NamedBase {

   public WfdWidgetItem() {
      // for jax-rs
   }

   public WfdWidgetItem(String name) {
      super(name);
   }

   @JsonIgnore
   public boolean isWidget() {
      return (this instanceof WfdWidgetDef);
   }

   @JsonIgnore
   public boolean isComposite() {
      return (this instanceof WfdWidgetComposite);
   }

}
