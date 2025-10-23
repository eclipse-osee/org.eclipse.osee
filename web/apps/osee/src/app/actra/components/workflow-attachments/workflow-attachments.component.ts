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
import { CommonModule } from '@angular/common';
import {
	ChangeDetectionStrategy,
	Component,
	computed,
	effect,
	inject,
	input,
	signal,
} from '@angular/core';
import { MatButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatTooltip } from '@angular/material/tooltip';
import { MatTableModule } from '@angular/material/table';
import { MatCheckboxModule } from '@angular/material/checkbox';

import { WorkflowAttachment, MAX_ATTACHMENT_SIZE_BYTES } from '../../types/actra-types';
import { AttachmentService } from '../../services/attachment.service';
import {
	AddAttachmentsDialogComponent,
	AddAttachmentsDialogData,
} from '../add-attachments-dialog/add-attachments-dialog.component';
import {
	UpdateAttachmentDialogComponent,
	UpdateAttachmentDialogData,
} from '../update-attachment-dialog/update-attachment-dialog.component';
import { catchError, EMPTY, filter, finalize, take, tap } from 'rxjs';
import { BytesPipe } from '@osee/shared/utils';
import { base64ToBlob } from '@osee/shared/utils';
import { MatIcon } from '@angular/material/icon';

@Component({
	selector: 'osee-workflow-attachments',
	imports: [
		CommonModule,
		MatButton,
		MatTooltip,
		MatTableModule,
		MatCheckboxModule,
		BytesPipe,
		MatIcon,
	],
	changeDetection: ChangeDetectionStrategy.OnPush,
	templateUrl: './workflow-attachments.component.html',
})
export class WorkflowAttachmentsComponent {
	teamWorkflowId = input.required<`${number}`>();

	private svc = inject(AttachmentService);
	private dialog = inject(MatDialog);

	protected attachments = signal<WorkflowAttachment[]>([]);
	protected loading = signal<boolean>(false);
	protected error = signal<string | null>(null);

	protected isEmpty = computed(
		() => !this.loading() && this.attachments().length === 0
	);

	// Material table columns.
	protected displayedColumns = [
		'select',
		'name',
		'extension',
		'sizeInBytes',
		'actions',
	];

	// Selection state.
	private selectedIds = signal<Set<`${number}`>>(new Set<`${number}`>());

	// Derived selection states.
	protected selectedCount = computed(() => this.selectedIds().size);
	protected allSelected = computed(() => {
		const total = this.attachments().length;
		return total > 0 && this.selectedIds().size === total;
	});
	protected isIndeterminate = computed(() => {
		const n = this.selectedIds().size;
		const total = this.attachments().length;
		return n > 0 && n < total;
	});

	constructor() {
		effect(() => {
			const id = this.teamWorkflowId();
			if (!id) return;
			this.fetchAttachments(String(id));
		});
	}

	private fetchAttachments(teamWorkflowId: string) {
		this.loading.set(true);
		this.error.set(null);
		this.svc
			.listAttachments(teamWorkflowId)
			.pipe(
				take(1),
				tap((list) => {
					this.attachments.set(list ?? []);
					// Clear selection on refresh.
					this.selectedIds.set(new Set());
				}),
				catchError((err: unknown) => {
					this.error.set(this.extractError(err));
					return EMPTY;
				}),
				finalize(() => {
					this.loading.set(false);
				})
			)
			.subscribe();
	}

	openAddDialog() {
		const data: AddAttachmentsDialogData = {
			maxFiles: 20,
			maxFileSizeBytes: MAX_ATTACHMENT_SIZE_BYTES,
		};
		this.dialog
			.open(AddAttachmentsDialogComponent, { data })
			.afterClosed()
			.pipe(
				take(1),
				filter((val) => val !== undefined),
				tap((files) => {
					if (!files || !files.length) return;
					this.uploadFiles(files);
				})
			)
			.subscribe();
	}

	openUpdateDialog(att: WorkflowAttachment) {
		const data: UpdateAttachmentDialogData = {
			attachment: {
				id: att.id,
				fileName: att.name,
				sizeBytes: att.sizeInBytes,
			},
			maxFileSizeBytes: MAX_ATTACHMENT_SIZE_BYTES,
		};
		this.dialog
			.open(UpdateAttachmentDialogComponent, { data })
			.afterClosed()
			.pipe(
				take(1),
				filter((val) => val !== undefined),
				tap((file) => {
					if (!file) return;
					this.updateFile(att, file);
				})
			)
			.subscribe();
	}

	openAttachment(att: WorkflowAttachment) {
		if (att.attachmentBytes && att.sizeInBytes != 0) {
			const blob = base64ToBlob(att.attachmentBytes, att.extension);
			const url = URL.createObjectURL(blob);
			window.open(url, '_blank');
			setTimeout(() => URL.revokeObjectURL(url), 3000);
			return;
		}

		this.svc
			.getAttachment(att.id)
			.pipe(
				take(1),
				tap((withBytes) => {
					const list = this.attachments().map((a) =>
						a.id === withBytes.id ? withBytes : a
					);
					this.attachments.set(list);
				}),
				tap((withBytes) => {
					if (!withBytes.attachmentBytes) return;
					const blob = base64ToBlob(
						withBytes.attachmentBytes,
						withBytes.extension
					);
					const url = URL.createObjectURL(blob);
					window.open(url, '_blank');
					setTimeout(() => URL.revokeObjectURL(url), 3000);
				}),
				catchError((err: unknown) => {
					this.error.set(this.extractError(err));
					return EMPTY;
				})
			)
			.subscribe();
	}

