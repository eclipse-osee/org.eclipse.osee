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
	name: 'splitApplicability',
	standalone: true,
})
export class SplitApplicabilityPipe implements PipeTransform {
	//eslint-disable-next-line @typescript-eslint/no-unused-vars
	transform(value: string, ...args: unknown[]): string {
		return value.split(new RegExp('s?=s?'))[1];
	}
}
