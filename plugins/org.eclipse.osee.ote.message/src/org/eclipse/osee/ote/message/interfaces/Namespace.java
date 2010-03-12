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
package org.eclipse.osee.ote.message.interfaces;

/**
 * @author Andrew M. Finkbeiner
 */
public class Namespace implements INamespace{

   private String namespace;
   /**
    * @param string
    */
   public Namespace(String string) {
      namespace = string;
   }
   
   public String toString(){
      return namespace;
   }

   @Override
   public boolean equals(Object obj) {
      Namespace ns = (Namespace)obj;
      return namespace.equals(ns.namespace);
   }


   @Override
   public int hashCode() {
      return namespace.hashCode();
   }

   
   
}
