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
export interface DiffEndPoint {
	id: string;
	name: string;
	endpointUrl: string;
}

export interface Config {
	programsToBuilds: ProgramToBuild[];
}

export interface ProgramToBuild {
	builds: Build[];
	program: Program;
}

export interface Build {
	name: string;
	guid: string;
}

export interface Program {
	name: string;
	guid: string;
}

export interface SearchOptions {
	workflowNum: string;
	workflowDesc: string;
	program: string;
	build: string;
	displaySearch: Boolean;
}