	downloadAttachment(attachment: WorkflowAttachment) {
		if (attachment.attachmentBytes && attachment.sizeInBytes != 0) {
			const blob = base64ToBlob(
				attachment.attachmentBytes,
				attachment.extension
			);
			const url = URL.createObjectURL(blob);
			const a = document.createElement('a');
			a.href = url;
			a.download = attachment.name || 'attachment';
			document.body.appendChild(a);
			a.click();
			a.remove();
			setTimeout(() => URL.revokeObjectURL(url), 3000);
			return;
		}

		this.svc
			.getAttachment(attachment.id)
			.pipe(
				take(1),
				tap((withBytes) => {
					const list = this.attachments().map((a) =>
						a.id === withBytes.id ? withBytes : a
					);
					this.attachments.set(list);
				}),
				tap((withBytes) => {
					if (
						!withBytes.attachmentBytes ||
						withBytes.sizeInBytes === 0
					)
						return;
					const blob = base64ToBlob(
						withBytes.attachmentBytes,
						withBytes.extension
					);
					const url = URL.createObjectURL(blob);
					const a = document.createElement('a');
					a.href = url;
					a.download = withBytes.name || 'attachment';
					document.body.appendChild(a);
					a.click();
					a.remove();
					setTimeout(() => URL.revokeObjectURL(url), 3000);
				}),
				catchError((err: unknown) => {
					this.error.set(this.extractError(err));
					return EMPTY;
				})
			)
			.subscribe();
	}

	private uploadFiles(files: File[]) {
		const id = String(this.teamWorkflowId());
		this.loading.set(true);
		this.svc
			.uploadAttachments(id, files)
			.pipe(
				take(1),
				tap((attachments: WorkflowAttachment[]) => {
					// Returns full refreshed list.
					this.attachments.set(attachments);
				}),
				catchError((err: unknown) => {
					this.error.set(this.extractError(err));
					return EMPTY;
				}),
				finalize(() => {
					this.loading.set(false);
				})
			)
			.subscribe();
	}

	private updateFile(att: WorkflowAttachment, file: File) {
		const id = String(this.teamWorkflowId());
		this.loading.set(true);
		this.svc
			.updateAttachment(id, att, file)
			.pipe(
				take(1),
				tap((updated) => {
					const list = this.attachments().map((a) =>
						a.id === updated.id ? updated : a
					);
					this.attachments.set(list);

					this.selectedIds.update((prev) => {
						const next = new Set(prev);
						next.delete(att.id);
						return next;
					});
				}),
				catchError((err) => {
					this.error.set(this.extractError(err));
					return EMPTY;
				}),
				finalize(() => {
					this.loading.set(false);
				})
			)
			.subscribe();
	}

	isSelected(id: `${number}`): boolean {
		return this.selectedIds().has(id);
	}

	toggle(id: `${number}`, checked: boolean): void {
		this.selectedIds.update((prev) => {
			const next = new Set(prev);
			return checked ? next.add(id) : (next.delete(id), next);
		});
	}

	toggleAll(checked: boolean): void {
		if (!checked) {
			this.selectedIds.set(new Set());
			return;
		}
		this.selectedIds.set(new Set(this.attachments().map((a) => a.id)));
	}

	deleteSelected(): void {
		const ids = Array.from(this.selectedIds());
		const count = ids.length;
		if (count === 0) return;
		if (!confirm(`Delete ${count} selected attachment(s)?`)) return;

		this.loading.set(true);

		this.svc
			.deleteAttachments(String(this.teamWorkflowId()), ids)
			.pipe(
				take(1),
				tap(() => {
					this.attachments.set(
						this.attachments().filter((a) => !ids.includes(a.id))
					);
					this.selectedIds.set(new Set());
				}),
				catchError((err: unknown) => {
					this.error.set(this.extractError(err));
					return EMPTY;
				}),
				finalize(() => {
					this.loading.set(false);
				})
			)
			.subscribe();
	}

	deleteSingle(attachment: WorkflowAttachment): void {
		if (!confirm(`Delete ${attachment.name}?`)) return;

		this.loading.set(true);

		this.svc
			.deleteAttachments(String(this.teamWorkflowId()), [attachment.id])
			.pipe(
				take(1),
				tap(() => {
					this.attachments.set(
						this.attachments().filter((a) => a.id !== attachment.id)
					);
					this.selectedIds.set(new Set());
				}),
				catchError((err: unknown) => {
					this.error.set(this.extractError(err));
					return EMPTY;
				}),
				finalize(() => {
					this.loading.set(false);
				})
			)
			.subscribe();
	}

	private extractError(err: unknown): string {
		if (err instanceof Error) {
			return err.message || 'Request failed';
		}

		if (typeof err === 'object' && err !== null) {
			const maybeMessage = (err as { message?: unknown }).message;
			if (typeof maybeMessage === 'string' && maybeMessage.length) {
				return maybeMessage;
			}

			const maybeError = (err as { error?: unknown }).error;
			if (typeof maybeError === 'object' && maybeError !== null) {
				const nestedMessage = (maybeError as { message?: unknown })
					.message;
				if (typeof nestedMessage === 'string' && nestedMessage.length) {
					return nestedMessage;
				}
			}
			if (typeof maybeError === 'string' && maybeError.length) {
				return maybeError;
			}
		}

		if (typeof err === 'string') {
			return err;
		}

		return 'Request failed';
	}
}
