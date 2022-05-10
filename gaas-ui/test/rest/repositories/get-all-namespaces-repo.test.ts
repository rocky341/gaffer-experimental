import axios from "axios";
import MockAdapter from "axios-mock-adapter";
import { GetAllNamespacesRepo } from "../../../src/rest/repositories/get-all-namespaces-repo";
import { APIError } from "../../../src/rest/APIError";
import { IAllNameSpacesResponse } from "../../../src/rest/http-message-interfaces/response-interfaces";

const mock = new MockAdapter(axios);
const repo = new GetAllNamespacesRepo();

afterEach(() => mock.resetHandlers());

describe("Get All Namespaces Repo", () => {
    it("should return many namespaces when api returns many", async () => {
        const apiResponse: IAllNameSpacesResponse = ["namespace1", "namespace2", "namespace3"];

        mock.onGet("/namespaces").reply(200, apiResponse);

        const actual: Array<string> = await repo.getAll();

        const expected = ["namespace1", "namespace2", "namespace3"];
        expect(actual).toEqual(expected);
    });

    it("should return one namespace when the api returns one", async () => {
        const apiResponse: IAllNameSpacesResponse = ["namespace1"];

        mock.onGet("/namespaces").reply(200, apiResponse);

        const actual: Array<string> = await repo.getAll();

        const expected = ["namespace1"];
        expect(actual).toEqual(expected);
    });
    it("should throw APIError with correct status message when no response body", async () => {
        mock.onGet("/namespaces").reply(404);

        await expect(repo.getAll()).rejects.toEqual(new APIError("Error Code 404", "Not Found"));
    });
    it("should throw APIError with title and detail from response body", async () => {
        mock.onGet("/namespaces").reply(404, { title: "Forbidden", detail: "Kubernetes access denied" });

        await expect(repo.getAll()).rejects.toEqual(new APIError("Forbidden", "Kubernetes access denied"));
    });

    it("should throw unknown APIError when undefined status and body", async () => {
        mock.onGet("/namespaces").reply(0);

        await expect(repo.getAll()).rejects.toEqual(new APIError("Unknown Error", "Unable to make request"));
    });
});
