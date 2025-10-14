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
import { createArtifact } from '@osee/transactions/functions';
import { CurrentTransactionService } from '@osee/transactions/services';
import { transactionResult } from '@osee/transactions/types';

@Injectable({ providedIn: 'root' })
export class AttachmentTestingService {
	private readonly latencyMs = 150;
	private store = new Map<string, WorkflowAttachment[]>();

	//////////////

	private http = inject(HttpClient);
	private _currentTx = inject(CurrentTransactionService);
	private teamWfBasePath = '/ats/teamwf';

	listAttachments(workflowId: string): Observable<WorkflowAttachment[]> {
		return this.http.get<WorkflowAttachment[]>(
			apiURL + `${this.teamWfBasePath}/${workflowId}/attachments`
		);
	}

	/////////////

	// listAttachments(workflowId: string): Observable<WorkflowAttachment[]> {
	//   const list = this.ensureSeeded(workflowId);
	//   return of(this.clone(list)).pipe(delay(this.latencyMs));
	// }

	// uploadAttachments(workflowId: string, files: File[]): Observable<WorkflowAttachment[]> {
	//   const now = new Date().toISOString();
	//   const current = this.ensureSeeded(workflowId);
	//   const created: WorkflowAttachment[] = files.map((f, i) => ({
	//     id: `new-${workflowId}-${Date.now()}-${i}`,
	//     name: f.name,
	//     extension: (f.name.split('.').pop() || '').toLowerCase(),
	//     sizeInBytes: f.size,
	//     // For testing we typically omit attachmentBytes in list responses
	//     attachmentBytes: undefined,
	//   }));
	//   this.store.set(workflowId, [...current, ...created]);
	//   return of(this.clone(created)).pipe(delay(this.latencyMs));
	// }

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
			`Creating Workflow Attachments`
		);

		const fileReadPromises = files.map((file) => {
			return new Promise<{ file: File; binaryContent: ArrayBuffer }>(
				(resolve, reject) => {
					const reader = new FileReader();
					reader.onload = (event) => {
						const binaryContent = event.target
							?.result as ArrayBuffer;
						resolve({ file, binaryContent });
					};
					reader.onerror = (e) =>
						reject(new Error(`Failed to read ${file.name}`));
					reader.readAsArrayBuffer(file);
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
								ArrayBuffer,
								ATTRIBUTETYPEID
							> = {
								id: '-1',
								value: binaryContent,
								typeId: ATTRIBUTETYPEIDENUM.NATIVE_CONTENT,
								gammaId: '-1',
							};

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
			// Apply the operator directly, not via switchMap
			this._currentTx.performMutation(),
			tap((v) => console.log(v)),
			map((mutationResult: Required<transactionResult>) => {
				// Map the mutation result to WorkflowAttachment[]
				return this.getArtifactsFromMutation(mutationResult);
			})
		);
	}

	private getArtifactsFromMutation(
		mutationResult: Required<transactionResult>
	): WorkflowAttachment[] {
		// TODO: derive from mutationResult (e.g., using mutationResult.resultIds, etc.)
		return [];
	}

	updateAttachment(
		workflowId: string,
		attachmentId: string,
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

	deleteAttachment(
		workflowId: string,
		attachmentId: string
	): Observable<void> {
		const list = this.ensureSeeded(workflowId);
		this.store.set(
			workflowId,
			list.filter((a) => a.id !== attachmentId)
		);
		return of(undefined).pipe(delay(this.latencyMs));
	}

	getDownloadUrl(
		workflowId: string,
		attachmentId: string
	): Observable<{ url: string }> {
		const url = `about:blank#${encodeURIComponent(workflowId)}-${encodeURIComponent(attachmentId)}`;
		return of({ url }).pipe(delay(this.latencyMs));
	}

	// Simulate a single-item fetch with bytes populated
	getAttachment(
		workflowId: string,
		attachmentId: string
	): Observable<WorkflowAttachment> {
		const content = `Mock content for ${attachmentId} (workflow ${workflowId})`;
		const base64 = btoa(content);
		const list = this.ensureSeeded(workflowId);
		const found = list.find((a) => a.id === attachmentId) ?? {
			id: attachmentId,
			name: `mock-${attachmentId}.txt`,
			extension: 'txt',
			sizeInBytes: content.length,
		};
		const withBytes: WorkflowAttachment = {
			...found,
			attachmentBytes: base64,
		};
		return of(structuredClone(withBytes)).pipe(delay(this.latencyMs));
	}

	private ensureSeeded(workflowId: string): WorkflowAttachment[] {
		if (!this.store.has(workflowId)) {
			const seeded: WorkflowAttachment[] = [
				{
					id: `att-${workflowId}-001`,
					name: 'requirements.pdf',
					extension: 'pdf',
					sizeInBytes: 123456,
				},
				{
					id: `att-${workflowId}-002`,
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
