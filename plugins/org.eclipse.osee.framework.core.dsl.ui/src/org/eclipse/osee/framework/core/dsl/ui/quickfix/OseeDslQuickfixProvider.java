/*********************************************************************
 * Copyright (c) 2010 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.core.dsl.ui.quickfix;

import org.eclipse.xtext.ui.editor.quickfix.DefaultQuickfixProvider;

/**
 * @author Roberto E. Escobar
 */
public class OseeDslQuickfixProvider extends DefaultQuickfixProvider {

   //	@Fix(MyJavaValidator.INVALID_NAME)
   //	public void capitalizeName(final Issue issue, IssueResolutionAcceptor acceptor) {
   //		acceptor.accept(issue, "Capitalize name", "Capitalize the name.", "upcase.png", new IModification() {
   //			public void apply(IModificationContext context) throws BadLocationException {
   //				IXtextDocument xtextDocument = context.getXtextDocument();
   //				String firstLetter = xtextDocument.get(issue.getOffset(), 1);
   //				xtextDocument.replace(issue.getOffset(), 1, firstLetter.toUpperCase());
   //			}
   //		});
   //	}

}
