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
	sub_name: 'SubMessage Name',
	sub_description: 'SubMessage Description',
	sub_number: 'SubMessage Number',
	sub_txRate: 'SubMessage Tx Rate',
	name: 'Message Name',
	description: 'Message Description',
	interfaceMessageNumber: 'Message Number',
	interfaceMessagePeriodicity: 'Periodicity',
	interfaceMessageRate: 'Tx Rate',
	interfaceMessageWriteAccess: 'Read/Write',
	interfaceMessageType: 'Type',
	applicability: 'Applicability',
};
@Pipe({
	name: 'convertMessageTableTitlesToString',
	standalone: true,
})
export class ConvertMessageTableTitlesToStringPipe implements PipeTransform {
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
