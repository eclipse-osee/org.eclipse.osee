/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.core.environment.config;

import java.io.Serializable;
import org.eclipse.osee.framework.jdk.core.persistence.Xmlizable;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.ote.core.test.tags.BaseTestTags;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TesterConfig implements Xmlizable, Serializable {

   private static final long serialVersionUID = -6513354260245950581L;
   private String name;
   private String id;
   private String email;

   public TesterConfig(){
      name = "";
      id = "";
      email = "";
   }
   
   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public Element toXml(Document doc) {
      Element testerElement = doc.createElement(BaseTestTags.EXECUTED_BY);
      testerElement.appendChild(Jaxp.createElement(doc, BaseTestTags.NAME_FIELD, name));
      testerElement.appendChild(Jaxp.createElement(doc, BaseTestTags.BEMS_FIELD, id));
      testerElement.appendChild(Jaxp.createElement(doc, BaseTestTags.EMAIL_FIELD, email));
      return testerElement;
   }
}
