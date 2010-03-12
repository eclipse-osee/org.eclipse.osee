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
package org.eclipse.osee.ote.core.log.record;

import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.eclipse.osee.ote.core.log.TestLevel;
import org.eclipse.osee.ote.core.test.tags.BaseTestTags;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TestDescriptionRecord extends BaseTestRecord{
   
   private static final long serialVersionUID = -2188431468814850228L;
   private String purpose;
   private String preCondition;
   private String postCondition;
   
   public TestDescriptionRecord(ITestEnvironmentAccessor testEnvironment) {
      super(testEnvironment, TestLevel.TEST_POINT, "Description Record", false);
      this.purpose = " ";
      this.preCondition = " ";
      this.postCondition = " ";
   }
     
   public void setPurpose(String purpose){
      this.purpose = purpose;
   }
   
   public void setPreCondition(String preCondition){
      this.preCondition = preCondition;
   }
   
   public void setPostCondition(String postCondition){
      this.postCondition = postCondition;
   }
   
   public Element toXml(Document doc){
      Element root = doc.createElement(BaseTestTags.DESCRIPTION_FIELD);
      root.appendChild(Jaxp.createElement(doc,BaseTestTags.PURPOSE_FIELD,purpose));
      root.appendChild(Jaxp.createElement(doc,BaseTestTags.PRECONDITION_FIELD,preCondition));
      root.appendChild(Jaxp.createElement(doc,BaseTestTags.POSTCONDITION_FIELD,postCondition));
      return root;
   }
}
