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
import { map, mergeMap } from 'rxjs/operators';
import { WorkflowAttachment } from '../types/team-workflow';
import { apiURL } from '@osee/environments';
import { CurrentTransactionService } from '@osee/transactions/services';
import { createArtifact } from '@osee/transactions/functions';
import {
	ARTIFACTTYPEIDENUM,
	RELATIONTYPEIDENUM,
} from '@osee/shared/types/constants';
import {
	ATTRIBUTETYPEIDENUM,
	BASEATTRIBUTETYPEIDENUM,
	ATTRIBUTETYPEID,
} from '@osee/attributes/constants';
import { applicabilitySentinel } from '@osee/applicability/types';
import { newAttribute } from '@osee/attributes/types';

@Injectable({ providedIn: 'root' })
export class AttachmentService {
	private http = inject(HttpClient);
	private _currentTx = inject(CurrentTransactionService);
	private teamWfBasePath = '/ats/teamwf';

	listAttachments(workflowId: string): Observable<WorkflowAttachment[]> {
		return this.http.get<WorkflowAttachment[]>(
			apiURL + `${this.teamWfBasePath}/${workflowId}/attachments`
		);
	}

	// uploadAttachments(workflowId: string, files: File[]): Observable<WorkflowAttachment[]> {
	//   const form = new FormData();
	//   files.forEach((f) => form.append('files', f));
	//   return this.http.post<WorkflowAttachment[]>(`${this.teamWfBasePath}/${workflowId}/attachments`, form);
	// }

	// Helper function to get file name without extension
	private getFileNameWithoutExtension(fileName: string): string {
		return fileName.split('.').slice(0, -1).join('.');
	}

	// Helper function to get file extension
	private getFileExtension(fileName: string): string {
		return fileName.split('.').pop() || '';
	}


	// uploadAttachments(
	// 	workflowId: string,
	// 	files: File[]
	// ): Observable<WorkflowAttachment[]> {
	uploadAttachments(
		workflowId: string,
		files: File[]
	) {
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
					reader.onerror = (e) => reject(new Error(`Failed to read ${file.name}`));
					reader.readAsArrayBuffer(file);
				}
			);
		});

		of(tx).pipe(
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
			// Apply the operator directly, not via switchMap
			this._currentTx.performMutation()
			// TODO: map the transactionResult to WorkflowAttachment[] if needed:
			// map((mutationResult) => toWorkflowAttachments(mutationResult))
		);
	}

	updateAttachment(
		workflowId: string,
		attachmentId: string,
		file: File
	): Observable<WorkflowAttachment> {
		const form = new FormData();
		form.append('file', file);
		return this.http.put<WorkflowAttachment>(
			`${this.teamWfBasePath}/${workflowId}/attachments/${attachmentId}`,
			form
		);
	}

	deleteAttachment(
		workflowId: string,
		attachmentId: string
	): Observable<void> {
		return this.http.delete<void>(
			`${this.teamWfBasePath}/${workflowId}/attachments/${attachmentId}`
		);
	}

	// If your backend provides a URL endpoint for direct open:
	getDownloadUrl(
		workflowId: string,
		attachmentId: string
	): Observable<{ url: string }> {
		return this.http.get<{ url: string }>(
			`${this.teamWfBasePath}/${workflowId}/attachments/${attachmentId}/download-url`
		);
	}

	// If you have a dedicated endpoint that returns a single WorkflowAttachment with attachmentBytes:
	getAttachment(
		workflowId: string,
		attachmentId: string
	): Observable<WorkflowAttachment> {
		return this.http.get<WorkflowAttachment>(
			`${this.teamWfBasePath}/${workflowId}/attachments/${attachmentId}`
		);
	}
}
