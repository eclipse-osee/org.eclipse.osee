/**
 * 
 */
package org.eclipse.osee.framework.ui.skynet.render;

/**
 * Used to store data about renderer for menu options
 * 
 * @author Jeff C. Phillips
 */
public class PreviewRendererData {

   private String name;
   private Object[] option;

   /**
    * @return the option
    */
   public Object[] getOption() {
      return option;
   }

   /**
    * @return the name
    */
   public String getName() {
      return name;
   }

   public PreviewRendererData(String name, Object... option) {
      super();
      this.name = name;
      this.option = option;
   }
}
