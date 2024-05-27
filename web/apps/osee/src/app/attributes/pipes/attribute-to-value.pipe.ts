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
import { attribute } from '@osee/attributes/types';
import { ATTRIBUTETYPEID } from '@osee/attributes/constants';

@Pipe({
	name: 'attributeToValue',
	standalone: true,
})
export class AttributeToValuePipe implements PipeTransform {
	transform<T>(
		value: attribute<T, ATTRIBUTETYPEID>,
		..._args: unknown[]
	): T | undefined {
		if (value.id !== '') {
			return value.value;
		}
		return undefined;
	}
}
