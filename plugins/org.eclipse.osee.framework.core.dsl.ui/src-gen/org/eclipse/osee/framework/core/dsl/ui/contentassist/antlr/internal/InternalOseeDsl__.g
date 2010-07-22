lexer grammar InternalOseeDsl;
@header {
package org.eclipse.osee.framework.core.dsl.ui.contentassist.antlr.internal;

// Hack: Use our own Lexer superclass by means of import. 
// Currently there is no other way to specify the superclass for the lexer.
import org.eclipse.xtext.ui.editor.contentassist.antlr.internal.Lexer;
}

T12 : 'DefaultAttributeDataProvider' ;
T13 : 'UriAttributeDataProvider' ;
T14 : 'unlimited' ;
T15 : 'DefaultAttributeTaggerProvider' ;
T16 : 'BooleanAttribute' ;
T17 : 'CompressedContentAttribute' ;
T18 : 'DateAttribute' ;
T19 : 'EnumeratedAttribute' ;
T20 : 'FloatingPointAttribute' ;
T21 : 'IntegerAttribute' ;
T22 : 'JavaObjectAttribute' ;
T23 : 'StringAttribute' ;
T24 : 'WordAttribute' ;
T25 : 'Lexicographical_Ascending' ;
T26 : 'Lexicographical_Descending' ;
T27 : 'Unordered' ;
T28 : 'ONE_TO_ONE' ;
T29 : 'ONE_TO_MANY' ;
T30 : 'MANY_TO_ONE' ;
T31 : 'MANY_TO_MANY' ;
T32 : 'ALLOW' ;
T33 : 'DENY' ;
T34 : 'SIDE_A' ;
T35 : 'SIDE_B' ;
T36 : 'import' ;
T37 : '.' ;
T38 : 'artifactType' ;
T39 : '{' ;
T40 : 'guid' ;
T41 : '}' ;
T42 : 'extends' ;
T43 : ',' ;
T44 : 'attribute' ;
T45 : 'branchGuid' ;
T46 : 'attributeType' ;
T47 : 'dataProvider' ;
T48 : 'min' ;
T49 : 'max' ;
T50 : 'overrides' ;
T51 : 'taggerId' ;
T52 : 'enumType' ;
T53 : 'description' ;
T54 : 'defaultValue' ;
T55 : 'fileExtension' ;
T56 : 'oseeEnumType' ;
T57 : 'entry' ;
T58 : 'entryGuid' ;
T59 : 'overrides enum' ;
T60 : 'add' ;
T61 : 'remove' ;
T62 : 'relationType' ;
T63 : 'sideAName' ;
T64 : 'sideAArtifactType' ;
T65 : 'sideBName' ;
T66 : 'sideBArtifactType' ;
T67 : 'defaultOrderType' ;
T68 : 'multiplicity' ;
T69 : 'artifact' ;
T70 : 'artGuid' ;
T71 : ';' ;
T72 : 'branch' ;
T73 : 'accessContext' ;
T74 : 'childrenOf' ;
T75 : 'edit' ;
T76 : 'of' ;
T77 : 'abstract' ;
T78 : 'inheritAll' ;

// $ANTLR src "../org.eclipse.osee.framework.core.dsl.ui/src-gen/org/eclipse/osee/framework/core/dsl/ui/contentassist/antlr/internal/InternalOseeDsl.g" 7772
RULE_WHOLE_NUM_STR : ('0'..'9')+;

// $ANTLR src "../org.eclipse.osee.framework.core.dsl.ui/src-gen/org/eclipse/osee/framework/core/dsl/ui/contentassist/antlr/internal/InternalOseeDsl.g" 7774
RULE_ID : '^'? ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'_'|'0'..'9')*;

// $ANTLR src "../org.eclipse.osee.framework.core.dsl.ui/src-gen/org/eclipse/osee/framework/core/dsl/ui/contentassist/antlr/internal/InternalOseeDsl.g" 7776
RULE_INT : ('0'..'9')+;

// $ANTLR src "../org.eclipse.osee.framework.core.dsl.ui/src-gen/org/eclipse/osee/framework/core/dsl/ui/contentassist/antlr/internal/InternalOseeDsl.g" 7778
RULE_STRING : ('"' ('\\' ('b'|'t'|'n'|'f'|'r'|'"'|'\''|'\\')|~(('\\'|'"')))* '"'|'\'' ('\\' ('b'|'t'|'n'|'f'|'r'|'"'|'\''|'\\')|~(('\\'|'\'')))* '\'');

// $ANTLR src "../org.eclipse.osee.framework.core.dsl.ui/src-gen/org/eclipse/osee/framework/core/dsl/ui/contentassist/antlr/internal/InternalOseeDsl.g" 7780
RULE_ML_COMMENT : '/*' ( options {greedy=false;} : . )*'*/';

// $ANTLR src "../org.eclipse.osee.framework.core.dsl.ui/src-gen/org/eclipse/osee/framework/core/dsl/ui/contentassist/antlr/internal/InternalOseeDsl.g" 7782
RULE_SL_COMMENT : '//' ~(('\n'|'\r'))* ('\r'? '\n')?;

// $ANTLR src "../org.eclipse.osee.framework.core.dsl.ui/src-gen/org/eclipse/osee/framework/core/dsl/ui/contentassist/antlr/internal/InternalOseeDsl.g" 7784
RULE_WS : (' '|'\t'|'\r'|'\n')+;

// $ANTLR src "../org.eclipse.osee.framework.core.dsl.ui/src-gen/org/eclipse/osee/framework/core/dsl/ui/contentassist/antlr/internal/InternalOseeDsl.g" 7786
RULE_ANY_OTHER : .;


