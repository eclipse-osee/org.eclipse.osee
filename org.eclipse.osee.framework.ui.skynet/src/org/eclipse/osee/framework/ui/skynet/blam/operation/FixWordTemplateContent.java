package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.WordArtifact;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;
import org.eclipse.osee.framework.ui.skynet.blam.WorkflowEditor;
import org.eclipse.osee.framework.ui.skynet.render.word.WordMLProducer;

/**
 * 
 * @author Jeff C. Phillips
 *
 */
public class FixWordTemplateContent implements BlamOperation {
	private boolean fix = false;
	@Override
	public String getDescriptionUsage() {
		return null;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public void runOperation(BlamVariableMap variableMap, IProgressMonitor monitor) throws Exception {
		Collection<Artifact> artifacts = variableMap.getArtifacts("Artifacts");
		
		for(Artifact artifact : artifacts){
			//template word artifacts only
			if(artifact instanceof WordArtifact && !((WordArtifact)artifact).isWholeWordArtifact()){
				WordArtifact wordArtifact = (WordArtifact)artifact;
				String content = wordArtifact.getContent();
				
                String[] splitString = content.split(WordMLProducer.LISTNUM_FIELD_TAIL_REG_EXP);
                if (splitString.length >= 2 && fix) {
                   content = splitString[0] + "</w:p></wx:sect>";
                   content = content.replace(WordMLProducer.LISTNUM_FIELD_HEAD, "");
                } else {
                   throw new OseeCoreException(
                         "There were errors removing template information from the Word content prior to saving. Content was not saved.");
                }
			}
		}
	}
	
   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getXWidgetXml()
    */
    @Override
    public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XListDropViewer\" displayName=\"artifacts\" /></xWidgets>";
    }

	@Override
	public void setWorkflowEditor(WorkflowEditor workflow) {
		// TODO Auto-generated method stub

	}

	@Override
	public Branch wrapOperationForBranch(BlamVariableMap variableMap)
			throws OseeArgumentException {
		return null;
	}

}
