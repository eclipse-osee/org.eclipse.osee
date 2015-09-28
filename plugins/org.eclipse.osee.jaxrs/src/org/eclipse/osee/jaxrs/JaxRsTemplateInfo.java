/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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