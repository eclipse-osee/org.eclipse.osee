/*********************************************************************
 * Copyright (c) 2024 Boeing
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
	name: 'objectValues',
	standalone: true,
})
export class ObjectValuesPipe implements PipeTransform {
	transform<T>(value: { [key: string]: T }): T[] {
		if (value === undefined || value === null) {
			return [];
		}
		return Object.values(value);
	}
}
