import { element } from "./element";

export interface AddElementDialog{
    id: string,
    name: string,
    element: element,
    type:{id:string,name:string}
}