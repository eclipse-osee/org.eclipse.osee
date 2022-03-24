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
import { BehaviorSubject } from 'rxjs';
import { BranchRoutedUIService } from './branch-routed-ui.service';

export const branchRoutedUiServiceMock: Partial<BranchRoutedUIService> = {
    branchType: '',
    type: new BehaviorSubject(""),
    id: new BehaviorSubject(""),
    branchId: '',
    
    set position (value:{type:string,id:string})  {
        //do nothing
    }
}