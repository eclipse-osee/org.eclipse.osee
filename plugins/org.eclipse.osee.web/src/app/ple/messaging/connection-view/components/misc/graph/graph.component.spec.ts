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
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { MatMenuModule } from '@angular/material/menu';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { NgxGraphModule } from '@swimlane/ngx-graph';
import { graphServiceMock } from '../../../mocks/CurrentGraphService.mock';
import { ConnectionViewRouterService } from '../../../services/connection-view-router.service';
import { CurrentGraphService } from '../../../services/current-graph.service';
import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';


import { GraphComponent } from './graph.component';
import { MatButtonHarness } from '@angular/material/button/testing';
import { MatMenuHarness, MatMenuItemHarness } from '@angular/material/menu/testing';
import { MatFormFieldModule } from '@angular/material/form-field';
import { FormsModule } from '@angular/forms';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { CommonModule } from '@angular/common';
import { ConfirmRemovalDialogComponent } from '../../dialogs/confirm-removal-dialog/confirm-removal-dialog.component';
import { CreateConnectionDialogComponent } from '../../dialogs/create-connection-dialog/create-connection-dialog.component';
import { CreateNewNodeDialogComponent } from '../../dialogs/create-new-node-dialog/create-new-node-dialog.component';
import { EditConnectionDialogComponent } from '../../dialogs/edit-connection-dialog/edit-connection-dialog.component';
import { EditNodeDialogComponent } from '../../dialogs/edit-node-dialog/edit-node-dialog.component';
import { EnumsService } from 'src/app/ple/messaging/shared/services/http/enums.service';
import { enumsServiceMock } from 'src/app/ple/messaging/shared/mocks/EnumsService.mock';

