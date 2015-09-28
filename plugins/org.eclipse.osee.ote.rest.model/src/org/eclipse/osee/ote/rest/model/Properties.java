/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.rest.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Andrew M. Finkbeiner
 */
@XmlRootElement
public class Properties {

   private List<KeyValue> pairs;
 
   public Properties(){
      pairs = new ArrayList<>();
   }
   
   public Properties(String simpleName) {
      this();
   }

   @XmlElementWrapper
   @XmlElement(name="Pair")
   public List<KeyValue> getPairs(){
	   return pairs;
   }
   
   public List<KeyValue> setPairs(List<KeyValue> pairs){
      return this.pairs = pairs;
   }

   public void addPair(KeyValue pair){
      pairs.add(pair);
   }

   public void put(String key, boolean value) {
      put(key, Boolean.toString(value));
   }

   public void put(String key, String value) {
      KeyValue pair = findPair(key);
      if(pair == null){
         pairs.add(new KeyValue(key, value));
      } else {
         pair.setValueArray(null);
         pair.setValue(value);
      }
   }

   public void put(String key, List<String> values) {
      KeyValue pair = findPair(key);
      if(pair == null){
         pairs.add(new KeyValue(key, values));
      } else {
         pair.setValueArray(values);
         pair.setValue(null);
      }
   }
   
   public void put(String key, String[] values) {
      put(key, Arrays.asList(values));
   }

   private KeyValue findPair(String key) {
      for(KeyValue pair:pairs){
         if(pair.getKey().equals(key)){
            return pair;
         }
      }
      return null;
   }

   public String get(String key) {
      KeyValue pair = findPair(key);
      if(pair != null){
         return pair.getValue();
      }
      return null;
   }

}
