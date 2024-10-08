/*********************************************************************
 * Copyright (c) 2024 Boeing
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
export type world = {
	orderedHeaders: string[];
	rows: worldRow[];
	collectorArt: collectorArt;
	atsId: string;
};

export type collectorArt = {
	name: string;
};

export type worldRow = Record<string, string>;

export type worldRowWithDiffs = Record<
	string,
	{
		value: string;
		added: boolean;
		deleted: boolean;
		changed: boolean;
	}
>;

export type worldWithDiffs = {
	orderedHeaders: string[];
	rows: worldRowWithDiffs[];
	collectorArt: collectorArt;
	atsId: string;
};
