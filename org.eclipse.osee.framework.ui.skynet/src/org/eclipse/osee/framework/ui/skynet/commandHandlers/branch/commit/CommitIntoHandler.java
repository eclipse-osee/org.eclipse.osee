/**
 * 
 */
package org.eclipse.osee.framework.ui.skynet.commandHandlers.branch.commit;


/**
 * @author Jeff C. Phillips
 *
 */
public class CommitIntoHandler extends CommitHandler{

   private static boolean USE_PARENT_BRANCH = false;
   
   public CommitIntoHandler() {
      super(USE_PARENT_BRANCH);
   }
}
