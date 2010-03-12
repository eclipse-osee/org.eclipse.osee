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
package org.eclipse.osee.ote.core.framework.saxparse.elements;


/**
 * @author Andrew M. Finkbeiner
 *
 */
public class OfpErrorEntryData {

   private String nodeId;
   private String count;
   private String severity;
   private String version;
   
   /**
    * @return the nodeId
    */
   public String getNodeId() {
      return nodeId;
   }

   /**
    * @return the count
    */
   public String getCount() {
      return count;
   }

   /**
    * @return the severity
    */
   public String getSeverity() {
      return severity;
   }

   /**
    * @return the version
    */
   public String getVersion() {
      return version;
   }

   /**
    * @param nodeId
    * @param value2
    * @param value3
    * @param value4
    */
   OfpErrorEntryData(String nodeId, String count, String severity, String version) {
      this.nodeId = nodeId;
      this.count = count;
      this.severity = severity;
      this.version = version;
   }

}
