import { feature } from "./base";

export class baseFeature implements feature {
    constructor(defaultFeature?: defaultBaseFeature) {
        if (defaultFeature) {
            this.valueType = defaultFeature.valueType;
            this.valueStr = defaultFeature.valueStr;
            this.defaultValue = defaultFeature.defaultValue;
            this.productAppStr = defaultFeature.productAppStr;
            this.productApplicabilities = defaultFeature.productApplicabilities;
            this.values = defaultFeature.values;
            this.multiValued = defaultFeature.multiValued;
            this.name = defaultFeature.name;
            this.description = defaultFeature.description;
        }
    }
    public name: string='';
    public description: string='';
    public valueType: string='';
    public valueStr: string='';
    public defaultValue: string='';
    public productAppStr: string='';
    public values: string[]=[];
    public productApplicabilities: string[] = [];
    public multiValued: boolean = false;
    public setValueStr(): void {
        this.valueStr=this.values.toString();
    }
    public setProductAppStr(): void {
        this.productAppStr = this.productApplicabilities.toString();
    } 
}
export class defaultBaseFeature implements feature {
    public name: string='';
    public description: string='';
    public valueType: string='String';
    public valueStr: string='String';
    public defaultValue: string='Included';
    public productAppStr: string='';
    public values: string[]=["Included","Excluded"];
    public productApplicabilities: string[] = [];
    public multiValued: boolean = false;
    public setValueStr(): void {
        this.valueStr=this.values.toString();
    }
    public setProductAppStr(): void {
        this.productAppStr = this.productApplicabilities.toString();
    }  
}