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
import { SUPPORTED_IMAGE_MIME_TYPES } from '@osee/shared/types/constants';

/** Validates whether a File has a supported image MIME type. */
export function isSupportedImageFile(file: File): boolean {
	return SUPPORTED_IMAGE_MIME_TYPES.includes(file.type);
}
