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
import { delay, map, mergeMap, switchMap, tap } from 'rxjs/operators';
import { WorkflowAttachment } from '../types/team-workflow';
import { apiURL } from '@osee/environments';
import { applicabilitySentinel } from '@osee/applicability/types';
import {
	ATTRIBUTETYPEID,
	BASEATTRIBUTETYPEIDENUM,
	ATTRIBUTETYPEIDENUM,
} from '@osee/attributes/constants';
import { newAttribute } from '@osee/attributes/types';
import {
	RELATIONTYPEIDENUM,
	ARTIFACTTYPEIDENUM,
} from '@osee/shared/types/constants';
import { createArtifact, deleteArtifact } from '@osee/transactions/functions';
import { CurrentTransactionService } from '@osee/transactions/services';

type NumericString = `${number}`;

@Injectable({ providedIn: 'root' })
export class AttachmentTestingService {
	private readonly latencyMs = 150;
	private store = new Map<string, WorkflowAttachment[]>();

	private http = inject(HttpClient);
	private _currentTx = inject(CurrentTransactionService);
	private teamWfBasePath = '/ats/teamwf';

	listAttachments(workflowId: string): Observable<WorkflowAttachment[]> {
		return this.http.get<WorkflowAttachment[]>(
			apiURL + `${this.teamWfBasePath}/${workflowId}/attachments`
		);
	}

	// Helper function to get file name without extension
	private getFileNameWithoutExtension(fileName: string): string {
		return fileName.split('.').slice(0, -1).join('.');
	}

	// Helper function to get file extension
	private getFileExtension(fileName: string): string {
		return fileName.split('.').pop() || '';
	}

