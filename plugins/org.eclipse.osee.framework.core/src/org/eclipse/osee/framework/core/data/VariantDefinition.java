/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * @author Donald G. Dunne
 */
public class VariantDefinition extends NamedIdBase {

   public Object data;
   public ArtifactId copyFrom;

   public VariantDefinition() {
      super(ArtifactId.SENTINEL.getId(), "");
      // Not doing anything
   }

   public VariantDefinition(Long id, String name) {
      super(id, name);
   }

   public Object getData() {
      return data;
   }

   public void setData(Object data) {
      this.data = data;
   }

   public ArtifactId getCopyFrom() {
      return copyFrom;
   }

   public void setCopyFrom(ArtifactId copyFrom) {
      this.copyFrom = copyFrom;
   }

}
