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
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { apiURL } from '../../../../../../environments/environment';
import { affectedArtifact } from '../../types/affectedArtifact';

@Injectable({
	providedIn: 'root',
})
export class AffectedArtifactService {
	constructor(private http: HttpClient) {}
	getEnumSetsByEnums(branchId: string | number, enumId: string | number) {
		return this.http.get<affectedArtifact[]>(
			apiURL + '/mim/branch/' + branchId + '/affected/enums/' + enumId
		);
	}

	getPlatformTypesByEnumSet(
		branchId: string | number,
		enumSetId: string | number
	) {
		return this.http.get<affectedArtifact[]>(
			apiURL +
				'/mim/branch/' +
				branchId +
				'/affected/enumsets/' +
				enumSetId
		);
	}

	getElementsByType(branchId: string | number, typeId: string | number) {
		return this.http.get<affectedArtifact[]>(
			apiURL + '/mim/branch/' + branchId + '/affected/types/' + typeId
		);
	}

	getStructuresByElement(
		branchId: string | number,
		elementId: string | number
	) {
		return this.http.get<affectedArtifact[]>(
			apiURL +
				'/mim/branch/' +
				branchId +
				'/affected/elements/' +
				elementId
		);
	}

	getSubMessagesByStructure(
		branchId: string | number,
		structureId: string | number
	) {
		return this.http.get<affectedArtifact[]>(
			apiURL +
				'/mim/branch/' +
				branchId +
				'/affected/structures/' +
				structureId
		);
	}

	getMessagesBySubMessage(
		branchId: string | number,
		subMessageId: string | number
	) {
		return this.http.get<affectedArtifact[]>(
			apiURL +
				'/mim/branch/' +
				branchId +
				'/affected/submessages/' +
				subMessageId
		);
	}
}
