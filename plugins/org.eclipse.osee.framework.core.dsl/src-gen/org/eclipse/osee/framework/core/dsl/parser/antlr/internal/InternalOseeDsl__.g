lexer grammar InternalOseeDsl;
@header {
package org.eclipse.osee.framework.core.dsl.parser.antlr.internal;

// Hack: Use our own Lexer superclass by means of import. 
// Currently there is no other way to specify the superclass for the lexer.
import org.eclipse.xtext.parser.antlr.Lexer;
}

T12 : 'import' ;
T13 : '.' ;
T14 : 'abstract' ;
T15 : 'artifactType' ;
T16 : 'extends' ;
T17 : ',' ;
T18 : '{' ;
T19 : 'guid' ;
T20 : '}' ;
T21 : 'attribute' ;
T22 : 'branchGuid' ;
T23 : 'attributeType' ;
T24 : 'overrides' ;
T25 : 'dataProvider' ;
T26 : 'DefaultAttributeDataProvider' ;
T27 : 'UriAttributeDataProvider' ;
T28 : 'min' ;
T29 : 'max' ;
T30 : 'unlimited' ;
T31 : 'taggerId' ;
T32 : 'DefaultAttributeTaggerProvider' ;
T33 : 'enumType' ;
T34 : 'description' ;
T35 : 'defaultValue' ;
T36 : 'fileExtension' ;
T37 : 'BooleanAttribute' ;
T38 : 'CompressedContentAttribute' ;
T39 : 'DateAttribute' ;
T40 : 'EnumeratedAttribute' ;
T41 : 'FloatingPointAttribute' ;
T42 : 'IntegerAttribute' ;
T43 : 'JavaObjectAttribute' ;
T44 : 'StringAttribute' ;
T45 : 'WordAttribute' ;
T46 : 'oseeEnumType' ;
T47 : 'entry' ;
T48 : 'entryGuid' ;
T49 : 'overrides enum' ;
T50 : 'inheritAll' ;
T51 : 'add' ;
T52 : 'remove' ;
T53 : 'relationType' ;
T54 : 'sideAName' ;
T55 : 'sideAArtifactType' ;
T56 : 'sideBName' ;
T57 : 'sideBArtifactType' ;
T58 : 'defaultOrderType' ;
T59 : 'multiplicity' ;
T60 : 'Lexicographical_Ascending' ;
T61 : 'Lexicographical_Descending' ;
T62 : 'Unordered' ;
T63 : 'artifact' ;
T64 : 'artGuid' ;
T65 : ';' ;
T66 : 'branch' ;
T67 : 'accessContext' ;
T68 : 'childrenOf' ;
T69 : 'edit' ;
T70 : 'of' ;
T71 : 'ONE_TO_ONE' ;
T72 : 'ONE_TO_MANY' ;
T73 : 'MANY_TO_ONE' ;
T74 : 'MANY_TO_MANY' ;
T75 : 'ALLOW' ;
T76 : 'DENY' ;
T77 : 'SIDE_A' ;
T78 : 'SIDE_B' ;
T79 : 'BOTH' ;

// $ANTLR src "../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g" 3193
RULE_WHOLE_NUM_STR : ('0'..'9')+;

// $ANTLR src "../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g" 3195
RULE_ID : '^'? ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'_'|'0'..'9')*;

// $ANTLR src "../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g" 3197
RULE_INT : ('0'..'9')+;

// $ANTLR src "../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g" 3199
RULE_STRING : ('"' ('\\' ('b'|'t'|'n'|'f'|'r'|'"'|'\''|'\\')|~(('\\'|'"')))* '"'|'\'' ('\\' ('b'|'t'|'n'|'f'|'r'|'"'|'\''|'\\')|~(('\\'|'\'')))* '\'');

// $ANTLR src "../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g" 3201
RULE_ML_COMMENT : '/*' ( options {greedy=false;} : . )*'*/';

// $ANTLR src "../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g" 3203
RULE_SL_COMMENT : '//' ~(('\n'|'\r'))* ('\r'? '\n')?;

// $ANTLR src "../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g" 3205
RULE_WS : (' '|'\t'|'\r'|'\n')+;

// $ANTLR src "../org.eclipse.osee.framework.core.dsl/src-gen/org/eclipse/osee/framework/core/dsl/parser/antlr/internal/InternalOseeDsl.g" 3207
RULE_ANY_OTHER : .;


