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
import {
	HttpMethods,
	FileExtensions,
	ProducesMediaType,
} from '@osee/shared/types';

export interface MimReport {
	id: string;
	name: string;
	url: string;
	httpMethod: HttpMethods;
	fileExtension: FileExtensions;
	fileNamePrefix: string;
	producesMediaType: ProducesMediaType;
	diffAvailable: boolean;
}
