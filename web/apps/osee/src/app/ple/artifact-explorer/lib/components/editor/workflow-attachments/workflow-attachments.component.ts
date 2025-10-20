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
import { MatTooltip } from '@angular/material/tooltip';
import { MatTableModule } from '@angular/material/table';
import { MatCheckboxModule } from '@angular/material/checkbox';

import { WorkflowAttachment } from '../../../types/team-workflow';
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
import { take } from 'rxjs';

@Component({
	selector: 'osee-workflow-attachments',
	imports: [
		CommonModule,
		MatButton,
		MatTooltip,
		MatTableModule,
		MatCheckboxModule,
	],
	templateUrl: './workflow-attachments.component.html',
})
export class WorkflowAttachmentsComponent {
	teamWorkflowId = input.required<`${number}`>();

	// private svc = inject(AttachmentService);
	private svc = inject(AttachmentTestingService);
	private dialog = inject(MatDialog);

	attachments = signal<WorkflowAttachment[]>([]);
	loading = signal<boolean>(false);
	error = signal<string | null>(null);

	isEmpty = computed(
		() => !this.loading() && this.attachments().length === 0
	);

	// Material table columns
	displayedColumns = [
		'select',
		'name',
		'extension',
		'sizeInBytes',
		'actions',
	];

	// Selection state
	private selectedIds = signal<Set<`${number}`>>(new Set<`${number}`>());

