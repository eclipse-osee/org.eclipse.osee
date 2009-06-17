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
package org.eclipse.osee.ote.core.testPoint;

import java.util.ArrayList;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.ote.core.environment.interfaces.ITestGroup;
import org.eclipse.osee.ote.core.environment.interfaces.ITestPoint;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Robert A. Fisher
 * @author Charles Shaw
 */
public class CheckGroup implements ITestGroup {
   private String groupName;
   private ArrayList<ITestPoint> testPoints;
   private Operation operation;

   /**
    * CheckGroup objects are used to setup complex TestPoint structures where the pass/fail behavior
    * can be an <b>And </b>'ing or an <b>Or </b>'ing of the <b>getPass() </b> values of all the
    * immediate children.
    * <p>
    * More complex TestPoint syntax can be obtained using the CheckGroup as a parent of other
    * CheckGroup objects, of which the <b>And </b> or <b>Or </b> setting can be set differently.
    * 
    * @param operation The logical operation used for combining items within this CheckGroup.
    */
   public CheckGroup(Operation operation, String groupName) {
      super();
      testPoints = new ArrayList<ITestPoint>();
      // this.allTrue = allTrue;
      this.operation = operation;
      this.groupName = groupName;
   }

   public CheckGroup add(ITestPoint testPoint) {
      this.testPoints.add(testPoint);

      return this;
   }

   public ArrayList<ITestPoint> getTestPoints() {
      return testPoints;
   }

   /**
    * @return The number of test points added to this check group so far
    */
   public int size() {
      return this.testPoints.size();
   }

   public boolean isPass() {
      boolean passFail;

      // Ensure that some points have been added
      if (testPoints.size() > 0) {
         // If this group is using AND logic then assume pass until find a
         // fail
         // if (allTrue) {
         if (operation == Operation.AND) {
            passFail = true;
            // Else the group is using OR logic, so assume fail until find a
            // pass
         }
         else {
            passFail = false;
         }
         
         for (ITestPoint testPoint : testPoints) {
            if (operation == Operation.AND) {
               passFail &= testPoint.isPass();
            }
            else {
               passFail |= testPoint.isPass();
            }
         }
      }
      else {
         passFail = false;
      }
      return passFail;
   }

   public Element toXml(Document doc) {
      return buildXml(doc, "CheckGroup");
   }

   protected Element buildXml(Document doc, String name) {
      Element checkGroupElement = doc.createElement(name);

      checkGroupElement.setAttribute("Mode", operation.toString());
      checkGroupElement.appendChild(Jaxp.createElement(doc, "GroupName", groupName));

      if (this.isPass()) {
         checkGroupElement.appendChild(Jaxp.createElement(doc, "Result", "PASSED"));
      }
      else {
         checkGroupElement.appendChild(Jaxp.createElement(doc, "Result", "FAILED"));
      }

      for (ITestPoint testPoint : testPoints) {
         checkGroupElement.appendChild(testPoint.toXml(doc));
      }

      return checkGroupElement;
   }

}