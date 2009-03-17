/**
 * 
 */
package org.eclipse.osee.framework.ui.skynet.commandHandlers.branch.commit;



/**
 * @author Jeff C. Phillips
 *
 */
public class CommitIntoParentHandler extends CommitHandler{

   private static boolean USE_PARENT_BRANCH = true;
   
   public CommitIntoParentHandler() {
      super(USE_PARENT_BRANCH);
   }
}
