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
package org.eclipse.osee.ote.core.model;

import java.io.Serializable;

@SuppressWarnings("unchecked")
/**
 * This key is used by the model manager to search for a model already instanciated for this environment. 
 * Each of the getter functions MUST return a unique identifier for the specific model they represent ( or null if
 * that particular type isn't available).
 * @author Andrew M. Finkbeiner
 * @param <M> 
 */
public class ModelKey<M extends IModel> implements Serializable, Comparable{
   /**
    * 
    */
   private static final long serialVersionUID = 4735332847721441142L;
   private String className;
   private String name;
   private Class<M> modelClass;

   /**
    * @param className
    */
   public ModelKey(String className) {
      this(className, null, null);
   }
   
   public ModelKey(ModelKey key)
   {
      this( key.className, key.name, null);
   }
   
   /**
    * @param className
    * @param name
    */
   public ModelKey(Class<M> clazz) {
      this(clazz.getCanonicalName(),null, clazz);
   }
   
   /**
    * @param className
    * @param name
    */
   public ModelKey(Class<M> clazz, String name) {
      this(clazz.getCanonicalName(),name, clazz);
   }


   /**
    * @param className
    * @param name
    * @param modelClass
    */
   public ModelKey(String className, String name, Class<M> modelClass) {
      this.className = className;
      this.name = name;
      this.modelClass = modelClass;
   }


   /**
    * return the instance class for the model specified by this key.  This class MUST match for 
    * all keys specifying this particular model.
    * @return The class for the model described by this key. This may be null if no class name is available but in this case
    * {@link #getClassName()} must NOT return null.
    */
   public Class<M> getModelClass()
   {
      return this.modelClass;
   }
   public void setModelClass( Class<M> modelClass)
   {
      this.modelClass = modelClass;
   }
   
   /**
    * return the name of the model specified by this key.  
    * @return The name for the model described by this key
    */
   public String getName()
   {
      if( this.name == null )
         this.name = this.getClassName().substring(this.getClassName().lastIndexOf(".") + 1);
      return this.name;
   }
   /**
    * return the fully qualified class name for the model specified by this key.  This class name MUST match for 
    * all keys specifying this particular model.  
    * @return The class name for the model described by this key. This may be null if no class name is available but in this case
    * {@link #getModelClass()} must NOT return null.  
    */
   public String getClassName()
   {
      if( className != null )
         return this.className;
      else if( modelClass != null )
         return modelClass.getCanonicalName();
      else
         return null;
   }
   
   public boolean equals(Object obj)
   {
      return getClassName().equals(((ModelKey)obj).getClassName());
   }
   
   public int hashCode()
   {
      return getClassName().hashCode();
   }
   
   public int compareTo(Object o ) {
      if( !(o instanceof ModelKey))
         return -1;
      
      ModelKey otherKey = (ModelKey)o;
      return this.getName().compareTo(otherKey.getName());
   }
   
}
