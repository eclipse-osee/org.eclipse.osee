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
import { MatTooltip } from '@angular/material/tooltip';

@Component({
	selector: 'osee-workflow-attachments',
	imports: [CommonModule, MatButton, MatTooltip],
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

	deleteAttachment(att: WorkflowAttachment) {
		const id = String(this.teamWorkflowId());
		if (!confirm(`Delete "${att.name}"?`)) return;
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

	openAttachment(att: WorkflowAttachment) {
		const id = String(this.teamWorkflowId());

		// If bytes are already present on the item, open them directly
		if (att.attachmentBytes) {
			const blob = base64ToBlob(att.attachmentBytes, att.extension);
			const url = URL.createObjectURL(blob);
			window.open(url, '_blank');
			URL.revokeObjectURL(url);
			return;
		}

		// Otherwise, use a URL endpoint (if available) or fetch the item with bytes
		this.svc.getDownloadUrl(id, att.id).subscribe({
			next: ({ url }) => window.open(url, '_blank'),
			error: () => {
				// Fallback to fetching the single item with bytes if URL fails
				this.svc.getAttachment(id, att.id).subscribe({
					next: (withBytes) => {
						if (!withBytes.attachmentBytes) return;
						const blob = base64ToBlob(
							withBytes.attachmentBytes,
							withBytes.extension
						);
						const url = URL.createObjectURL(blob);
						window.open(url, '_blank');
						URL.revokeObjectURL(url);
					},
					error: (err2) => this.error.set(this.extractError(err2)),
				});
			},
		});
	}

	downloadAttachment(att: WorkflowAttachment) {
		const id = String(this.teamWorkflowId());

		if (att.attachmentBytes) {
			const blob = base64ToBlob(att.attachmentBytes, att.extension);
			const url = URL.createObjectURL(blob);
			const a = document.createElement('a');
			a.href = url;
			a.download = att.name || 'attachment';
			document.body.appendChild(a);
			a.click();
			a.remove();
			URL.revokeObjectURL(url);
			return;
		}

		// Fallback: fetch single item with bytes
		this.svc.getAttachment(id, att.id).subscribe({
			next: (withBytes) => {
				if (!withBytes.attachmentBytes) return;
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

	private updateFile(att: WorkflowAttachment, file: File) {
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
