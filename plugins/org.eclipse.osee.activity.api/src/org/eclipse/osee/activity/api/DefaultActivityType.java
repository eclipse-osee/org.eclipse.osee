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
package org.eclipse.osee.activity.api;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Ryan D. Brooks
 */
@XmlRootElement
public class DefaultActivityType implements ActivityType {

   private Long typeId;
   private Long logLevel;
   private String module;
   private String messageFormat;

   @Override
   public Long getTypeId() {
      return typeId;
   }

   @Override
   public Long getLogLevel() {
      return logLevel;
   }

   @Override
   public String getModule() {
      return module;
   }

   @Override
   public String getMessageFormat() {
      return messageFormat;
   }

   public void setTypeId(Long typeId) {
      this.typeId = typeId;
   }

   public void setLogLevel(Long logLevel) {
      this.logLevel = logLevel;
   }

   public void setModule(String module) {
      this.module = module;
   }

   public void setMessageFormat(String messageFormat) {
      this.messageFormat = messageFormat;
   }

   @Override
   public String toString() {
      return "DefaultActivityType [typeId=" + typeId + ", logLevel=" + logLevel + ", module=" + module + ", messageFormat=" + messageFormat + "]";
   }

}