grammar ApplicabilityGrammar;

options {
ASTLabelType=CommonTree;
output=AST;
}


// START:members
@header {
	import java.util.HashMap;
	import java.util.Arrays;
	import java.util.Collections;
}

@members {

	private String applicabilityType = null;
	private String featureId = null;
	
	private ArrayList<String> configIds = new ArrayList<>();
	private HashMap<String, List<String>> featureId_values_map = new HashMap<>();
   private ArrayList<String> featureOperators = new ArrayList<>();
   
   
   public ArrayList<String> getFeatureOperators() {
    	return featureOperators;
   }
   
   public ArrayList<String> getConfigIds() {
    	return configIds;
   }
   
   public HashMap<String, List<String>> getFeatureIdValuesMap() {
   	return featureId_values_map;
  	}
  	
  	public String getApplicabilityType() {
  		return applicabilityType;
  	}
}

start                  :  applicability EOF! { featureOperators.removeAll(Collections.singleton(null)); };

applicability           : config_applicability { applicabilityType="config"; } 
								| feature_applicability { applicabilityType="feature"; } ;
									
config_applicability    : 'Configuration[' config_expressions+ ']';

config_expressions		:  OR? ID { configIds.add($ID.text); };

feature_applicability   : 'Feature[' feature_expressions+ ']' ;

feature_expressions     : (operator? feature_expression) { featureOperators.add($operator.text); };

feature_expression		: ID { featureId = $ID.text; 
										 featureId_values_map.put(featureId, new ArrayList<String>());
										} 
								('=' temp=feature_value)? { if($temp.text == null) {
																		featureId_values_map.put(featureId, Arrays.asList("Default"));
																	  }
																	};
																	
feature_value			:  value	| start_compound ;
							
start_compound			: '(' { featureId_values_map.get(featureId).add("("); } 
							   compound_value 
								')' {	featureId_values_map.get(featureId).add(")"); };

compound_value			: value+ | multiple_compounds;
							
multiple_compounds   : start_compound 
							  operator { featureId_values_map.get(featureId).add($operator.text); } 
							  compound_value;

value				      : temp=operator? ID { 	
														if($temp.text != null)
															featureId_values_map.get(featureId).add($temp.text);
															
														featureId_values_map.get(featureId).add($ID.text);
													
												 		};

operator					: AND | OR;
OR							: '|';
AND						: '&';
ID : ('a'..'z'|'A'..'Z')('a'..'z'|'A'..'Z'|'0'..'9'|'_'|'-')* ;

WS : (' '|'\r'|'\t'|'\n')+ {$channel=HIDDEN;};



