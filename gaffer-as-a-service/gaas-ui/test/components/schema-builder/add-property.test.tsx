/*
 * Copyright 2021-2022 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *
 */

import React from "react";
import { mount, ReactWrapper } from "enzyme";
import AddProperty from "../../../src/components/schema-builder/add-property";

let wrapper: ReactWrapper;
const onAddPropertyMockCallBack = jest.fn();
beforeEach(() => {
    wrapper = mount(<AddProperty onAddProperty={onAddPropertyMockCallBack} />);
});
afterEach(() => {
    wrapper.unmount();
});

describe("Add Property UI Component", () => {
    describe("Add Property inputs", () => {
        it("should have a property key input field", () => {
            const propertyKeyField = wrapper.find("input#property-key-input");

            expect(propertyKeyField.props().name).toBe("Property Key");
        });

        it("should have a property value input field", () => {
            const propertyValueField = wrapper.find("input#property-value-input");
            expect(propertyValueField.props().name).toBe("Property Value");
        });
    });

    describe("On Add Property", () => {
        it("should callback with a type object when a new type has been added", () => {
            const expectedResult: object = {
                Key: "value",
            };

            addPropertyKey("Key");
            addPropertyValue("value");
            clickAddProperty();

            expect(onAddPropertyMockCallBack).toHaveBeenLastCalledWith(expectedResult);
        });
    });
    describe("Add Property Button", () => {
        it("should have an add property button", () => {
            const addPropertyButton = wrapper.find("button#add-property-button");

            expect(addPropertyButton.text()).toBe("Add Property");
        });
        it("should be disabled when key and value empty", () => {
            const addPropertyButton = wrapper.find("button#add-property-button");

            expect(addPropertyButton.props().disabled).toBe(true);
        });
    });
});

function addPropertyKey(key: string) {
    const propertyKeyInputField = wrapper.find("input#property-key-input");
    propertyKeyInputField.simulate("change", {
        target: { value: key },
    });
}
function addPropertyValue(value: string) {
    const propertyKeyInputField = wrapper.find("input#property-value-input");
    propertyKeyInputField.simulate("change", {
        target: { value: value },
    });
}

function clickAddProperty() {
    const addPropertyButton = wrapper.find("button#add-property-button");
    addPropertyButton.simulate("click");
}
