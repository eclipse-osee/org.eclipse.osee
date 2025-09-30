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
	Component,
	computed,
	effect,
	inject,
	input,
	signal,
} from '@angular/core';
import { MatButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { Attachment } from '../../../types/team-workflow';
// import { AttachmentService } from '../../../services/attachment.service';
import { AttachmentTestingService } from '../../../services/attachment-testing.service';
import {
	AddAttachmentsDialogComponent,
	AddAttachmentsDialogData,
} from '../add-attachments-dialog/add-attachments-dialog.component';
import {
	UpdateAttachmentDialogComponent,
	UpdateAttachmentDialogData,
} from '../update-attachment-dialog/update-attachment-dialog.component';

@Component({
	selector: 'osee-workflow-attachments',
	imports: [CommonModule, MatButton],
	templateUrl: './workflow-attachments.component.html',
})
export class WorkflowAttachmentsComponent {
	// Accept `${number}` from the parent (InputSignal) and strings too
	teamWorkflowId = input.required<`${number}`>();

	// private svc = inject(AttachmentService);
	private svc = inject(AttachmentTestingService);
	private dialog = inject(MatDialog);

	attachments = signal<Attachment[]>([]);
	loading = signal<boolean>(false);
	error = signal<string | null>(null);

	isEmpty = computed(
		() => !this.loading() && this.attachments().length === 0
	);

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
		this.svc.listAttachments(teamWorkflowId).subscribe({
			next: (list) => {
				this.attachments.set(list ?? []);
				this.loading.set(false);
			},
			error: (err) => {
				this.error.set(this.extractError(err));
				this.loading.set(false);
			},
		});
	}

	openAddDialog() {
		const data: AddAttachmentsDialogData = {
			maxFiles: 20,
			maxFileSizeBytes: 50 * 1024 * 1024,
			accept: '*',
		};
		this.dialog
			.open(AddAttachmentsDialogComponent, { data })
			.afterClosed()
			.subscribe((files: File[] | null) => {
				if (!files || !files.length) return;
				this.uploadFiles(files);
			});
	}

	openUpdateDialog(att: Attachment) {
		const data: UpdateAttachmentDialogData = {
			attachment: att,
			accept: '*',
			maxFileSizeBytes: 50 * 1024 * 1024,
		};
		this.dialog
			.open(UpdateAttachmentDialogComponent, { data })
			.afterClosed()
			.subscribe((file: File | null) => {
				if (!file) return;
				this.updateFile(att, file);
			});
	}

	deleteAttachment(att: Attachment) {
		const id = String(this.teamWorkflowId());
		if (!confirm(`Delete "${att.fileName}"?`)) return;
		this.loading.set(true);
		this.svc.deleteAttachment(id, att.id).subscribe({
			next: () => {
				this.attachments.set(
					this.attachments().filter((a) => a.id !== att.id)
				);
				this.loading.set(false);
			},
			error: (err) => {
				this.error.set(this.extractError(err));
				this.loading.set(false);
			},
		});
	}

	openAttachment(att: Attachment) {
		const id = String(this.teamWorkflowId());
		this.svc.getDownloadUrl(id, att.id).subscribe({
			next: ({ url }) => window.open(url, '_blank'),
			error: (err) => this.error.set(this.extractError(err)),
		});
	}

	downloadAttachment(att: Attachment) {
		const id = String(this.teamWorkflowId());
		this.svc.downloadAttachmentBlob(id, att.id).subscribe({
			next: (blob) => {
				const url = URL.createObjectURL(blob);
				const a = document.createElement('a');
				a.href = url;
				a.download = att.fileName || 'attachment';
				document.body.appendChild(a);
				a.click();
				a.remove();
				URL.revokeObjectURL(url);
			},
			error: (err) => this.error.set(this.extractError(err)),
		});
	}

	private uploadFiles(files: File[]) {
		const id = String(this.teamWorkflowId());
		this.loading.set(true);
		this.svc.uploadAttachments(id, files).subscribe({
			next: (uploaded) => {
				this.attachments.set([...this.attachments(), ...uploaded]);
				this.loading.set(false);
			},
			error: (err) => {
				this.error.set(this.extractError(err));
				this.loading.set(false);
			},
		});
	}

	private updateFile(att: Attachment, file: File) {
		const id = String(this.teamWorkflowId());
		this.loading.set(true);
		this.svc.updateAttachment(id, att.id, file).subscribe({
			next: (updated) => {
				const list = this.attachments().map((a) =>
					a.id === updated.id ? updated : a
				);
				this.attachments.set(list);
				this.loading.set(false);
			},
			error: (err) => {
				this.error.set(this.extractError(err));
				this.loading.set(false);
			},
		});
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
