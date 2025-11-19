/*********************************************************************
 * Copyright (c) 2022 Boeing
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
export const enum FileExtensions {
	XML = 'xml',
	ZIP = 'zip',
	CSV = 'csv',
	JSON = 'json',
}

export const enum ProducesMediaType {
	JSON = 'application/json',
	XML = 'application/xml',
	ZIP = 'application/zip',
}

// (mebibytes) by (bytes per kibibyte) by (kibibytes per mebibyte)
export const MAX_FILE_SIZE_BYTES = 50 * 1024 * 1024;
