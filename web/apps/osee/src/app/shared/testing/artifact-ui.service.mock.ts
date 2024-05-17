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
import { ArtifactUiService } from '@osee/shared/services';
import { NamedId } from '@osee/shared/types';
import { of } from 'rxjs';

export const artifactUiServiceMock: Partial<ArtifactUiService> = {
	getArtifactTypes(filter: string) {
		return of([
			{
				id: '1',
				name: 'Artifact Type 1',
			},
			{
				id: '2',
				name: 'Artifact Type 2',
			},
		]);
	},
	getAttributeTypes(artifactTypes: NamedId[]) {
		return of([
			{
				id: '1',
				name: 'Attribute Type 1',
			},
			{
				id: '2',
				name: 'Attribute Type 2',
			},
		]);
	},
	get allArtifactTypes() {
		return of([
			{
				id: '1',
				name: 'Artifact Type 1',
			},
			{
				id: '2',
				name: 'Artifact Type 2',
			},
		]);
	},
	get allAttributeTypes() {
		return of([
			{
				id: '1',
				name: 'Attribute Type 1',
			},
			{
				id: '2',
				name: 'Attribute Type 2',
			},
			{
				id: '3',
				name: 'Attribute Type 3',
			},
		]);
	},
	getArtifactTypeAttributes(artifactTypeId: `${number}`) {
		return of([
			{
				id: '1',
				multiplicityId: '1',
				name: 'Test Attribute',
				storeType: 'String',
				typeId: '123',
				value: 'Test value',
			},
		]);
	},
};
