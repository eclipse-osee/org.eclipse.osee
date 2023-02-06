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
import { user } from 'src/app/types/user';
import { action } from '../../../types/configuration-management/action';

//used in action dropdown
export interface TransitionActionDialogData {
	actions: action[];
	selectedUser: user;
}
