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
export type DiffEndPoint = {
	id: string;
	name: string;
	endpointUrl: string;
};

export type Config = {
	programsToBuilds: ProgramToBuild[];
};

export type ProgramToBuild = {
	builds: Build[];
	program: Program;
};

export type Build = {
	name: string;
	guid: string;
};

export type Program = {
	name: string;
	guid: string;
};

export type SearchOptions = {
	workflowNum: string;
	workflowDesc: string;
	program: string;
	build: string;
	displaySearch: boolean;
};
