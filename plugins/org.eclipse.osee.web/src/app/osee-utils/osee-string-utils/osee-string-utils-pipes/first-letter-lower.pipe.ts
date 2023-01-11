/*********************************************************************
 * Copyright (c) 2023 Boeing
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
	name: 'oseeFirstLetterLower',
	standalone: true,
})
export class FirstLetterLowerPipe implements PipeTransform {
	constructor() {}
	transform(value: string): string;
	transform(value: null | undefined): null;
	transform(value: string | null | undefined): string | null;
	transform(value: string | null | undefined): string | null {
		if (value == null) return null;
		if (typeof value !== 'string') {
			throw new Error('Type of value is not a string');
		}
		return value.charAt(0).toLowerCase() + value.slice(1);
	}
}
