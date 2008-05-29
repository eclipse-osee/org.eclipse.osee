/*
 * Created on May 28, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets.workflow;

/**
 * @author Donald G. Dunne
 */
public abstract class WorkItemDefinition {

   protected final String id;
   protected final String name;
   protected final String parentId;
   protected Object data;

   public WorkItemDefinition(String name, String id, String parentId) {
      this.name = name;
      this.id = id;
      this.parentId = parentId;
      if (this.id == null || this.id.equals("")) throw new IllegalArgumentException("id must be unique and non-null");
   }

   public String toString() {
      return id + " - " + name;
   }

   /**
    * @return the id
    */
   public String getId() {
      return id;
   }

   /**
    * @return the name
    */
   public String getName() {
      return name;
   }

   /**
    * @return the parentId
    */
   public String getParentId() {
      return parentId;
   }

   /**
    * @return the data
    */
   public Object getData() {
      return data;
   }

   /**
    * @param data the data to set
    */
   public void setData(Object data) {
      this.data = data;
   }

}
