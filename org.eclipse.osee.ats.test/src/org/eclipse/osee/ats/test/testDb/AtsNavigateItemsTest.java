/*
 * Created on May 12, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.test.testDb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.config.demo.config.PopulateDemoActions;
import org.eclipse.osee.ats.editor.SMAEditor;
import org.eclipse.osee.ats.navigate.AtsNavigateViewItems;
import org.eclipse.osee.ats.navigate.NavigateView;
import org.eclipse.osee.ats.world.WorldView;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;

/**
 * @author Donald G. Dunne
 */
public class AtsNavigateItemsTest extends TestCase {

   private Map<String, XNavigateItem> nameToNavItem = new HashMap<String, XNavigateItem>();

   /* (non-Javadoc)
    * @see junit.framework.TestCase#setUp()
    */
   protected void setUp() throws Exception {
      super.setUp();
      // This test should only be run on test db
      assertFalse(AtsPlugin.isProductionDb());
      // Confirm test setup with demo data
      assertTrue(PopulateDemoActions.isDbPopulatedWithDemoData().isTrue());
      // Setup hash if navigate items to names
      for (XNavigateItem item : AtsNavigateViewItems.getInstance().getSearchNavigateItems())
         createNameToNavItemMap(item, nameToNavItem);
      // Confirm user is Joe Smith
      assertTrue(SkynetAuthentication.getUser().getUserId().equals("Joe Smith"));
   }

   public void testMyWorld() throws Exception {
      XNavigateItem item = nameToNavItem.get("My World");
      NavigateView.getNavigateView().handleDoubleClick(item, TableLoadOption.ForcePend);
      assertTrue(numOfType(WorldView.getLoadedArtifacts(), ActionArtifact.class) == 9);
   }

   public void testMyFavorites() throws Exception {
      XNavigateItem item = nameToNavItem.get("My Favorites");
      NavigateView.getNavigateView().handleDoubleClick(item, TableLoadOption.ForcePend);
      assertTrue(numOfType(WorldView.getLoadedArtifacts(), TeamWorkFlowArtifact.class) == 3);
   }

   public void testMyReviews() throws Exception {
      XNavigateItem item = nameToNavItem.get("My Reviews");
      NavigateView.getNavigateView().handleDoubleClick(item, TableLoadOption.ForcePend);
      assertTrue(numOfType(WorldView.getLoadedArtifacts(), PeerToPeerReviewArtifact.class) == 2);
   }

   public void testMySubscribed() throws Exception {
      XNavigateItem item = nameToNavItem.get("My Subscribed");
      NavigateView.getNavigateView().handleDoubleClick(item, TableLoadOption.ForcePend);
      assertTrue(numOfType(WorldView.getLoadedArtifacts(), TeamWorkFlowArtifact.class) == 1);
   }

   public void testMyRecentVisited() throws Exception {
      // Load My Favorites
      XNavigateItem item = nameToNavItem.get("My Favorites");
      NavigateView.getNavigateView().handleDoubleClick(item, TableLoadOption.ForcePend);
      Collection<Artifact> arts = WorldView.getLoadedArtifacts();
      assertTrue(arts.size() == 3);
      for (Artifact artifact : arts)
         SMAEditor.editArtifact(artifact);
      // Clear WorldView
      WorldView.loadIt("", new ArrayList<Artifact>(), TableLoadOption.ForcePend);
      assertTrue(WorldView.getLoadedArtifacts().size() == 0);
      // Load Recently Visited
      item = nameToNavItem.get("My Recently Visited");
      NavigateView.getNavigateView().handleDoubleClick(item, TableLoadOption.ForcePend);
      assertTrue(WorldView.getLoadedArtifacts().size() == 3);
   }

   public void testMyTeamWorkflows() throws Exception {
      XNavigateItem item = nameToNavItem.get("My Team Workflows");
      NavigateView.getNavigateView().handleDoubleClick(item, TableLoadOption.ForcePend);
      assertTrue(numOfType(WorldView.getLoadedArtifacts(), TeamWorkFlowArtifact.class) == 24);
   }

   public int numOfType(Collection<? extends Artifact> arts, Class<?> clazz) {
      int num = 0;
      for (Artifact art : arts)
         if (clazz.isAssignableFrom(art.getClass())) num++;
      return num;
   }

   public void createNameToNavItemMap(XNavigateItem item, Map<String, XNavigateItem> nameToItemMap) {
      nameToItemMap.put(item.getName(), item);
      for (XNavigateItem child : item.getChildren()) {
         createNameToNavItemMap(child, nameToItemMap);
      }
   }

}
