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
T36 : 'BOTH' ;
T37 : 'import' ;
T38 : '.' ;
T39 : 'artifactType' ;
T40 : '{' ;
T41 : 'guid' ;
T42 : '}' ;
T43 : 'extends' ;
T44 : ',' ;
T45 : 'attribute' ;
T46 : 'branchGuid' ;
T47 : 'attributeType' ;
T48 : 'dataProvider' ;
T49 : 'min' ;
T50 : 'max' ;
T51 : 'overrides' ;
T52 : 'taggerId' ;
T53 : 'enumType' ;
T54 : 'description' ;
T55 : 'defaultValue' ;
T56 : 'fileExtension' ;
T57 : 'oseeEnumType' ;
T58 : 'entry' ;
T59 : 'entryGuid' ;
T60 : 'overrides enum' ;
T61 : 'add' ;
T62 : 'remove' ;
T63 : 'relationType' ;
T64 : 'sideAName' ;
T65 : 'sideAArtifactType' ;
T66 : 'sideBName' ;
T67 : 'sideBArtifactType' ;
T68 : 'defaultOrderType' ;
T69 : 'multiplicity' ;
T70 : 'artifact' ;
T71 : 'artGuid' ;
T72 : ';' ;
T73 : 'branch' ;
T74 : 'accessContext' ;
T75 : 'childrenOf' ;
T76 : 'edit' ;
T77 : 'of' ;
T78 : 'abstract' ;
T79 : 'inheritAll' ;

// $ANTLR src "../org.eclipse.osee.framework.core.dsl.ui/src-gen/org/eclipse/osee/framework/core/dsl/ui/contentassist/antlr/internal/InternalOseeDsl.g" 7822
RULE_WHOLE_NUM_STR : ('0'..'9')+;

// $ANTLR src "../org.eclipse.osee.framework.core.dsl.ui/src-gen/org/eclipse/osee/framework/core/dsl/ui/contentassist/antlr/internal/InternalOseeDsl.g" 7824
RULE_ID : '^'? ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'_'|'0'..'9')*;

// $ANTLR src "../org.eclipse.osee.framework.core.dsl.ui/src-gen/org/eclipse/osee/framework/core/dsl/ui/contentassist/antlr/internal/InternalOseeDsl.g" 7826
RULE_INT : ('0'..'9')+;

// $ANTLR src "../org.eclipse.osee.framework.core.dsl.ui/src-gen/org/eclipse/osee/framework/core/dsl/ui/contentassist/antlr/internal/InternalOseeDsl.g" 7828
RULE_STRING : ('"' ('\\' ('b'|'t'|'n'|'f'|'r'|'"'|'\''|'\\')|~(('\\'|'"')))* '"'|'\'' ('\\' ('b'|'t'|'n'|'f'|'r'|'"'|'\''|'\\')|~(('\\'|'\'')))* '\'');

// $ANTLR src "../org.eclipse.osee.framework.core.dsl.ui/src-gen/org/eclipse/osee/framework/core/dsl/ui/contentassist/antlr/internal/InternalOseeDsl.g" 7830
RULE_ML_COMMENT : '/*' ( options {greedy=false;} : . )*'*/';

// $ANTLR src "../org.eclipse.osee.framework.core.dsl.ui/src-gen/org/eclipse/osee/framework/core/dsl/ui/contentassist/antlr/internal/InternalOseeDsl.g" 7832
RULE_SL_COMMENT : '//' ~(('\n'|'\r'))* ('\r'? '\n')?;

// $ANTLR src "../org.eclipse.osee.framework.core.dsl.ui/src-gen/org/eclipse/osee/framework/core/dsl/ui/contentassist/antlr/internal/InternalOseeDsl.g" 7834
RULE_WS : (' '|'\t'|'\r'|'\n')+;

// $ANTLR src "../org.eclipse.osee.framework.core.dsl.ui/src-gen/org/eclipse/osee/framework/core/dsl/ui/contentassist/antlr/internal/InternalOseeDsl.g" 7836
RULE_ANY_OTHER : .;


