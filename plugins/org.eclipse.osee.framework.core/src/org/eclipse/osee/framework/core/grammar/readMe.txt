How to make updates to the grammar:
1. Edit ApplicabilityGrammar.g
2. Go to your git workspace into org.eclipse.osee/plugs/org.eclipse.osee.gramework.core/src/org/eclipse/osee/framework/core/grammar
3. In command line: java -jar antlr-3.2.jar ApplicabilityGrammar.g
	this generates new ApplicabilityGrammarLexer.java & ApplicabilityGramarParser.java
4. Add to top of generated files: package org.eclipse.osee.framework.core.grammar;
5. Move generated files under the src-gen folder under org.eclipse.osee.framework.core.grammar


Note: The error below occurs with Antlr 3.2 on any java version higher than 8. Ignore the error becuase the grammar still generated the files correctly.

error(10):  internal error: Can't get property indirectDelegates using method get/isIndirectDelegates from org.antlr.tool.Grammar instance : java.lang.NullPointerException
java.util.Objects.requireNonNull(Unknown Source)
java.util.ArrayList.removeAll(Unknown Source)
org.antlr.tool.CompositeGrammar.getIndirectDelegates(CompositeGrammar.java:222)
org.antlr.tool.Grammar.getIndirectDelegates(Grammar.java:2620)
sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
sun.reflect.NativeMethodAccessorImpl.invoke(Unknown Source)
sun.reflect.DelegatingMethodAccessorImpl.invoke(Unknown Source)
java.lang.reflect.Method.invoke(Unknown Source)
org.antlr.stringtemplate.language.ASTExpr.invokeMethod(ASTExpr.java:564)

