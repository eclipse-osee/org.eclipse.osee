/*********************************************************************
 * Copyright (c) 2022 Boeing
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
const _gcArtifactTypeId = {
	CONTEXT: '3962411134691320126',
	COMMAND: '3605711044364389729',
	EXECUTED_COMMAND: '3605721345366379123',
	EXECUTED_COMMAND_HISTORY: '3102324341367389724',
	PARAMETER: '5334063606392099440',
	PARAMETER_INTEGER: '3007766441141267760',
	PARAMETER_BRANCH: '4683538775178036503',
	PARAMETER_BOOLEAN: '9092244262700990331',
	PARAMETER_STRING: '6057500041616318960',
} as const;
type gcArtifactId = (typeof _gcArtifactTypeId)[keyof typeof _gcArtifactTypeId];
