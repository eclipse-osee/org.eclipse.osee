/*********************************************************************
 * Copyright (c) 2025 Boeing
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
import { HttpClient } from '@angular/common/http';
import { defer, forkJoin, from, Observable, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { WorkflowAttachment } from '../types/actra-types';
import { apiURL } from '@osee/environments';
import { applicabilitySentinel } from '@osee/applicability/types';
import {
	ATTRIBUTETYPEID,
	BASEATTRIBUTETYPEIDENUM,
	ATTRIBUTETYPEIDENUM,
} from '@osee/attributes/constants';
import { newAttribute, validAttribute } from '@osee/attributes/types';
import {
	RELATIONTYPEIDENUM,
	ARTIFACTTYPEIDENUM,
} from '@osee/shared/types/constants';
import { createArtifact, deleteArtifact } from '@osee/transactions/functions';
import { CurrentTransactionService } from '@osee/transactions/services';
import {
	getFileExtension,
	getFileNameWithoutExtension,
} from '@osee/shared/utils';
import { transactionResult } from '@osee/transactions/types';

@Injectable({ providedIn: 'root' })
export class AttachmentService {
	private http = inject(HttpClient);
	private _currentTx = inject(CurrentTransactionService);
	private teamWfBasePath = '/ats/teamwf';

	listAttachments(workflowId: string): Observable<WorkflowAttachment[]> {
		return this.http.get<WorkflowAttachment[]>(
			apiURL +
				`${this.teamWfBasePath}/${workflowId}/attachments?returnBytes=false`
		);
	}

	uploadAttachments(
		workflowId: string,
		files: File[]
	): Observable<Required<transactionResult>> {
		return defer(() => {
			const supportingInfoRelation = {
				typeId: RELATIONTYPEIDENUM.SUPPORTING_INFO,
				sideA: workflowId,
			};

			const initialTx = this._currentTx.createTransaction(
				`Adding Attachments To Workflow ${workflowId}`
			);

			// Read all files in parallel.
			const reads$ = forkJoin(
				files.map((file) => this.readFileAsBase64(file))
			);

			return reads$.pipe(
				map((fileResults) => {
					return fileResults.reduce(
						(accTx, { file, binaryContent }) => {
							const fileNameAttr: newAttribute<
								string,
								ATTRIBUTETYPEID
							> = {
								id: '-1',
								value: getFileNameWithoutExtension(file.name),
								typeId: BASEATTRIBUTETYPEIDENUM.NAME,
								gammaId: '-1',
							};

							const fileExtAttr: newAttribute<
								string,
								ATTRIBUTETYPEID
							> = {
								id: '-1',
								value: getFileExtension(file.name),
								typeId: ATTRIBUTETYPEIDENUM.EXTENSION,
								gammaId: '-1',
							};

							const fileNativeContentAttr: newAttribute<
								string,
								ATTRIBUTETYPEID
							> = {
								id: '-1',
								value: binaryContent,
								typeId: ATTRIBUTETYPEIDENUM.NATIVE_CONTENT,
								gammaId: '-1',
							};

							const result = createArtifact(
								accTx,
								ARTIFACTTYPEIDENUM.GENERALDOCUMENT,
								applicabilitySentinel,
								[supportingInfoRelation],
								undefined,
								fileNameAttr,
								fileExtAttr,
								fileNativeContentAttr
							);

							return result.tx;
						},
						initialTx
					);
				}),
				this._currentTx.performMutation()
			);
		});
	}

	private readFileAsBase64(
		file: File
	): Observable<{ file: File; binaryContent: string }> {
		return from(
			new Promise<{ file: File; binaryContent: string }>(
				(resolve, reject) => {
					const reader = new FileReader();

					reader.onload = () => {
						const result = reader.result;
						if (typeof result !== 'string') {
							reject(
								new Error(
									`Unexpected reader result for ${file.name}`
								)
							);
							return;
						}
						const base64 = result.split(',')[1] ?? '';
						if (!base64) {
							reject(new Error(`Empty content for ${file.name}`));
							return;
						}
						resolve({ file, binaryContent: base64 });
					};

					reader.onerror = () =>
						reject(new Error(`Failed to read ${file.name}`));

					reader.readAsDataURL(file);
				}
			)
		);
	}

	updateAttachment(
		workflowId: string,
		attachment: WorkflowAttachment,
		file: File
	): Observable<Required<transactionResult>> {
		return defer(() => {
			return this.readFileAsBase64(file).pipe(
				switchMap((fileResult) => {
					const fileNameAttr: validAttribute<
						string,
						ATTRIBUTETYPEID
					> = {
						id: attachment.nameAtId,
						value: getFileNameWithoutExtension(file.name),
						typeId: BASEATTRIBUTETYPEIDENUM.NAME,
						gammaId: attachment.nameGamma,
					};

					const fileExtAttr: validAttribute<string, ATTRIBUTETYPEID> =
						{
							id: attachment.extensionAtId,
							value: getFileExtension(file.name),
							typeId: ATTRIBUTETYPEIDENUM.EXTENSION,
							gammaId: attachment.extensionGamma,
						};

					const fileNativeContentAttr: validAttribute<
						string,
						ATTRIBUTETYPEID
					> = {
						id: attachment.nativeContentAtId,
						value: fileResult.binaryContent,
						typeId: ATTRIBUTETYPEIDENUM.NATIVE_CONTENT,
						gammaId: attachment.nativeContentGamma,
					};

					const set = [
						fileNameAttr,
						fileExtAttr,
						fileNativeContentAttr,
					];

					return this._currentTx.modifyArtifactAndMutate(
						`Updating Attachment Of Workflow ${workflowId}`,
						attachment.id,
						applicabilitySentinel,
						{ set }
					);
				})
			);
		});
	}

	deleteAttachments(
		workflowId: string,
		attachmentIds: `${number}`[]
	): Observable<Required<transactionResult>> {
		let tx = this._currentTx.createTransaction(
			`Deleting Attachments From Workflow ${workflowId}`
		);

		for (const attachmentId of attachmentIds) {
			tx = deleteArtifact(tx, attachmentId); // update tx
		}

		return of(tx).pipe(this._currentTx.performMutation());
	}

	getDownloadUrl(
		workflowId: string,
		attachmentId: `${number}`
	): Observable<{ url: string }> {
		const url = `about:blank#${encodeURIComponent(workflowId)}-${encodeURIComponent(attachmentId)}`;
		return of({ url });
	}

	getAttachment(attachmentId: `${number}`): Observable<WorkflowAttachment> {
		return this.http.get<WorkflowAttachment>(
			apiURL + `${this.teamWfBasePath}/${attachmentId}/attachment`
		);
	}
}
