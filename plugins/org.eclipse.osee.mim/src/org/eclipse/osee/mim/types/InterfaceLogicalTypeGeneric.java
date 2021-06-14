/*********************************************************************
 * Copyright (c) 2021 Boeing
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

package org.eclipse.osee.mim.types;

import com.fasterxml.jackson.annotation.JsonView;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;
import org.eclipse.osee.mim.InterfaceLogicalTypeView;

/**
 * @author Audrey E Denk
 */
public abstract class InterfaceLogicalTypeGeneric extends NamedIdBase {
   @JsonView(InterfaceLogicalTypeView.Detailed.class)
   private List<InterfaceLogicalTypeField> fields;

   public InterfaceLogicalTypeGeneric(Long id, String name) {
      super(id, name);
   }

   public List<InterfaceLogicalTypeField> getFields() {
      return fields;
   }

   public void setFields(List<InterfaceLogicalTypeField> fields) {
      this.fields = fields;
   }

}