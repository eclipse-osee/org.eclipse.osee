/*********************************************************************
 * Copyright (c) 2021 Boeing
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
const transformMatrix: any = {
	name: 'SubMessage Name',
	description: 'SubMessage Description',
	interfaceSubMessageNumber: 'SubMessage Number',
	applicability: 'Applicability',
};
@Pipe({
	name: 'convertSubMessageTitlesToString',
})
export class ConvertSubMessageTitlesToStringPipe implements PipeTransform {
	transform(value: string, ...args: unknown[]): unknown {
		if (
			transformMatrix[value] != null &&
			transformMatrix[value] != undefined
		) {
			return transformMatrix[value];
		}
		return value;
	}
}
