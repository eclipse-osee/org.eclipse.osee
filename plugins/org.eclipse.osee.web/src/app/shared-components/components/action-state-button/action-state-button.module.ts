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
import { NgModule } from '@angular/core';
import { ActionDropDownComponent } from './action-drop-down/action-drop-down.component';
import { CreateActionDialogComponent } from './create-action-dialog/create-action-dialog.component';
import { TransitionActionToReviewDialogComponent } from './transition-action-to-review-dialog/transition-action-to-review-dialog.component';

@NgModule({
	declarations: [],
	imports: [
		ActionDropDownComponent,
		CreateActionDialogComponent,
		TransitionActionToReviewDialogComponent,
	],
	exports: [ActionDropDownComponent],
})
export class ActionStateButtonModule {}