	// Derived selection states
	selectedCount = computed(() => this.selectedIds().size);
	allSelected = computed(() => {
		const total = this.attachments().length;
		return total > 0 && this.selectedIds().size === total;
	});
	isIndeterminate = computed(() => {
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
			.pipe(take(1))
			.subscribe({
				next: (list) => {
					this.attachments.set(list ?? []);
					// Clear selection on refresh.
					this.selectedIds.set(new Set());
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

	openUpdateDialog(att: WorkflowAttachment) {
		const data: UpdateAttachmentDialogData = {
			attachment: {
				id: att.id,
				fileName: att.name,
				sizeBytes: att.sizeInBytes,
				contentType: undefined,
			},
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
			.pipe(take(1))
			.subscribe({
				next: (withBytes) => {
					const list = this.attachments().map((a) =>
						a.id === withBytes.id ? withBytes : a
					);
					this.attachments.set(list);

					if (!withBytes.attachmentBytes) return;
					const blob = base64ToBlob(
						withBytes.attachmentBytes,
						withBytes.extension
					);
					const url = URL.createObjectURL(blob);
					window.open(url, '_blank');
					setTimeout(() => URL.revokeObjectURL(url), 3000);
				},
				error: (err) => this.error.set(this.extractError(err)),
			});
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
			.pipe(take(1))
			.subscribe({
				next: (withBytes) => {
					const list = this.attachments().map((a) =>
						a.id === withBytes.id ? withBytes : a
					);
					this.attachments.set(list);

					if (
						!withBytes.attachmentBytes ||
						withBytes.sizeInBytes == 0
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
				},
				error: (err) => this.error.set(this.extractError(err)),
			});
	}

	private uploadFiles(files: File[]) {
		const id = String(this.teamWorkflowId());
		this.loading.set(true);
		this.svc
			.uploadAttachments(id, files)
			.pipe(take(1))
			.subscribe({
				next: (attachments: WorkflowAttachment[]) => {
					this.attachments.set(attachments);
					this.loading.set(false);
				},
				error: (err: unknown) => {
					this.error.set(this.extractError(err));
					this.loading.set(false);
				},
			});
	}

	private updateFile(att: WorkflowAttachment, file: File) {
		const id = String(this.teamWorkflowId());
		this.loading.set(true);
		this.svc
			.updateAttachment(id, att, file)
			.pipe(take(1))
			.subscribe({
				next: (updated) => {
					const list = this.attachments().map((a) =>
						a.id === updated.id ? updated : a
					);
					this.attachments.set(list);
					// Deselect updated item
					this.selectedIds.update((prev) => {
						const next = new Set(prev);
						next.delete(att.id);
						return next;
					});
					this.loading.set(false);
				},
				error: (err) => {
					this.error.set(this.extractError(err));
					this.loading.set(false);
				},
			});
	}

	isSelected(id: `${number}`): boolean {
		return this.selectedIds().has(id);
	}

	toggle(id: `${number}`, checked: boolean): void {
		this.selectedIds.update((prev) => {
			const next = new Set(prev);
			if (checked) {
				next.add(id);
			} else {
				next.delete(id);
			}
			return next;
		});
	}

	masterToggle(checked: boolean): void {
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
			.pipe(take(1))
			.subscribe({
				next: () => {
					this.attachments.set(
						this.attachments().filter((a) => !ids.includes(a.id))
					);
					this.selectedIds.set(new Set());
					this.loading.set(false);
				},
				error: (err) => {
					this.error.set(this.extractError(err));
					this.loading.set(false);
				},
			});
	}

	deleteSingle(attachment: WorkflowAttachment): void {
		if (!confirm(`Delete ${attachment.name}?`)) return;

		this.loading.set(true);

		this.svc
			.deleteAttachments(String(this.teamWorkflowId()), [attachment.id])
			.pipe(take(1))
			.subscribe({
				next: () => {
					this.attachments.set(
						this.attachments().filter((a) => !(attachment.id === a.id))
					);
					this.selectedIds.set(new Set());
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

/**
 * Convert a base64 string to a Blob, attempting to infer a content type
 * from the file extension. Falls back to application/octet-stream.
 */
function base64ToBlob(base64: string, extension?: string): Blob {
	const contentType =
		guessContentTypeFromExtension(extension) ?? 'application/octet-stream';
	const byteChars = atob(base64);
	const byteNumbers = new Array<number>(byteChars.length);
	for (let i = 0; i < byteChars.length; i++) {
		byteNumbers[i] = byteChars.charCodeAt(i);
	}
	const byteArray = new Uint8Array(byteNumbers);
	return new Blob([byteArray], { type: contentType });
}

/**
 * MIME type inference from extension.
 * https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/MIME_types/Common_types
 */
const EXT_TO_MIME: Record<string, string> = {
	// Archives and packages
	'7z': 'application/x-7z-compressed',
	arc: 'application/x-freearc',
	bz: 'application/x-bzip',
	bz2: 'application/x-bzip2',
	gz: 'application/gzip',
	jar: 'application/java-archive',
	rar: 'application/vnd.rar',
	tar: 'application/x-tar',
	zip: 'application/zip',

	// Binary / generic
	bin: 'application/octet-stream',

	// Audio
	aac: 'audio/aac',
	cda: 'application/x-cdf',
	mid: 'audio/midi',
	midi: 'audio/midi',
	mp3: 'audio/mpeg',
	oga: 'audio/ogg',
	opus: 'audio/ogg',
	wav: 'audio/wav',
	weba: 'audio/webm',

	// Video
	avi: 'video/x-msvideo',
	mp4: 'video/mp4',
	mpeg: 'video/mpeg',
	ogv: 'video/ogg',
	ts: 'video/mp2t',
	webm: 'video/webm',
	'3gp': 'video/3gpp',
	'3g2': 'video/3gpp2',

	// Images
	apng: 'image/apng',
	avif: 'image/avif',
	bmp: 'image/bmp',
	gif: 'image/gif',
	ico: 'image/vnd.microsoft.icon',
	jpeg: 'image/jpeg',
	jpg: 'image/jpeg',
	png: 'image/png',
	svg: 'image/svg+xml',
	tif: 'image/tiff',
	tiff: 'image/tiff',
	webp: 'image/webp',

	// Fonts
	eot: 'application/vnd.ms-fontobject',
	otf: 'font/otf',
	ttf: 'font/ttf',
	woff: 'font/woff',
	woff2: 'font/woff2',

	// Text / data formats
	abw: 'application/x-abiword',
	csh: 'application/x-csh',
	css: 'text/css',
	csv: 'text/csv',
	htm: 'text/html',
	html: 'text/html',
	ics: 'text/calendar',
	js: 'text/javascript',
	mjs: 'text/javascript',
	json: 'application/json',
	jsonld: 'application/ld+json',
	md: 'text/markdown',
	php: 'application/x-httpd-php',
	rtf: 'application/rtf',
	sh: 'application/x-sh',
	txt: 'text/plain',
	xhtml: 'application/xhtml+xml',
	xml: 'application/xml',
	xul: 'application/vnd.mozilla.xul+xml',
	webmanifest: 'application/manifest+json',

	// Documents
	pdf: 'application/pdf',

	// Microsoft Office (legacy + OOXML)
	doc: 'application/msword',
	docx: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
	xls: 'application/vnd.ms-excel',
	xlsx: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
	ppt: 'application/vnd.ms-powerpoint',
	pptx: 'application/vnd.openxmlformats-officedocument.presentationml.presentation',
	vsd: 'application/vnd.visio',

	// OpenDocument formats
	odt: 'application/vnd.oasis.opendocument.text',
	ods: 'application/vnd.oasis.opendocument.spreadsheet',
	odp: 'application/vnd.oasis.opendocument.presentation',

	// Books / eBooks
	azw: 'application/vnd.amazon.ebook',
	epub: 'application/epub+zip',

	// Apple installer
	mpkg: 'application/vnd.apple.installer+xml',
};

export function guessContentTypeFromExtension(
	ext?: string
): string | undefined {
	if (!ext) return undefined;
	return EXT_TO_MIME[ext.toLowerCase()] || 'application/octet-stream';
}
