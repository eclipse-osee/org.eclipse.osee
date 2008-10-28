package org.eclipse.osee.framework.jini.service.core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import net.jini.entry.AbstractEntry;
import org.eclipse.osee.framework.jdk.core.util.EnhancedProperties;

/*
 * Created on May 14, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */

/**
 * @author Ken J. Aguilar
 */
public class PropertyEntry extends AbstractEntry {

   /**
    * 
    */
   private static final long serialVersionUID = 8506398896518763116L;
   public HashMap<String, Serializable> map;

   public PropertyEntry() {
      map = new HashMap<String, Serializable>(64);
   }

   public PropertyEntry(Map<String, Serializable> properties) {
      this();
      this.map.putAll(properties);
   }

   public void setProperty(String key, Serializable value) {
      map.put(key, value);
   }

   public Serializable getProperty(String key, Serializable defaultValue) {
      Serializable value = map.get(key);
      return value == null ? defaultValue : value;
   }

   /**
    * fills the supplied {@link EnhancedProperties} object with all the properties contained in this entry
    * 
    * @param props
    */
   public void fillProps(Map<String, Serializable> props) {
      props.putAll(map);
   }

   /* (non-Javadoc)
    * @see net.jini.entry.AbstractEntry#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object other) {
      if (other instanceof PropertyEntry) {
         return ((PropertyEntry) other).map.equals(map);
      } else {
         return false;
      }
   }
   
}
