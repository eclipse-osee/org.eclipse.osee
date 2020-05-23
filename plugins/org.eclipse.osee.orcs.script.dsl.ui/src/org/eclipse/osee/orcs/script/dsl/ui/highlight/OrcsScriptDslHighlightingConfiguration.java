/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.orcs.script.dsl.ui.highlight;

import static org.eclipse.osee.orcs.script.dsl.ui.OrcsScriptDslUiConstants.BOOLEAN;
import static org.eclipse.osee.orcs.script.dsl.ui.OrcsScriptDslUiConstants.COLOR__BACKGROUND;
import static org.eclipse.osee.orcs.script.dsl.ui.OrcsScriptDslUiConstants.COLOR__BOOLEAN;
import static org.eclipse.osee.orcs.script.dsl.ui.OrcsScriptDslUiConstants.COLOR__COMMENT;
import static org.eclipse.osee.orcs.script.dsl.ui.OrcsScriptDslUiConstants.COLOR__NULL;
import static org.eclipse.osee.orcs.script.dsl.ui.OrcsScriptDslUiConstants.COLOR__NUMBER;
import static org.eclipse.osee.orcs.script.dsl.ui.OrcsScriptDslUiConstants.COLOR__STRING;
import static org.eclipse.osee.orcs.script.dsl.ui.OrcsScriptDslUiConstants.COLOR__VARIABLE;
import static org.eclipse.osee.orcs.script.dsl.ui.OrcsScriptDslUiConstants.COMMENT;
import static org.eclipse.osee.orcs.script.dsl.ui.OrcsScriptDslUiConstants.COMMENT_FONT;
import static org.eclipse.osee.orcs.script.dsl.ui.OrcsScriptDslUiConstants.NULL;
import static org.eclipse.osee.orcs.script.dsl.ui.OrcsScriptDslUiConstants.NUMBER;
import static org.eclipse.osee.orcs.script.dsl.ui.OrcsScriptDslUiConstants.STRING;
import static org.eclipse.osee.orcs.script.dsl.ui.OrcsScriptDslUiConstants.STYLE_ID__BOOLEAN;
import static org.eclipse.osee.orcs.script.dsl.ui.OrcsScriptDslUiConstants.STYLE_ID__COMMENT;
import static org.eclipse.osee.orcs.script.dsl.ui.OrcsScriptDslUiConstants.STYLE_ID__NULL;
import static org.eclipse.osee.orcs.script.dsl.ui.OrcsScriptDslUiConstants.STYLE_ID__NUMBER;
import static org.eclipse.osee.orcs.script.dsl.ui.OrcsScriptDslUiConstants.STYLE_ID__STRING;
import static org.eclipse.osee.orcs.script.dsl.ui.OrcsScriptDslUiConstants.STYLE_ID__VARIABLE;
import static org.eclipse.osee.orcs.script.dsl.ui.OrcsScriptDslUiConstants.TEXT_FONT;
import static org.eclipse.osee.orcs.script.dsl.ui.OrcsScriptDslUiConstants.VARIABLE;
import com.google.inject.Singleton;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingConfiguration;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingConfigurationAcceptor;
import org.eclipse.xtext.ui.editor.utils.TextStyle;

/**
 * @author Roberto E. Escobar
 */
@Singleton
public class OrcsScriptDslHighlightingConfiguration implements IHighlightingConfiguration {

   @Override
   public void configure(IHighlightingConfigurationAcceptor acceptor) {
      addStyle(acceptor, STYLE_ID__VARIABLE, VARIABLE, COLOR__VARIABLE);
      addStyle(acceptor, STYLE_ID__COMMENT, COMMENT, COLOR__COMMENT, COMMENT_FONT);
      addStyle(acceptor, STYLE_ID__STRING, STRING, COLOR__STRING);
      addStyle(acceptor, STYLE_ID__NUMBER, NUMBER, COLOR__NUMBER);
      addStyle(acceptor, STYLE_ID__BOOLEAN, BOOLEAN, COLOR__BOOLEAN);
      addStyle(acceptor, STYLE_ID__NULL, NULL, COLOR__NULL, SWT.BOLD);
   }

   private void addStyle(IHighlightingConfigurationAcceptor acceptor, String styleId, String styleName, RGB foregroundText) {
      addStyle(acceptor, styleId, styleName, foregroundText, SWT.NORMAL);
   }

   private void addStyle(IHighlightingConfigurationAcceptor acceptor, String styleId, String styleName, RGB foregroundText, int style) {
      addStyle(acceptor, styleId, styleName, COLOR__BACKGROUND, foregroundText, TEXT_FONT, style);
   }

   private void addStyle(IHighlightingConfigurationAcceptor acceptor, String styleId, String styleName, RGB foregroundText, FontData fontData) {
      addStyle(acceptor, styleId, styleName, COLOR__BACKGROUND, foregroundText, fontData, SWT.NORMAL);
   }

   private void addStyle(IHighlightingConfigurationAcceptor acceptor, String styleId, String styleName, RGB backgroundText, RGB foregroundText, FontData fontData, int style) {
      TextStyle textStyle = new TextStyle();
      textStyle.setBackgroundColor(backgroundText);
      textStyle.setColor(foregroundText);
      textStyle.setStyle(style);
      textStyle.setFontData(fontData);
      acceptor.acceptDefaultHighlighting(styleId, styleName, textStyle);
   }

}
