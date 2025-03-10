export interface IGraphByIdResponse {
    graphId: string;
    description: string;
    url: string;
    restUrl: string;
    status: "UP" | "DOWN";
    configName: string;
}

export interface IAllGraphsResponse {
    graphs: IGraphByIdResponse[];
}

export interface IAllNameSpacesResponse extends Array<string> {}

export interface IGraphStatusResponse {
    status: string;
}

export interface IWhoAmIResponse {
    "x-email": string;
}

export interface IGetAllGraphIdsResponse extends Array<string> {}

export interface IStoreTypesResponse {
    storeTypes: Array<{
        name: string;
        parameters: string[];
    }>;
}
