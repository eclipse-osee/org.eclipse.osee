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
package org.eclipse.osee.framework.jdk.core.util.requirement;

import java.util.Vector;
import org.eclipse.osee.framework.jdk.core.persistence.Xmlizable;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Ryan D. Brooks
 * @author Robert A. Fisher
 */
public class RequirementId implements Xmlizable {
   // TODO add requirement data
   private String description;
   private Vector<String> partialDescriptions;
   private int timesVerified;
   private boolean isPartial, isWhole, conflictExists;

   /**
    *  
    */
   public RequirementId() {
      this("");
      this.timesVerified = 0;
      this.isPartial = false;
      this.isWhole = false;
      this.conflictExists = false;
   }

   public RequirementId(String description) {
      super();
      this.description = description;
   }

   public String toString() {
      return description;
   }

   public Element toXml(Document doc) {
      return Jaxp.createElement(doc, "RequirementId", description);
   }

   public boolean conflictPresent() {
      return conflictExists;
   }

   /**
    * Increments the count for how many times this requirement has been verified (which should only be once). If this
    * requirement has already been marked as being previously verified (either partially or wholly) than a conflict flag
    * will be set since a requirement can only be wholly verified once.
    */
   public void verifiedHere() {

      // TODO add ability to store in what script this requirement is verified.

      if (isPartial || isWhole) {
         conflictExists = true;
      } else {
         isWhole = true;
         timesVerified++;
      }

   }

   /**
    * Adds a new description to the partial requirement description vector and increments the count for how many times
    * this requirement has been verified. If this requirement has already been marked as being wholly verified than a
    * conflict flag will be set since a partial verify is not needed.
    * 
    * @param partialDesc - Helpful and concise description of what part of the requirement is being verified.
    */
   public void partiallyVerifiedHere(String partialDesc) {

      // TODO add ability to store in what script or function this requirement is partially verified.

      if (isWhole) {
         conflictExists = true;
      } else {
         if (!isPartial) {
            isPartial = true;
         }

         partialDescriptions.add(partialDesc);
         timesVerified++;
      }

   }

   public void usedHere() {

      // TODO add ability to store in what script or function this requirement is used.

   }
}