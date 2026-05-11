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

import { from, Observable } from 'rxjs';

/**
 * Convert a base64 string to a Blob, attempting to infer a content type
 * from the file extension. Falls back to application/octet-stream.
 */
export function base64ToBlob(base64: string, extension?: string): Blob {
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

export function getFileNameWithoutExtension(fileName: string): string {
	return fileName.split('.').slice(0, -1).join('.');
}

export function getFileExtension(fileName: string): string {
	return fileName.split('.').pop() || '';
}

export function readFileAsBase64(
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
