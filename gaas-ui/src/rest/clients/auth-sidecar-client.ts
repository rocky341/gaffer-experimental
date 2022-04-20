import axios, { AxiosError, AxiosRequestConfig, AxiosResponse } from "axios";
import { Config } from "../config";
import { RestClient } from "./rest-client";

export interface IWhatAuthInfo {
    requiredFields: Array<string>;
    requiredHeaders: object;
    attributes: object;
}

export class AuthSidecarClient {
    private static whatAuthObject: IWhatAuthInfo = {
        requiredFields: [],
        requiredHeaders: {},
        attributes: {},
    };
    private static token: string;
    private whoami: string;
    public constructor() {
        this.whoami = "";
    }
    private static setToken(token: string) {
        this.token = token;
    }
    public static getRequiredHeaders(): object {
        return this.whatAuthObject.requiredHeaders;
    }
    public static getRequiredFields(): Array<string> {
        return this.whatAuthObject.requiredFields;
    }
    public static getAttributes(): object {
        return this.whatAuthObject.attributes;
    }
    public static getToken(): string {
        return this.token;
    }
    public static resetAuthSidecarClient() {
        AuthSidecarClient.setToken("");
    }
    public async getWhatAuth(): Promise<IWhatAuthInfo> {
        var response: AxiosResponse<any>;
        try {
            response = await axios({
                baseURL: Config.REACT_APP_AUTH_ENDPOINT,
                url: "/what-auth",
                method: "GET",
            });
        } catch (error) {
            throw RestClient.fromError(error as AxiosError<any>);
        }
        try {
            this.validateWhatAuthObject(response.data);
            AuthSidecarClient.whatAuthObject = response.data;
            return AuthSidecarClient.whatAuthObject;
        } catch (error) {
            throw error;
        }
    }
    private validateWhatAuthObject(data: object) {
        const requiredAttributes: string = ["attributes", "requiredFields", "requiredHeaders"].sort().toString();
        const objectKeys: string = Object.keys(data).sort().toString();
        if (objectKeys === requiredAttributes) {
            for (const [key, value] of Object.entries(data)) {
                if (
                    (key === "attributes" && typeof value !== "object") ||
                    (key === "requiredFields" && !Array.isArray(value)) ||
                    (key === "requiredHeaders" && typeof value !== "object")
                ) {
                    throw new Error("Invalid Response").message;
                }
            }
        } else {
            throw new Error("Invalid Response").message;
        }
    }
    private convertMapToJson(map: Map<String, String>): object {
        return Object.fromEntries(map);
    }
    public async getWhoAmI() {
        const config: AxiosRequestConfig = {};
        config.baseURL = Config.REACT_APP_AUTH_ENDPOINT;
        config.url = "/whoami";
        config.method = "GET";

        console.log(AuthSidecarClient.getToken());
        if (AuthSidecarClient.getToken().length > 0) {
            console.log("here" + AuthSidecarClient.getToken());
            config.headers = {
                Authorization: "test" + AuthSidecarClient.token,
            };
        }

        try {
            const response: AxiosResponse<any> = await axios(config);
            this.whoami = response.data;
            return response.data;
        } catch (e) {
            const error = e as AxiosError<any>;
            throw new Error(error.message);
        }
    }
    public async postAuth(data: Map<string, string>) {
        try {
            const response: AxiosResponse<any> = await axios({
                baseURL: Config.REACT_APP_AUTH_ENDPOINT,
                url: "/auth",
                method: "POST",
                data: this.convertMapToJson(data),
            });
            AuthSidecarClient.setToken(response.data);
            console.log(AuthSidecarClient.getToken());
        } catch (e) {
            console.error(e);
            throw e;
        }
    }
    private static getAuthHeader = () => {
        const headers = AuthSidecarClient.getRequiredHeaders();
        Object.entries(headers).forEach(([key, value]) => {
            if (key === "Authorization") {
                return value;
            }
        });
        return "";
    };
}
