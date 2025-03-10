import { GraphType } from "./graph-type";

export class Graph {
    private readonly graphId: string;
    private readonly description: string;
    private readonly url: string;
    private readonly restUrl: string;
    private readonly status: "UP" | "DOWN";
    private readonly configName: string;
    private readonly type: GraphType;

    constructor(
        graphId: string,
        description: string,
        url: string,
        restUrl: string,
        status: "UP" | "DOWN",
        configName: string,
        type: GraphType
    ) {
        this.graphId = graphId;
        this.description = description;
        this.url = url;
        this.restUrl = restUrl;
        this.status = status;
        this.configName = configName;
        this.type = type;
    }

    public getId(): string {
        return this.graphId;
    }

    public getDescription(): string {
        return this.description;
    }

    public getUrl(): string {
        return this.url;
    }

    public getStatus(): string {
        return this.status;
    }

    public getConfigName(): string {
        return this.configName;
    }

    public getType(): GraphType {
        return this.type;
    }

    public getRestUrl(): string {
        return this.restUrl;
    }
    public getGraphHost(): string {
        return this.restUrl.substring(this.restUrl.indexOf("/") + 2).split("/rest")[0];
    }
}