describe('GraphComponent', () => {
  let component: GraphComponent;
  let fixture: ComponentFixture<GraphComponent>;
  let loader: HarnessLoader;
  let router: any;
  let routerService: ConnectionViewRouterService;

  beforeEach(async () => {
    router = jasmine.createSpyObj('Router', ['navigate', 'createUrlTree', 'serializeUrl'],{'url':new String()});
    await TestBed.configureTestingModule({
      imports:[MatDialogModule,NgxGraphModule,NoopAnimationsModule,RouterTestingModule,MatMenuModule,MatFormFieldModule,FormsModule,MatSelectModule,MatInputModule,CommonModule],
      providers:
        [
          { provide: Router, useValue: router },
          { provide: CurrentGraphService, useValue: graphServiceMock },
          {provide: EnumsService,useValue:enumsServiceMock}
      ],
      declarations: [ GraphComponent,ConfirmRemovalDialogComponent,CreateConnectionDialogComponent,CreateNewNodeDialogComponent,EditConnectionDialogComponent,EditNodeDialogComponent ]
    })
      .compileComponents();
    routerService=TestBed.inject(ConnectionViewRouterService)
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GraphComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    loader = TestbedHarnessEnvironment.loader(fixture);
  });
  beforeEach(function () {
    let window1 = spyOn(window, 'open').and.callFake((url,target,replace) => {
      return null;
    })
  });
  beforeEach(() => {
    component.linkMenuTrigger.closeMenu();
    component.nodeMenuTrigger.closeMenu();
  })

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('Core Functionality', () => {
    
    describe('View Functionality', () => {

      it('should navigate to messages page', () => {
        routerService.branchType = 'product line';
        routerService.branchId = '8';
        component.navigateToMessages("hello");
        expect(router.navigate).toHaveBeenCalledWith(['', 'product line', '8', 'hello', 'messages'])
      });
    })
      describe('Editing Menus', () => {
        beforeEach(() => {
          component.editMode = true;
        })
        describe('Link Menu', () => {
          let menu: MatMenuHarness;
          let items: MatMenuItemHarness[];
          beforeEach(() => {
            component.linkMenuTrigger.closeMenu();
            component.openLinkDialog(new MouseEvent("contextmenu", { clientX: 100, clientY: 100 }), { id: '1', source: '10', target: '15', data: { name: '1' } }, [{ id: '10', label: '10' }, { id: '15', label: '15' }]);
            expect(component.linkMenuTrigger.menuOpen).toBeTruthy();
          })
          describe('Editing Link Menu', () => {
            beforeEach(async () => {
              menu = await loader.getHarness(MatMenuHarness);
              expect(menu).toBeDefined();
              items = (await menu.getItems());
              expect(items).toBeDefined();
              expect(items.length).toEqual(4);
            })
            describe('Editing Functionality', () => {
              it('should open edit connection dialog', async () => {
                let spy = spyOn(component, 'openConnectionEditDialog').and.callThrough();
                let item = (await menu?.getItems())[2] || items[2];
                expect(item).toBeDefined();
                expect(await item.getText()).toEqual('Edit 1');
                await item.click();
                expect(spy).toHaveBeenCalled();
              })
  
              it('should open remove connection dialog', async () => {
                let spy = spyOn(component, 'openRemoveConnectionDialog').and.callThrough();
                let item = (await menu?.getItems())[3] || items[3];
                expect(item).toBeDefined();
                expect(await item.getText()).toEqual('Remove connection 1');
                await item.click();
                expect(spy).toHaveBeenCalled();
              })
            })
            describe('Navigation', () => {
              it('should navigate to messages page', async () => {
                let spy = spyOn(component, 'navigateToMessages').and.callThrough();
                let item = (await menu?.getItems())[0] || items[0];
                expect(item).toBeDefined();
                expect(await item.getText()).toEqual('Go to 1');
                await item.click();
                expect(spy).toHaveBeenCalled();
              })
  
              it('should navigate to messages page in new tab', async () => {
                let spy = spyOn(component, 'navigateToMessagesInNewTab').and.callThrough();
                let item = (await menu?.getItems())[1] || items[1];
                expect(item).toBeDefined();
                expect(await item.getText()).toEqual('Go to 1 in new tab');
                await item.click();
                expect(spy).toHaveBeenCalled();
              })
            })
          })
        });
        describe('Node Menu', () => {
          let menu: MatMenuHarness;
          let items: MatMenuItemHarness[];
          beforeEach(() => {
            component.nodeMenuTrigger.closeMenu();
            component.linkMenuTrigger.closeMenu();
            component.openNodeDialog(new MouseEvent("contextmenu", { clientX: 200, clientY: 200 }), {id:'1',data:{name:'first'}},[{id:'a3',source:'1',target:'2',data:{name:'a'}},{id:'a4',source:'2',target:'1',data:{name:'b'}}]);
            expect(component.nodeMenuTrigger.menuOpen).toBeTruthy();
          })
          describe('Editing Node Menu', () => {
            beforeEach(async () => {
              (await loader.getAllHarnesses(MatMenuHarness)).forEach(async(tempMenu) => {
                if ((await tempMenu.getItems()).length > 0) {
                  menu = tempMenu;
                  expect(menu).toBeDefined();
                  items = (await menu.getItems());
                  expect(items).toBeDefined();
                  expect(items.length).toEqual(3);
                }
              })
              //menu = await loader.getHarness(MatMenuHarness);
            })
            describe('Editing Functionality', () => {
              it('should open edit node dialog', async () => {
                (await loader.getAllHarnesses(MatMenuHarness)).forEach(async(tempMenu) => {
                  if ((await tempMenu.getItems()).length > 0) {
                    menu = tempMenu;
                    expect(menu).toBeDefined();
                    items = (await menu.getItems());
                    expect(items).toBeDefined();
                    expect(items.length).toEqual(3);
                    let spy = spyOn(component, 'openEditNodeDialog').and.callThrough();
                    let item = (await menu?.getItems())[0] || items[0];
                    expect(item).toBeDefined();
                    expect(await item.getText()).toEqual('Edit first');
                    await item.click();
                    expect(spy).toHaveBeenCalled();
                  }
                })
              })

              it('should open remove node dialog', async () => {
                (await loader.getAllHarnesses(MatMenuHarness)).forEach(async(tempMenu) => {
                  if ((await tempMenu.getItems()).length > 0) {
                    menu = tempMenu;
                    expect(menu).toBeDefined();
                    items = (await menu.getItems());
                    expect(items).toBeDefined();
                    expect(items.length).toEqual(3);
                    let spy = spyOn(component, 'removeNodeAndConnection').and.callThrough();
                    let item = (await menu?.getItems())[1] || items[1];
                    expect(item).toBeDefined();
                    expect(await item.getText()).toEqual('Remove first & Connection');
                    await item.click();
                    expect(spy).toHaveBeenCalled();
                  }
                })
              })

              it('should open create node dialog', async () => {
                (await loader.getAllHarnesses(MatMenuHarness)).forEach(async(tempMenu) => {
                  if ((await tempMenu.getItems()).length > 0) {
                    menu = tempMenu;
                    expect(menu).toBeDefined();
                    items = (await menu.getItems());
                    expect(items).toBeDefined();
                    expect(items.length).toEqual(3);
                    let spy = spyOn(component, 'createConnectionToNode').and.callThrough();
                    let item = (await menu?.getItems())[2] || items[2];
                    expect(item).toBeDefined();
                    expect(await item.getText()).toEqual('Create Connection To first');
                    await item.click();
                    expect(spy).toHaveBeenCalled();
                  }
                })
              })
            })
          })
        })
        describe('Graph Menu', () => {
          let menu: MatMenuHarness;
          let items: MatMenuItemHarness[];
          beforeEach(() => {
            component.graphMenuTrigger.closeMenu();
            let event = new MouseEvent("contextmenu", { clientX: 300, clientY: 300, },);
            let el = document.createElement('button');
            el.classList.add('panning-rect')
            Object.defineProperty(event, 'target', {value: el, enumerable: true});
            component.openGraphDialog(event);
            expect(component.graphMenuTrigger.menuOpen).toBeTruthy();
          })
          describe('Editing Graph Menu', () => {
            beforeEach(async () => {
              (await loader.getAllHarnesses(MatMenuHarness)).forEach(async(tempMenu) => {
                if ((await tempMenu.getItems()).length > 0) {
                  menu = tempMenu;
                  expect(menu).toBeDefined();
                  items = (await menu.getItems());
                  expect(items).toBeDefined();
                  expect(items.length).toEqual(1);
                }
              })
            })
            it('should open the create node dialog', async () => {
              (await loader.getAllHarnesses(MatMenuHarness)).forEach(async(tempMenu) => {
                if ((await tempMenu.getItems()).length > 0) {
                  menu = tempMenu;
                  expect(menu).toBeDefined();
                  items = (await menu.getItems());
                  expect(items).toBeDefined();
                  expect(items.length).toEqual(1);
                  let spy = spyOn(component, 'createNewNode').and.callThrough();
                  let item = (await menu?.getItems())[0] || items[0];
                  expect(item).toBeDefined();
                  expect(await item.getText()).toEqual('Create New Node');
                  await item.click();
                  expect(spy).toHaveBeenCalled();
                }
              })
            })
          })
        })
      })
    })
    
});
