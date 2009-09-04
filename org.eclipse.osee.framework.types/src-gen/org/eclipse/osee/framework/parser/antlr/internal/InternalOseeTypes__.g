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
T28 : 'MappedAttributeDataProvider' ;
T29 : 'min' ;
T30 : 'max' ;
T31 : 'unlimited' ;
T32 : 'taggerId' ;
T33 : 'DefaultAttributeTaggerProvider' ;
T34 : 'enumType' ;
T35 : 'description' ;
T36 : 'defaultValue' ;
T37 : 'fileExtension' ;
T38 : 'BooleanAttribute' ;
T39 : 'CompressedContentAttribute' ;
T40 : 'DateAttribute' ;
T41 : 'EnumeratedAttribute' ;
T42 : 'FloatingPointAttribute' ;
T43 : 'IntegerAttribute' ;
T44 : 'JavaObjectAttribute' ;
T45 : 'StringAttribute' ;
T46 : 'WordAttribute' ;
T47 : 'oseeEnumType' ;
T48 : 'entry' ;
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

// $ANTLR src "../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g" 1794
RULE_WHOLE_NUM_STR : ('0'..'9')+;

// $ANTLR src "../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g" 1796
RULE_ID : '^'? ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'_'|'0'..'9')*;

// $ANTLR src "../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g" 1798
RULE_INT : ('0'..'9')+;

// $ANTLR src "../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g" 1800
RULE_STRING : ('"' ('\\' ('b'|'t'|'n'|'f'|'r'|'"'|'\''|'\\')|~(('\\'|'"')))* '"'|'\'' ('\\' ('b'|'t'|'n'|'f'|'r'|'"'|'\''|'\\')|~(('\\'|'\'')))* '\'');

// $ANTLR src "../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g" 1802
RULE_ML_COMMENT : '/*' ( options {greedy=false;} : . )*'*/';

// $ANTLR src "../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g" 1804
RULE_SL_COMMENT : '//' ~(('\n'|'\r'))* ('\r'? '\n')?;

// $ANTLR src "../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g" 1806
RULE_WS : (' '|'\t'|'\r'|'\n')+;

// $ANTLR src "../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g" 1808
RULE_ANY_OTHER : .;


