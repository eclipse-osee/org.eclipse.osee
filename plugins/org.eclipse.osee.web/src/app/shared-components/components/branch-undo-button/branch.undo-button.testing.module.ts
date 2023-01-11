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
/**
 *
 *
 * DO NOT IMPORT OUTSIDE OF TESTS
 *
 *
 */
import { NgModule } from '@angular/core';
import { UndoButtonBranchMockComponent } from './branch-undo-button.component.mock';

@NgModule({
	declarations: [],
	imports: [UndoButtonBranchMockComponent],
	exports: [UndoButtonBranchMockComponent],
})
export class BranchUndoButtonTestingModule {}