	uploadAttachments(
		workflowId: string,
		files: File[]
	): Observable<WorkflowAttachment[]> {
		return defer(() => {
			const supportingInfoRelation = {
				typeId: RELATIONTYPEIDENUM.SUPPORTING_INFO,
				sideA: workflowId,
			};

			const initialTx = this._currentTx.createTransaction(
				`Creating Attachments To Workflow ${workflowId}`
			);

			// Read all files in parallel
			const reads$ = forkJoin(
				files.map((file) => this.readFileAsBase64(file))
			);

			return reads$.pipe(
				// Build up the transaction immutably from initialTx
				map((fileResults) => {
					return fileResults.reduce(
						(accTx, { file, binaryContent }) => {
							const fileNameAttr: newAttribute<
								string,
								ATTRIBUTETYPEID
							> = {
								id: '-1',
								value: this.getFileNameWithoutExtension(
									file.name
								),
								typeId: BASEATTRIBUTETYPEIDENUM.NAME,
								gammaId: '-1',
							};

							const fileExtAttr: newAttribute<
								string,
								ATTRIBUTETYPEID
							> = {
								id: '-1',
								value: this.getFileExtension(file.name),
								typeId: ATTRIBUTETYPEIDENUM.EXTENSION,
								gammaId: '-1',
							};

							const fileNativeContentAttr: newAttribute<
								string[],
								ATTRIBUTETYPEID
							> = {
								id: '-1',
								value: [binaryContent],
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
				this._currentTx.performMutation(),
				switchMap(() => this.listAttachments(workflowId))
			);
		});
	}

	private readFileAsBase64(
		file: File
	): Observable<{ file: File; binaryContent: string }> {
		return new Observable((subscriber) => {
			const reader = new FileReader();

			reader.onload = () => {
				const result = reader.result;
				if (typeof result !== 'string') {
					subscriber.error(
						new Error(`Unexpected reader result for ${file.name}`)
					);
					return;
				}
				const base64 = result.split(',')[1] ?? '';
				if (!base64) {
					subscriber.error(
						new Error(`Empty content for ${file.name}`)
					);
					return;
				}
				subscriber.next({ file, binaryContent: base64 });
				subscriber.complete();
			};

			reader.onerror = () => {
				subscriber.error(new Error(`Failed to read ${file.name}`));
			};

			reader.readAsDataURL(file);

			return () => {
				// Cleanup
				try {
					reader.abort();
				} catch {
					//
				}
			};
		});
	}

	// updateAttachment(
	// 	workflowId: string,
	// 	attachmentId: NumericString,
	// 	file: File
	// ): Observable<WorkflowAttachment> {
	// 	const list = this.ensureSeeded(workflowId);
	// 	const idx = list.findIndex((a) => a.id === attachmentId);

	// 	const updated: WorkflowAttachment = {
	// 		...(idx >= 0
	// 			? list[idx]
	// 			: {
	// 					id: attachmentId,
	// 					name: '',
	// 					extension: '',
	// 					sizeInBytes: 0,
	// 				}),
	// 		name: file.name,
	// 		extension: (file.name.split('.').pop() || '').toLowerCase(),
	// 		sizeInBytes: file.size,
	// 		attachmentBytes: undefined,
	// 	};

	// 	if (idx >= 0) {
	// 		const next = [...list];
	// 		next[idx] = updated;
	// 		this.store.set(workflowId, next);
	// 	} else {
	// 		this.store.set(workflowId, [...list, updated]);
	// 	}

	// 	return of(structuredClone(updated)).pipe(delay(this.latencyMs));
	// }

	updateAttachment(
		workflowId: string,
		attachmentId: NumericString,
		file: File
	): Observable<WorkflowAttachment> {
		return defer(() => {
			return this.readFileAsBase64(file).pipe(
				switchMap((fileResult) => {
					const fileNameAttr: newAttribute<string, ATTRIBUTETYPEID> =
						{
							id: '-1',
							value: this.getFileNameWithoutExtension(file.name),
							typeId: BASEATTRIBUTETYPEIDENUM.NAME,
							gammaId: '-1',
						};

					const fileExtAttr: newAttribute<string, ATTRIBUTETYPEID> = {
						id: '-1',
						value: this.getFileExtension(file.name),
						typeId: ATTRIBUTETYPEIDENUM.EXTENSION,
						gammaId: '-1',
					};

					const fileNativeContentAttr: newAttribute<
						string[],
						ATTRIBUTETYPEID
					> = {
						id: '-1',
						value: [fileResult.binaryContent],
						typeId: ATTRIBUTETYPEIDENUM.NATIVE_CONTENT,
						gammaId: '-1',
					};

					const set = [
						fileNameAttr,
						fileExtAttr,
						fileNativeContentAttr,
					];

					return this._currentTx.modifyArtifactAndMutate(
						`Updating Attachment Of Workflow ${workflowId}`,
						attachmentId,
						applicabilitySentinel,
						{set}
					);
				}),
				tap((a) => console.log(a)),
				switchMap(() => this.getAttachment(attachmentId))
			);
		});
	}

	deleteAttachments(workflowId: string, attachmentIds: NumericString[]) {
		let tx = this._currentTx.createTransaction(
			`Deleting Attachments From Workflow ${workflowId}`
		);

		for (const attachmentId of attachmentIds) {
			tx = deleteArtifact(tx, attachmentId); // update tx
		}

		const list = this.ensureSeeded(workflowId);
		const remaining = list.filter((a) => !attachmentIds.includes(a.id));
		this.store.set(workflowId, remaining);

		return of(tx).pipe(this._currentTx.performMutation());
	}

	getDownloadUrl(
		workflowId: string,
		attachmentId: NumericString
	): Observable<{ url: string }> {
		const url = `about:blank#${encodeURIComponent(workflowId)}-${encodeURIComponent(attachmentId)}`;
		return of({ url }).pipe(delay(this.latencyMs));
	}

	getAttachment(attachmentId: NumericString): Observable<WorkflowAttachment> {
		return this.http.get<WorkflowAttachment>(
			apiURL + `${this.teamWfBasePath}/${attachmentId}/attachment`
		);
	}

	// Simulate a single-item fetch with bytes populated
	// getAttachment(
	// 	workflowId: string,
	// 	attachmentId: NumericString
	// ): Observable<WorkflowAttachment> {
	// 	const content = `Mock content for ${attachmentId} (workflow ${workflowId})`;
	// 	const base64 = btoa(content);
	// 	const list = this.ensureSeeded(workflowId);
	// 	const found = list.find((a) => a.id === attachmentId) ?? {
	// 		id: attachmentId,
	// 		name: `mock-${attachmentId}.txt`,
	// 		extension: 'txt',
	// 		sizeInBytes: content.length,
	// 	};
	// 	const withBytes: WorkflowAttachment = {
	// 		...found,
	// 		attachmentBytes: base64,
	// 	};
	// 	return of(structuredClone(withBytes)).pipe(delay(this.latencyMs));
	// }

	private ensureSeeded(workflowId: string): WorkflowAttachment[] {
		if (!this.store.has(workflowId)) {
			// Seed with numeric-string IDs to satisfy `${number}`
			const seeded: WorkflowAttachment[] = [
				{
					id: '1001',
					name: 'requirements.pdf',
					extension: 'pdf',
					sizeInBytes: 123456,
				},
				{
					id: '1002',
					name: 'screenshot.png',
					extension: 'png',
					sizeInBytes: 98765,
				},
			];
			this.store.set(workflowId, seeded);
		}
		return this.store.get(workflowId) as WorkflowAttachment[];
	}

	private clone<T>(arr: T[]): T[] {
		try {
			return structuredClone(arr);
		} catch {
			return JSON.parse(JSON.stringify(arr)) as T[];
		}
	}
}
