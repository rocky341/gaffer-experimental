import { RestClient } from "../clients/rest-client";
import { ICreateGraphRequestBody } from "../http-message-interfaces/request-interfaces";
import { Config } from "../config";

export class CreateStoreTypesGraphRepo {
    public async create(
        graphId: string,
        description: string,
        configName: string,
        config: ICreateGraphConfig
    ): Promise<void> {
        if (config.schema === undefined) {
            throw new Error("Schema is undefined");
        }
        const httpRequestBody: ICreateGraphRequestBody = {
            graphId: graphId,
            description: description,
            configName: configName,
            schema: config.schema,
        };
        await new RestClient()
            .baseUrl(Config.REACT_APP_KAI_REST_API_HOST)
            .post()
            .requestBody(httpRequestBody)
            .graphs()
            .execute();
    }
}
export interface ICreateGraphConfig {
    schema?: {
        entities: object;
        edges: object;
        types: object;
    };
    proxyContextRoot?: string;
    proxyHost?: string;
    proxySubGraphs?: Array<{ graphId: string; host: string; root: string }>;
}
