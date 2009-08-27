lexer grammar InternalOseeTypes;
@header {
package org.eclipse.osee.framework.contentassist.antlr.internal;

// Hack: Use our own Lexer superclass by means of import. 
// Currently there is no other way to specify the superclass for the lexer.
import org.eclipse.xtext.ui.common.editor.contentassist.antlr.internal.Lexer;
}

T12 : 'DefaultAttributeDataProvider' ;
T13 : 'UriAttributeDataProvider' ;
T14 : 'MappedAttributeDataProvider' ;
T15 : 'unlimited' ;
T16 : 'DefaultAttributeTaggerProvider' ;
T17 : 'BooleanAttribute' ;
T18 : 'CompressedContentAttribute' ;
T19 : 'DateAttribute' ;
T20 : 'EnumeratedAttribute' ;
T21 : 'FloatingPointAttribute' ;
T22 : 'IntegerAttribute' ;
T23 : 'JavaObjectAttribute' ;
T24 : 'StringAttribute' ;
T25 : 'WordAttribute' ;
T26 : 'Lexicographical_Ascending' ;
T27 : 'Lexicographical_Descending' ;
T28 : 'Unordered' ;
T29 : 'ONE_TO_MANY' ;
T30 : 'MANY_TO_MANY' ;
T31 : 'MANY_TO_ONE' ;
T32 : 'ONE_TO_ONE' ;
T33 : 'import' ;
T34 : '.' ;
T35 : 'abstract' ;
T36 : 'artifactType' ;
T37 : '{' ;
T38 : '}' ;
T39 : 'extends' ;
T40 : 'attribute' ;
T41 : 'attributeType' ;
T42 : 'dataProvider' ;
T43 : 'min' ;
T44 : 'max' ;
T45 : 'taggerId' ;
T46 : 'enumType' ;
T47 : 'description' ;
T48 : 'defaultValue' ;
T49 : 'fileExtension' ;
T50 : 'oseeEnumType' ;
T51 : 'entry' ;
T52 : 'relationType' ;
T53 : 'sideAName' ;
T54 : 'sideAArtifactType' ;
T55 : 'sideBName' ;
T56 : 'sideBArtifactType' ;
T57 : 'defaultOrderType' ;
T58 : 'multiplicity' ;

// $ANTLR src "../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g" 2607
RULE_WHOLE_NUM_STR : ('0'..'9')+;

// $ANTLR src "../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g" 2609
RULE_ID : '^'? ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'_'|'0'..'9')*;

// $ANTLR src "../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g" 2611
RULE_INT : ('0'..'9')+;

// $ANTLR src "../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g" 2613
RULE_STRING : ('"' ('\\' ('b'|'t'|'n'|'f'|'r'|'"'|'\''|'\\')|~(('\\'|'"')))* '"'|'\'' ('\\' ('b'|'t'|'n'|'f'|'r'|'"'|'\''|'\\')|~(('\\'|'\'')))* '\'');

// $ANTLR src "../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g" 2615
RULE_ML_COMMENT : '/*' ( options {greedy=false;} : . )*'*/';

// $ANTLR src "../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g" 2617
RULE_SL_COMMENT : '//' ~(('\n'|'\r'))* ('\r'? '\n')?;

// $ANTLR src "../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g" 2619
RULE_WS : (' '|'\t'|'\r'|'\n')+;

// $ANTLR src "../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g" 2621
RULE_ANY_OTHER : .;


