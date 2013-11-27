package org.eclipse.osee.ote.rest.model;

import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class KeyValue {

   private String key;
   private String value;
   
   private List<String> values;
   
   public KeyValue(){
      
   }
   
   public KeyValue(String key, String value){
      this.key = key;
      this.value = value;
   }

   public KeyValue(String key, List<String> values) {
      this.key = key;
      this.values = values;
   }

   public String getKey() {
      return key;
   }

   public void setKey(String key) {
      this.key = key;
   }

   public String getValue() {
      return value;
   }

   public void setValue(String value) {
      this.value = value;
   }
   
   @XmlElementWrapper
   @XmlElement(name="Values")
   public List<String> getValues() {
      return values;
   }
   
   public List<String> setValues(List<String> values) {
      return this.values = values;
   }

   public void setValueArray(List<String> values) {
      this.values = values;
   }

}
