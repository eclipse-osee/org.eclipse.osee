/*********************************************************************
 * Copyright (c) 2026 Boeing
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

export type MarkdownDiffEntry = {
	artifactId: string;
	artifactName: string;
	changeType: string;
	changeDescription: string;
	wasValue: string;
	isValue: string;
};

export type DiffLineOld = {
	type: 'removed' | 'unchanged' | 'empty';
	lineNumber: number | null;
	content: string;
};

export type DiffLineNew = {
	type: 'added' | 'unchanged' | 'empty';
	lineNumber: number | null;
	content: string;
};

export type DiffRow = {
	old: DiffLineOld;
	new: DiffLineNew;
};

export type ExportFormat = 'md' | 'html';
