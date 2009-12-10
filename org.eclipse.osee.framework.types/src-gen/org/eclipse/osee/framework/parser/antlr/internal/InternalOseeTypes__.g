lexer grammar InternalOseeTypes;
@header {
package org.eclipse.osee.framework.parser.antlr.internal;

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
T63 : 'ONE_TO_ONE' ;
T64 : 'ONE_TO_MANY' ;
T65 : 'MANY_TO_ONE' ;
T66 : 'MANY_TO_MANY' ;

// $ANTLR src "../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g" 1994
RULE_WHOLE_NUM_STR : ('0'..'9')+;

// $ANTLR src "../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g" 1996
RULE_ID : '^'? ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'_'|'0'..'9')*;

// $ANTLR src "../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g" 1998
RULE_INT : ('0'..'9')+;

// $ANTLR src "../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g" 2000
RULE_STRING : ('"' ('\\' ('b'|'t'|'n'|'f'|'r'|'"'|'\''|'\\')|~(('\\'|'"')))* '"'|'\'' ('\\' ('b'|'t'|'n'|'f'|'r'|'"'|'\''|'\\')|~(('\\'|'\'')))* '\'');

// $ANTLR src "../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g" 2002
RULE_ML_COMMENT : '/*' ( options {greedy=false;} : . )*'*/';

// $ANTLR src "../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g" 2004
RULE_SL_COMMENT : '//' ~(('\n'|'\r'))* ('\r'? '\n')?;

// $ANTLR src "../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g" 2006
RULE_WS : (' '|'\t'|'\r'|'\n')+;

// $ANTLR src "../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g" 2008
RULE_ANY_OTHER : .;


