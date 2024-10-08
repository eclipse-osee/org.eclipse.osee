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
export type healthStatus = {
	servers: healthServer[];
};

export type healthServer = {
	serverAlive: boolean;
	dbAlive: boolean;
	name: string;
	errorMsg: string;
};

export type remoteHealthDetails = {
	healthDetails: healthDetails;
	errorMsg: string;
};
export type healthDetails = {
	uri: string;
	startTime: string;
	upTime: string;
	authScheme: string;
	authSchemeSupported: string[];
	heapMemAlloc: string;
	heapMemMax: string;
	heapMemUsed: string;
	nonHeapMemAlloc: string;
	nonHeapMemMax: string;
	nonHeapMemUsed: string;
	codeLocation: string;
	systemLoad: string;
	supportedVersions: string[];
	serverId: string;
	binaryDataPath: string;
	threadStats: string[];
	garbageCollectorStats: string[];
	serverWithHealthInfo: string;
};

export const defaultRemoteHealthDetails: remoteHealthDetails = {
	healthDetails: {
		uri: '',
		startTime: '',
		upTime: '',
		authScheme: '',
		authSchemeSupported: [],
		heapMemAlloc: '',
		heapMemMax: '',
		heapMemUsed: '',
		nonHeapMemAlloc: '',
		nonHeapMemMax: '',
		nonHeapMemUsed: '',
		codeLocation: '',
		systemLoad: '',
		supportedVersions: [],
		serverId: '',
		binaryDataPath: '',
		threadStats: [],
		garbageCollectorStats: [],
		serverWithHealthInfo: '',
	},
	errorMsg: '',
};

export type remoteHealthLog = {
	healthLog: healthLog;
};

export type healthLog = {
	log: string;
};

export type remoteHealthJava = {
	healthJava: healthJava;
	errorMsg: string;
};

export type healthJava = {
	vmName: string;
	vmVendor: string;
	vmVersion: string;
	vmSpecVersion: string;
	classPath: string;
	libraryPath: string;
	osName: string;
	osVersion: string;
	osArch: string;
	processArgs: string[];
	processes: string[];
};
export type remoteHealthTop = {
	healthTop: healthTop;
	errorMsg: string;
};

export type healthTop = {
	top: string;
};

export type healthBalancers = {
	balancers: healthBalancer[];
};

export type healthBalancer = {
	name: string;
	alive: boolean;
	errorMsg: string;
};

export type healthActiveMq = {
	activeMqUrl: string;
	active: boolean;
	errorMsg: string;
};

export type healthUsage = {
	allUsers: user[];
	allSessions: session[];
	versionTypeMap: versionTypeMap;
	versionNameMap: versionNameMap;
	errorMsg: string;
};

export type user = {
	name: string;
	email: string;
	userId: string;
	accountId: number;
};

export type session = {
	user: user;
	date: string;
	version: string;
	sessionId: string;
	clientAddress: string;
	clientMachineName: string;
	port: string;
};

export type versionTypeMap = Record<string, user[]>;

export type versionNameMap = Record<string, user[]>;

export type unknownJson = Record<string, unknown>;

export type healthSql = {
	errorMsg: string;
	sqls: sql[];
};

export type sql = {
	sqlText: string;
	elapsedTime: string;
	executions: string;
	elapsedTimeAverage: string;
	percent: string;
};

export type healthSqlSize = {
	errorMsg: string;
	size: number;
};

export type healthTablespace = {
	errorMsg: string;
	tablespaces: tablespace[];
};

export type tablespace = {
	tablespaceName: string;
	maxTsPctUsed: string;
	autoExtend: string;
	tsPctUsed: string;
	tsPctFree: string;
	usedTsSize: string;
	freeTsSize: string;
	currTsSize: string;
	maxTxSize: string;
};
