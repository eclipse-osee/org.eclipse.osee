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
import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
	name: 'bytes',
	standalone: true,
})
export class BytesPipe implements PipeTransform {
	transform(bytes: number, precision = 2): string {
		if (isNaN(parseFloat(String(bytes))) || !isFinite(bytes)) {
			return '?';
		}

		const units = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];
		const i = Math.floor(Math.log(bytes) / Math.log(1024));
		return `${(bytes / Math.pow(1024, i)).toFixed(precision)} ${units[i]}`;
	}
}
