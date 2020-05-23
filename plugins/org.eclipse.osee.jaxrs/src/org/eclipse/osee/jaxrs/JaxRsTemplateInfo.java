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

package org.eclipse.osee.jaxrs;

import java.util.Set;
import java.util.TreeSet;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Roberto E. Escobar
 */
@XmlRootElement
public class JaxRsTemplateInfo extends JaxRsTemplateId {

   private Set<String> attributes;

   public Set<String> getAttributes() {
      if (attributes == null) {
         attributes = new TreeSet<>();
      }
      return attributes;
   }

   public void setAttributes(Set<String> attributes) {
      this.attributes = attributes;
   }

}