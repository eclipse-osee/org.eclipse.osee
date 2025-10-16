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
import { from, Observable, of } from 'rxjs';
import { delay, map, mergeMap, tap } from 'rxjs/operators';
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
import { transactionResult } from '@osee/transactions/types';

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
		const supportingInfoRelation = {
			typeId: RELATIONTYPEIDENUM.SUPPORTING_INFO,
			sideA: workflowId,
		};

		let tx = this._currentTx.createTransaction(
			`Creating Attachments To Workflow ${workflowId}`
		);

		const fileReadPromises = files.map((file) => {
			return new Promise<{ file: File; binaryContent: string }>(
				(resolve, reject) => {
					const reader = new FileReader();
					reader.onload = (event) => {
						const binaryContent = (event.target
							?.result as string).split(",")[1];

						console.log(
							`Read binary content for file: ${file.name}`,
							binaryContent
						); // Debugging statement
						resolve({ file, binaryContent });
					};
					reader.onerror = () =>
						reject(new Error(`Failed to read ${file.name}`));
					reader.readAsDataURL(file);
				}
			);
		});

		return of(tx).pipe(
			mergeMap((tx) =>
				from(Promise.all(fileReadPromises)).pipe(
					map((fileResults) => {
						fileResults.forEach(({ file, binaryContent }) => {
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

							console.log(
								`Creating artifact for file: ${file.name}`,
								{
									fileNameAttr,
									fileExtAttr,
									fileNativeContentAttr,
								}
							); // Debugging statement

							const result = createArtifact(
								tx,
								ARTIFACTTYPEIDENUM.GENERALDOCUMENT,
								applicabilitySentinel,
								[supportingInfoRelation],
								undefined,
								fileNameAttr,
								fileExtAttr,
								fileNativeContentAttr
							);

							tx = result.tx; // update tx
						});

						return tx; // emit the updated transaction
					})
				)
			),
			tap((v) => console.log(v)),
			this._currentTx.performMutation(),
			tap((v) => console.log(v)),
			map((mutationResult: Required<transactionResult>) => {
				// Map the mutation result to WorkflowAttachment[]
				return this.getArtifactsFromMutation(mutationResult);
			})
		);
	}

	private arrayBufferToBase64(buffer: ArrayBuffer): string {
		const decoder = new TextDecoder('utf-8');
		return decoder.decode(buffer);
	}

	private getArtifactsFromMutation(
		mutationResult: Required<transactionResult>
	): WorkflowAttachment[] {
		// TODO: derive from mutationResult (e.g., using mutationResult.resultIds, etc.)
		return [];
	}

	updateAttachment(
		workflowId: string,
		attachmentId: NumericString,
		file: File
	): Observable<WorkflowAttachment> {
		const list = this.ensureSeeded(workflowId);
		const idx = list.findIndex((a) => a.id === attachmentId);

		const updated: WorkflowAttachment = {
			...(idx >= 0
				? list[idx]
				: {
						id: attachmentId,
						name: '',
						extension: '',
						sizeInBytes: 0,
					}),
			name: file.name,
			extension: (file.name.split('.').pop() || '').toLowerCase(),
			sizeInBytes: file.size,
			attachmentBytes: undefined,
		};

		if (idx >= 0) {
			const next = [...list];
			next[idx] = updated;
			this.store.set(workflowId, next);
		} else {
			this.store.set(workflowId, [...list, updated]);
		}

		return of(structuredClone(updated)).pipe(delay(this.latencyMs));
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
