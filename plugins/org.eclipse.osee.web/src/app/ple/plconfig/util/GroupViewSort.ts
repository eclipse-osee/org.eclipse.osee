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
import { view } from "../types/pl-config-applicui-branch-mapping";
import { configurationGroup } from "../types/pl-config-configurations";
interface groupInterface{
    id:string,
    name: string,
    count:number,
    views:(view|configurationGroup)[]
}
class group implements groupInterface{
    constructor(name?: string,views?:view[],view?:view, id?:string) {
        this.name = name || "No Group";
        this.views = views || [];
        if (views) {
            this.count = views.length;
        }
        if (view) {
            this.views.push(view);
            this.count++;
        }
        this.id = id || "";
    }
    name: string = "No Group";
    count: number = 0;
    views: (view | configurationGroup)[] = [];
    id = "";
    addView(view: view) {
        this.views.push(view);
        if (view.hasFeatureApplicabilities) {
            this.count++;   
        }
    }
    findView(view: string) {
        return this.views.find(val => val.name === view);
    }
    getGroup() {
        let returnObj = {
            name: this.name,
            id:this.name,
            views: this.views.filter((val)=>val.name !==this.name)
        }
        return returnObj;
    }
}
interface sortedViewsInterface {
    groups: groupInterface[],
    viewCount: number,
    groupCount:number,
}
class sortedViews implements sortedViewsInterface{
    groups = [new group()];
    viewCount = 0;
    groupCount = 0;
    syncGroupCount() {
        this.groupCount = 0;
        this.groups.forEach((value) => {
            if (value.name !== "No Group") {
                this.groupCount += value.count;
            }
        })
    }
    getCounts() {
        let countArray: number[] = [];
        this.groups.forEach((value) => {
            if (value.name !== "No Group") {
                countArray.push(value.count);
            } else {
                countArray.push(value.count);
            }
        })
        return countArray;
    }
    getGroupHeaders() {
        let groupArray: string[] = [];
        this.groups.forEach((val) => {
            if (val.views.length > 1 ||val.name==='No Group') {
                groupArray.push(val.name);
            }
        })
        return groupArray;
    }
    findView(view: string) {
        let returnValue: view = {
            hasFeatureApplicabilities:false,
            id: '-1',
            name:'Not Found'
        };
        this.groups.forEach((val) => {
            if (val.findView(view)) {
                returnValue=val.findView(view)||{
                    hasFeatureApplicabilities:false,
                    id: '-1',
                    name:'Not Found'
                }
            }
        })
        return returnValue;
    }
    findGroup(groupString: string) {
        return this.groups.find((val) => val.name === groupString)?.getGroup() || {
            name: '',
            id:'',
            views:[]
        }
    }
}

export class GroupViewSorter {
    groupList: configurationGroup[] = [];
    viewList: view[] = [];
    viewObj: sortedViews = new sortedViews();
    constructor(groupDetailList?:configurationGroup[], views?:view[]) {
        this.groupList = groupDetailList || [];
        this.viewList = views || [];
    }
    reset() {
        this.viewObj = new sortedViews();
    }
    syncGroups(groups: configurationGroup[]) {
        this.groupList = groups;
    }
    syncViews(views: view[]) {
        this.viewList = views;
    }
    addGroup(group: configurationGroup) {
        let tempGroup = this.groupList.find(val => val.id === group.id && val.name === group.name);
        if (tempGroup) {
            tempGroup = group;
        } else {
            this.groupList.push(group);   
        }
    }
    addView(view: view) {
        let tempView = this.viewList.find(val => val.id === view.id && val.name === view.name);
        if (tempView) {
            tempView = view;
        } else {
            this.viewList.push(view);   
        }
    }
    sort() {
        this.resetDefaultGrouping();
        this.viewList.forEach((element) => {
            if (!this.getGroupListForView(element)) {
                this.addViewToDefaultGrouping(element);
            } else {
                this.addViewToGrouping(element,this.getGroupListForView(element));
            }
        })
        this.groupList.forEach((value) => {
            this.addViewToGrouping(value, value.name);
        })
    }
    getGroupListForView(view: view) {
        let returnValue: string = "";
        this.groupList.forEach((element) => {
            element.configurations.forEach((value) => {
                if (element.name === view.name || value === view.id) {
                    returnValue = element.name;
                }
            })
        })
        return returnValue;
    }
    resetDefaultGrouping() {
        let defaultView = this.viewObj.groups.find(val => val.name === "No Group");
        if (defaultView?.views) {
            defaultView.views = [];
            defaultView.count = 0;
        }
        this.viewObj.viewCount = 0;
    }
    addViewToDefaultGrouping(view:view) {
        let defaultGroup = this.viewObj.groups.find(val => val.name === "No Group");
        if (defaultGroup?.views) {
            defaultGroup.addView(view);
        }
        if (view.hasFeatureApplicabilities) {
            this.viewObj.viewCount++;   
        }
        this.viewObj.syncGroupCount();
    }
    addViewToGrouping(view: view, groupName: string): void {
        let grouping = this.viewObj.groups.find(val => val.name === groupName);
        if (grouping) {
            let existingGrouping = grouping.views.find(el => el.id === view.id);
            if (existingGrouping) {
                existingGrouping.id = view.id;
                existingGrouping.name = view.name;
            } else {
                grouping.addView(view);   
            }
        } else {
            this.viewObj.groups.push(new group(groupName, undefined, this.getGroupFromName(groupName),this.getGroupFromName(groupName)?.id));
            let newGroup = this.viewObj.groups.find(val => val.name === groupName)
            if (newGroup && !newGroup.views.find(val=>val.name===view.name)) {
                newGroup.addView(view);
            }
        }
        this.viewObj.syncGroupCount();
    }
    getSortedArrayOfConfigurations() {
        let sortedArr:string[] = [];
        this.viewObj.groups.forEach((value) => {
            if (value.count > 1 && value.name !=='No Group') {
                sortedArr.push(value.name);
            }
            sortedArr.push(...value.views.map(a=>a.hasFeatureApplicabilities?a.name:''))
        })
        let filteredArr: string[] = sortedArr.filter((el) => {
            return el != '';
        })
        return filteredArr;
    }
    getGroupFromName(name: string) {
        return this.groupList.find(val => val.name === name);
    }
}