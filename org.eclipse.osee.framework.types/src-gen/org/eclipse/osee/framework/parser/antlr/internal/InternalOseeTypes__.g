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
T49 : 'entryGuid' ;
T50 : 'overrides enum' ;
T51 : 'inheritAll' ;
T52 : 'add' ;
T53 : 'remove' ;
T54 : 'relationType' ;
T55 : 'sideAName' ;
T56 : 'sideAArtifactType' ;
T57 : 'sideBName' ;
T58 : 'sideBArtifactType' ;
T59 : 'defaultOrderType' ;
T60 : 'multiplicity' ;
T61 : 'Lexicographical_Ascending' ;
T62 : 'Lexicographical_Descending' ;
T63 : 'Unordered' ;
T64 : 'ONE_TO_ONE' ;
T65 : 'ONE_TO_MANY' ;
T66 : 'MANY_TO_ONE' ;
T67 : 'MANY_TO_MANY' ;

// $ANTLR src "../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g" 1818
RULE_WHOLE_NUM_STR : ('0'..'9')+;

// $ANTLR src "../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g" 1820
RULE_ID : '^'? ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'_'|'0'..'9')*;

// $ANTLR src "../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g" 1822
RULE_INT : ('0'..'9')+;

// $ANTLR src "../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g" 1824
RULE_STRING : ('"' ('\\' ('b'|'t'|'n'|'f'|'r'|'"'|'\''|'\\')|~(('\\'|'"')))* '"'|'\'' ('\\' ('b'|'t'|'n'|'f'|'r'|'"'|'\''|'\\')|~(('\\'|'\'')))* '\'');

// $ANTLR src "../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g" 1826
RULE_ML_COMMENT : '/*' ( options {greedy=false;} : . )*'*/';

// $ANTLR src "../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g" 1828
RULE_SL_COMMENT : '//' ~(('\n'|'\r'))* ('\r'? '\n')?;

// $ANTLR src "../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g" 1830
RULE_WS : (' '|'\t'|'\r'|'\n')+;

// $ANTLR src "../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g" 1832
RULE_ANY_OTHER : .;


