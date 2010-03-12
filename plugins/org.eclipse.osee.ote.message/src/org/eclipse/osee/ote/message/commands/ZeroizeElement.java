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
package org.eclipse.osee.ote.message.commands;

import java.io.Serializable;
import java.util.List;

import org.eclipse.osee.ote.message.enums.MemType;




/**
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 * @author Ken J. Aguilar
 */
public class ZeroizeElement implements Serializable {


	   /**
	 * 
	 */
	private static final long serialVersionUID = 2245725475520729629L;
	private final String message;
	   private final List<Object> element;
       private final MemType type;
	   
	   /**
	    * 
	    */
	   public ZeroizeElement(String message, MemType type, List<Object> elementPath) {
	      super();
	      this.message = message;
          this.type = type;
	      this.element = elementPath;
	   }
       
       
       
	   /**
	    * @return Returns the message.
	    */
	   public String getMessage() {
	      return message;
	   }
	   /**
	    * @return Returns the data.
	    */
	   public List<Object> getElement() {
	      return element;
	   }
       
       public MemType getMemType() {
          return type;
       }
}
