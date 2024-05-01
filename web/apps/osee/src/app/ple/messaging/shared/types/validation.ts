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
export interface ConnectionValidationResult {
	branch: string;
	viewId: string;
	connectionName: string;
	passed: boolean;
	structureByteAlignmentErrors: string[];
	duplicateStructureNameErrors: string[];
	messageTypeErrors: string[];
}

export const connectionValidationResultSentinel: ConnectionValidationResult = {
	branch: '-1',
	viewId: '-1',
	connectionName: '',
	passed: true,
	structureByteAlignmentErrors: [],
	duplicateStructureNameErrors: [],
	messageTypeErrors: [],
};
