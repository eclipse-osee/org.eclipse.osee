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

package org.eclipse.osee.orcs.script.dsl.typesystem;

import java.util.Map;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

/**
 * @author Roberto E. Escobar
 */
public class TemplateBinding extends MinimalEObjectImpl.Container {
   protected static final Map<String, Object> VALUE_EDEFAULT = null;
   private static final int OS_BINDING_ID = 95;
   private Map<String, Object> data;

   public Map<String, Object> map() {
      return data;
   }

   public void setValue(Map<String, Object> data) {
      this.data = data;
   }

   @Override
   public Object eGet(int featureID, boolean resolve, boolean coreType) {
      switch (featureID) {
         case OS_BINDING_ID:
            return map();
      }
      return super.eGet(featureID, resolve, coreType);
   }

   @SuppressWarnings("unchecked")
   @Override
   public void eSet(int featureID, Object newValue) {
      switch (featureID) {
         case OS_BINDING_ID:
            setValue((Map<String, Object>) newValue);
            return;
      }
      super.eSet(featureID, newValue);
   }

   @Override
   public void eUnset(int featureID) {
      switch (featureID) {
         case OS_BINDING_ID:
            setValue(VALUE_EDEFAULT);
            return;
      }
      super.eUnset(featureID);
   }

   @Override
   public boolean eIsSet(int featureID) {
      switch (featureID) {
         case OS_BINDING_ID:
            return VALUE_EDEFAULT == null ? data != null : !VALUE_EDEFAULT.equals(data);
      }
      return super.eIsSet(featureID);
   }

   @Override
   public String toString() {
      if (eIsProxy()) {
         return super.toString();
      }

      StringBuffer result = new StringBuffer(super.toString());
      result.append(" (value: ");
      result.append(data);
      result.append(')');
      return result.toString();
   }
}