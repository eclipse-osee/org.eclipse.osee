/*********************************************************************
 * Copyright (c) 2023 Boeing
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
export interface artifactWithDirectRelations {
	artId: string;
	artName: string;
	artType: string;
	relations: relation[];
}

export interface relation {
	relationTypeToken: relationTypeToken;
	relationSides: relationSide[];
}

export interface relationTypeToken {
	id: `${number}`;
	idIntValue: number;
	idString: string;
	multiplicity: string;
	name: string;
	newRelationTable: boolean;
	order: string;
	ordered: boolean;
	relationArtifactType: string;
}

export interface relationSide {
	name: string;
	artifacts: artifact[];
	isSideA: boolean;
	isSideB: boolean;
}

export interface artifact {
	name: string;
	id: `${number}`;
	typeId: string;
	typeName: string;
	attributes: attribute[];
	editable: boolean;
}

export interface attribute {
	name: string;
	value: string;
	typeId: string;
	id: string;
	baseType: string;
}

export interface tab {
	artifact: artifact;
	branchId: string;
	viewId: string;
}

export interface artifactHierarchyOptions {
	showRelations: boolean;
}

// Legacy Icon Dictionary - Replace with adding artifact type angular/material icon names to the core artifact tokens
const ARTIFACT_TYPE_ICON_DICTIONARY: { [id: string]: string } = {
	'Actionable Item': 'call_to_action',
	'Agile Backlog': 'list',
	'Agile Feature Group': 'group_work',
	'Agile Program': 'code',
	'Agile Program Backlog': 'list_alt',
	'Agile Program Backlog Item': 'bookmark_border',
	'Agile Program Feature': 'stars',
	'Agile Sprint': 'loop',
	'Agile Story': 'history_edu',
	'Agile Team': 'groups',
	Artifact: 'article',
	'Branch View': 'fork_right',
	Breaker: 'pause_circle_outline',
	'Certification Baseline Event': 'emoji_events',
	Component: 'settings_input_component',
	Country: 'map',
	'Customer Requirement - MS Word': 'rule',
	Feature: 'stars',
	'Feature Definition': 'description',
	Folder: 'folder',
	'General Data': 'dataset',
	'General Document': 'text_snippet',
	'Git Commit': 'commit',
	'Global Preferences': 'settings',
	'Group Artifact': 'group_work',
	'Heading - MS Word': 'view_headline',
	'Implementation Details - MS Word': 'details',
	'MS Word Whole Document': 'article',
	'Osee Type Definition': 'description',
	'Plain Text': 'text_fields',
	Program: 'code',
	'Renderer Template - Whole Word': 'dynamic_form',
	'Software Design - MS Word': 'design_services',
	'Software Requirement - HTML': 'rule',
	'Software Requirement - MS Word': 'rule',
	'Software Requirement Function - MS Word': 'rule',
	'Software Requirement Plain Text': 'rule',
	'Software Requirement Procedure - MS Word': 'rule',
	'Subsystem Requirement - MS Word': 'rule',
	'System Requirement - MS Word': 'rule',
	'Team Definition': 'description',
	'Test Case': 'fact_check',
	'Test Procedure': 'assignment',
	'Universal Group': 'group_work',
	User: 'person_outline',
	'User Group': 'supervised_user_circle',
	'Work Package': 'cases',
	'XViewer Global Customization': 'dashboard_customize',
};
const ARTIFACT_TYPE_ICON_DEFAULT = 'padding';
export function fetchIconFromDictionary(key: string): string {
	return ARTIFACT_TYPE_ICON_DICTIONARY[key] || ARTIFACT_TYPE_ICON_DEFAULT;
}
