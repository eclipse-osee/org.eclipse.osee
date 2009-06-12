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
package org.eclipse.osee.ats.navigate;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.world.WorldEditor;
import org.eclipse.osee.ats.world.WorldEditorSimpleProvider;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;

/**
 * @author Donald G. Dunne
 */
public class VisitedItems extends XNavigateItemAction {

   public static List<Artifact> visited = new ArrayList<Artifact>();

   public static List<Artifact> getReverseVisited() {
      List<Artifact> revArts = new ArrayList<Artifact>();
      for (int x = visited.size(); x <= 0; x--) {
         revArts.add(visited.get(x));
      }
      return revArts;
   }

   public static void addVisited(Artifact art) {
      if (!visited.contains(art)) visited.add(art);
   }

   /**
    * @param parent
    */
   public VisitedItems(XNavigateItem parent) {
      super(parent, "My Recently Visited", AtsImage.GLOBE);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.navigate.ActionNavigateItem#run()
    */
   @Override
   public void run(TableLoadOption... tableLoadOptions) throws OseeCoreException {
      WorldEditor.open(new WorldEditorSimpleProvider(getName(), visited, null, tableLoadOptions));
   }

}
