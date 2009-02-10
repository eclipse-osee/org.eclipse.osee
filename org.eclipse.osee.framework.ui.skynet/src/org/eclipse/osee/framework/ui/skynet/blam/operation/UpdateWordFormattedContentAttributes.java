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
package org.eclipse.osee.framework.ui.skynet.blam.operation;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;

/**
 * 
 * @author Jeff C. Phillips
 *
 */
public class UpdateWordFormattedContentAttributes extends AbstractBlam {
	private static final String UPDATE_ATTRIBUTE_TYPE_WHOLE_WORD = "update osee_attribute set attr_type_id = 963 where gamma_id in (select t2.gamma_id from osee_artifact t1, osee_attribute t2 where t1.art_id = t2.art_id and t2.attr_type_id = 19 and t1.art_type_id in (select art_type_id from osee_artifact_type where name in ('Checklist (WordML)', 'Guideline', 'How To', 'Renderer Template', 'Roadmap','Template (WordML)', 'Test Procedure WML', 'Work Instruction', 'Work Sheet (WordML)')))";
	private static final String UPDATE_ATTRIBUTE_TYPE_WORD_TEMPLATE = "update osee_attribute set attr_type_id = 962 where attr_type_id = 19";
	private static final String UPDATE_ATTR_VAL_WHOLE_WORD = "update osee_valid_attributes set attr_type_id = 963 where attr_type_id = 19 and art_type_id in (select art_type_id from osee_artifact_type where name in ('Checklist (WordML)', 'Guideline', 'How To', 'Renderer Template', 'Roadmap','Template (WordML)', 'Test Procedure WML', 'Work Instruction', 'Work Sheet (WordML)'))";
	private static final String UPDATE_ATTR_VAL_WORD_TEMP = "update osee_valid_attributes set attr_type_id = 962 where attr_type_id = 19";
	
	@Override
	public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
		monitor.beginTask("Update Word Fromatted Content attributes", 4);
		monitor.setTaskName("UPDATE_ATTRIBUTE_TYPE_WHOLE_WORD");
		ConnectionHandler.runPreparedUpdate(UPDATE_ATTRIBUTE_TYPE_WHOLE_WORD);
		monitor.worked(1);
		monitor.setTaskName("UPDATE_ATTRIBUTE_TYPE_WORD_TEMPLATE");
		ConnectionHandler.runPreparedUpdate(UPDATE_ATTRIBUTE_TYPE_WORD_TEMPLATE);
		monitor.worked(1);
		monitor.setTaskName("UPDATE_ATTR_VAL_WHOLE_WORD");
		ConnectionHandler.runPreparedUpdate(UPDATE_ATTR_VAL_WHOLE_WORD);
		monitor.worked(1);
		monitor.setTaskName("UPDATE_ATTR_VAL_WORD_TEMP");
		ConnectionHandler.runPreparedUpdate(UPDATE_ATTR_VAL_WORD_TEMP);
		monitor.done();
	}
}
