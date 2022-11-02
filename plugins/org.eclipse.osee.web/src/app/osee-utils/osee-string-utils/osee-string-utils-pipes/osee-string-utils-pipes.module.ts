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
import { NgModule } from '@angular/core';
import { DisplayTruncatedStringWithFieldOverflowPipe } from './display-truncated-string-with-field-overflow.pipe';

@NgModule({
	declarations: [DisplayTruncatedStringWithFieldOverflowPipe],
	imports: [],
	exports: [DisplayTruncatedStringWithFieldOverflowPipe],
})
export class OseeStringUtilsPipesModule {}
