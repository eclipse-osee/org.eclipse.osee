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
import { Injectable, inject } from '@angular/core';
import { Observable, defer, map } from 'rxjs';
import {
	ATTRIBUTETYPEID,
	ATTRIBUTETYPEIDENUM,
	BASEATTRIBUTETYPEIDENUM,
} from '@osee/attributes/constants';
import { newAttribute } from '@osee/attributes/types';
import { applicabilitySentinel } from '@osee/applicability/types';
import {
	ARTIFACTTYPEIDENUM,
	RELATIONTYPEIDENUM,
} from '@osee/shared/types/constants';
import { createArtifact } from '@osee/transactions/functions';
import { CurrentTransactionService } from '@osee/transactions/services';
import { transactionResult } from '@osee/transactions/types';
import {
	readFileAsBase64,
	getFileExtension,
	getFileNameWithoutExtension,
} from '@osee/shared/utils';

export type ImageUploadResult = {
	readonly artifactId: string;
	readonly imageName: string;
};

@Injectable({ providedIn: 'root' })
export class MarkdownImageService {
	private readonly currentTx = inject(CurrentTransactionService);

	uploadImageArtifact(
		parentArtifactId: string,
		file: File
	): Observable<ImageUploadResult> {
		return defer(() => {
			const imageName = getFileNameWithoutExtension(file.name);

			return readFileAsBase64(file).pipe(
				map(({ binaryContent }) => {
					const hierarchicalRelation = {
						typeId: RELATIONTYPEIDENUM.DEFAULT_HIERARCHICAL,
						sideA: parentArtifactId,
					};

					const nameAttr: newAttribute<string, ATTRIBUTETYPEID> = {
						id: '-1',
						value: imageName,
						typeId: BASEATTRIBUTETYPEIDENUM.NAME,
						gammaId: '-1',
					};

					const extensionAttr: newAttribute<
						string,
						ATTRIBUTETYPEID
					> = {
						id: '-1',
						value: getFileExtension(file.name),
						typeId: ATTRIBUTETYPEIDENUM.EXTENSION,
						gammaId: '-1',
					};

					const nativeContentAttr: newAttribute<
						string,
						ATTRIBUTETYPEID
					> = {
						id: '-1',
						value: binaryContent,
						typeId: ATTRIBUTETYPEIDENUM.NATIVE_CONTENT,
						gammaId: '-1',
					};

					const tx = this.currentTx.createTransaction(
						`Upload image: ${file.name}`
					);

					createArtifact(
						tx,
						ARTIFACTTYPEIDENUM.IMAGE,
						applicabilitySentinel,
						[hierarchicalRelation],
						undefined,
						nameAttr,
						extensionAttr,
						nativeContentAttr
					);

					return tx;
				}),
				this.currentTx.performMutation(),
				map((txResult: Required<transactionResult>) => {
					const createdId = txResult.results.ids?.at(0);
					if (!createdId) {
						throw new Error(
							'Image artifact creation succeeded but no artifact ID was returned.'
						);
					}
					return {
						artifactId: createdId,
						imageName,
					} as ImageUploadResult;
				})
			);
		});
	}
}
