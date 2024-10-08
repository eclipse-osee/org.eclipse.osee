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
import {
	attribute,
	isInvalidAttr,
	newAttribute,
	validAttribute,
} from '@osee/attributes/types';
import { ATTRIBUTETYPEID } from '@osee/attributes/constants';

@Pipe({
	name: 'validAttribute',
	standalone: true,
})
export class ValidAttributePipe<T, U extends ATTRIBUTETYPEID>
	implements PipeTransform
{
	transform(
		value: attribute<T, U>,
		..._args: unknown[]
	): validAttribute<T, U> | newAttribute<T, U> | undefined {
		if (!isInvalidAttr(value)) {
			return value;
		} else {
			return;
		}
	}
}
