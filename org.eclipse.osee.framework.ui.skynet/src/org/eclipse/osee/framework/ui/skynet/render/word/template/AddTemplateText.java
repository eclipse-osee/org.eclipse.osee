/*
 * Created on Mar 5, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.render.word.template;

import java.sql.SQLException;
import java.util.List;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.render.word.WordMLProducer;


/**
 * @author b1528444
 *
 */
public class AddTemplateText implements ITemplateTask {

	private String template;
	private int begin;
	private int end;
	/**
	 * @param last
	 * @param start
	 * @param template
	 */
	public AddTemplateText(int begin, int end, String template) {
		this.template = template;
		this.end = end;
		this.begin = begin;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.osee.framework.ui.skynet.render.word.template.ITemplateTask#process(java.lang.StringBuilder, org.eclipse.osee.framework.skynet.core.artifact.Artifact, java.util.List)
	 */
	@Override
	public void process(WordMLProducer wordMl, Artifact artifact,
			List<ITemplateAttributeHandler> handlers) throws SQLException,
			Exception {
		wordMl.addWordMl(template.subSequence(begin, end));
	}

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.word.template.ITemplateTask#isTypeNameWildcard()
    */
   @Override
   public boolean isTypeNameWildcard() {
      return false;
   }


}
