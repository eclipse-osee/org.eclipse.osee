/**
 * 
 */
package org.eclipse.osee.framework.ui.skynet.render;

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * Used to store data about renderer for menu options
 * 
 * @author Jeff C. Phillips
 */
public class PreviewRendererData {
   private String name;
   private Object[] option;
   private ImageDescriptor imageDescriptor;
   private String commandId;

   public PreviewRendererData(String name, String commandId, ImageDescriptor imageDescriptor, Object... option) {
      super();
      this.commandId = commandId;
      this.imageDescriptor = imageDescriptor;
      this.name = name;
      this.option = option;
   }

   /**
    * @return the imageDescriptor
    */
   public ImageDescriptor getImageDescriptor() {
      return imageDescriptor;
   }

   /**
    * @return the commandId
    */
   public String getCommandId() {
      return commandId;
   }

   /**
    * @return the option
    */
   @Deprecated
   public Object[] getOption() {
      return option;
   }

   /**
    * @return the name
    */
   @Deprecated
   public String getName() {
      return name;
   }
}
