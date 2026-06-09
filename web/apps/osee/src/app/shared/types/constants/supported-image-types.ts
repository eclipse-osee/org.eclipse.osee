/*********************************************************************
 * Copyright (c) 2026 Boeing
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

/**
 * Supported image file extensions (without leading dot).
 * Matches the server-side SUPPORTED_IMAGE_EXTENSIONS in MarkdownHtmlUtil.java.
 */
export const SUPPORTED_IMAGE_EXTENSIONS = [
	'png',
	'jpg',
	'jpeg',
	'gif',
	'bmp',
	'webp',
	'svg',
] as const;

/** MIME types corresponding to the supported image extensions. */
export const SUPPORTED_IMAGE_MIME_TYPES: readonly string[] = [
	'image/png',
	'image/jpeg',
	'image/gif',
	'image/bmp',
	'image/webp',
	'image/svg+xml',
];

/** Comma-separated accept string for file inputs (e.g. ".png,.jpg,..."). */
export const ACCEPTED_IMAGE_INPUT_TYPES = SUPPORTED_IMAGE_EXTENSIONS.map(
	(ext) => `.${ext}`
).join(',');

/** Human-readable label for display in UI messages. */
export const SUPPORTED_IMAGE_FORMATS_LABEL = SUPPORTED_IMAGE_EXTENSIONS.map(
	(ext) => ext.toUpperCase()
).join(', ');
