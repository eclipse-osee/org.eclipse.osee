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
import { Injectable, Signal, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import {
	HttpClient,
	httpResource,
	HttpResourceRef,
} from '@angular/common/http';
import { defer, forkJoin, merge, Observable, of } from 'rxjs';
import { filter, map, switchMap } from 'rxjs/operators';
import { WorkflowAttachment } from '../types/actra-types';
import { apiURL } from '@osee/environments';
import { COMMON_BRANCH_ID } from '@osee/shared/types';
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
	readFileAsBase64,
} from '@osee/shared/utils';
import { transactionResult } from '@osee/transactions/types';
import { ArtifactChangeNotificationService } from '@osee/shared/services';

@Injectable({ providedIn: 'root' })
export class AttachmentService {
	private readonly http = inject(HttpClient);
	private readonly _currentTx = inject(CurrentTransactionService);
	private readonly changeNotification = inject(
		ArtifactChangeNotificationService
	);
	private readonly teamWfBasePath = '/ats/teamwf';

	/**
	 * Resource for a workflow's attachments, refetched precisely on SSE change notifications.
	 *
	 * Attachments are GENERALDOCUMENT artifacts on the Common branch (the ATS branch), related to
	 * the workflow via SUPPORTING_INFO. Their mutations commit on Common and emit `artifactChanged`
	 * invalidations there. To refetch only when the attachment set actually changes — not on every
	 * Common change — the trigger is scoped to invalidations touching:
	 * - the workflow artifact itself with a RELATION change (relate/unrelate an attachment). A pure
	 *   attribute edit on the workflow (e.g. its description) leaves the attachment set unchanged
	 *   and must not refetch, so the workflow clause is gated on relation change types.
	 * - any currently-listed attachment artifact (an in-place update touches only the attachment,
	 *   not the workflow).
	 * Plus `resync$` to recover changes missed during an SSE disconnect.
	 *
	 * The merged trigger is read as a signal inside the request factory: `httpResource` re-issues
	 * its GET whenever a signal it reads changes. `currentAttachmentIds` is a live signal of the
	 * loaded attachment ids so newly relevant ids (e.g. after a refetch) extend the scope.
	 *
	 * @param workflowId the team workflow id — the Common anchor and the GET path segment
	 * @param currentAttachmentIds ids of the attachments currently shown, so in-place updates to
	 * them trigger a refetch
	 */
	getAttachmentsResource(
		workflowId: Signal<`${number}`>,
		currentAttachmentIds: Signal<`${number}`[]>
	): HttpResourceRef<WorkflowAttachment[] | undefined> {
		const changeTick = toSignal(
			merge(
				this.changeNotification.forBranch(COMMON_BRANCH_ID).pipe(
					// The signal reads here are intentionally UNTRACKED — this is an RxJS operator,
					// not a reactive (computed/effect) context, so the filter simply reads the
					// current values on each SSE emission. That is what keeps the scope current
					// (latest workflow id + loaded attachment ids) without re-subscribing. Do NOT
					// hoist these reads out of the callback: capturing them once would freeze the
					// attachment-id set and break refetch-on-update.
					filter(
						(inv) =>
							// Workflow relate/unrelate changes the attachment set; a
							// pure workflow attribute edit does not, so gate on relation.
							(inv.artifactId === workflowId() &&
								inv.changeTypes.some(
									(t) =>
										t === 'relation_added' ||
										t === 'relation_deleted'
								)) ||
							// In-place update to a currently-listed attachment.
							currentAttachmentIds().includes(
								inv.artifactId as `${number}`
							)
					)
				),
				this.changeNotification.resync$
			),
			{ initialValue: undefined }
		);

		return httpResource(() => {
			changeTick();
			return (
				apiURL +
				`${this.teamWfBasePath}/${workflowId()}/attachments?returnBytes=false`
			);
		});
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
				files.map((file) => readFileAsBase64(file))
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

	updateAttachment(
		workflowId: string,
		attachment: WorkflowAttachment,
		file: File
	): Observable<Required<transactionResult>> {
		return defer(() => {
			return readFileAsBase64(file).pipe(
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

					const fileExtAttr: validAttribute<
						string,
						typeof ATTRIBUTETYPEIDENUM.EXTENSION
					> = {
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
