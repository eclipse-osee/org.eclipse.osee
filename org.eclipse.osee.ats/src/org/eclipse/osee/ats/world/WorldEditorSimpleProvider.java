/*
 * Created on Nov 7, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.world;

import java.util.Collection;
import org.eclipse.osee.ats.world.search.WorldSearchItem.SearchType;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.CustomizeData;

/**
 * @author Donald G. Dunne
 */
public class WorldEditorSimpleProvider extends WorldEditorProvider {

   private final String name;
   private final Collection<? extends Artifact> artifacts;

   public WorldEditorSimpleProvider(String name, Collection<? extends Artifact> artifacts) {
      this(name, artifacts, null, TableLoadOption.None);
   }

   public WorldEditorSimpleProvider(String name, Collection<? extends Artifact> artifacts, CustomizeData customizeData, TableLoadOption... tableLoadOption) {
      super(customizeData, tableLoadOption);
      this.name = name;
      this.artifacts = artifacts;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldEditorProvider#copy(org.eclipse.osee.ats.world.IWorldEditorProvider)
    */
   @Override
   public IWorldEditorProvider copyProvider() {
      return new WorldEditorSimpleProvider(name, artifacts, customizeData, tableLoadOptions);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldEditorProvider#run(org.eclipse.osee.ats.world.WorldEditor)
    */
   @Override
   public void run(WorldEditor worldEditor, SearchType searchtype, boolean forcePend) throws OseeCoreException {
      worldEditor.getWorldComposite().load(name, artifacts, customizeData, getTableLoadOptions());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldEditorProvider#getName()
    */
   @Override
   public String getName() throws OseeCoreException {
      return name;
   }

}
