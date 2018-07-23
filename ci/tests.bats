#!/usr/bin/env bats

set -o pipefail

load ${DISPATCH_ROOT}/e2e/tests/helpers.bash

@test "Version" {
    run dispatch version
    echo_to_log
}

@test "Create java base image" {

    run dispatch create base-image java-base ${image_url} --language java
    echo_to_log
    assert_success

    run_with_retry "dispatch get base-image java-base --json | jq -r .status" "READY" 8 5
}

@test "Create java image" {
    run dispatch create image java java-base
    echo_to_log
    assert_success

    run_with_retry "dispatch get image java --json | jq -r .status" "READY" 8 15
}

@test "Create java function no schema" {
    run dispatch create function --image=java java-hello-no-schema ${DISPATCH_ROOT}/examples/java/hello-with-deps --handler=io.dispatchframework.examples.Hello
    echo_to_log
    assert_success

    run_with_retry "dispatch get function java-hello-no-schema --json | jq -r .status" "READY" 20 5
}

@test "Execute java function no schema" {
    run_with_retry "dispatch exec java-hello-no-schema --input='{\"name\": \"Jon\", \"place\": \"Winterfell\"}' --wait --json | jq -r .output" "Hello, Jon from Winterfell" 5 5
}

@test "Create java function with runtime deps" {
    run dispatch create image java-with-deps java-base --runtime-deps ${DISPATCH_ROOT}/examples/java/hello-with-deps/pom.xml
    assert_success
    run_with_retry "dispatch get image java-with-deps --json | jq -r .status" "READY" 20 5

    run dispatch create function --image=java-with-deps java-hello-with-deps ${DISPATCH_ROOT}/examples/java/hello-with-deps --handler=io.dispatchframework.examples.HelloWithDeps
    echo_to_log
    assert_success

    run_with_retry "dispatch get function java-hello-with-deps --json | jq -r .status" "READY" 20 5
}

@test "Execute java with runtime deps" {
    run_with_retry "dispatch exec java-hello-with-deps --wait --json | jq -r .output" "Hello, Someone from timezone UTC" 5 5
}

@test "Create java function with classes" {
    run dispatch create function --image=java java-hello-with-classes ${DISPATCH_ROOT}/examples/java/hello-with-deps --handler=io.dispatchframework.examples.HelloWithClasses
    echo_to_log
    assert_success

    run_with_retry "dispatch get function java-hello-with-classes --json | jq -r .status" "READY" 20 5
}

@test "Execute java with classes" {
    run_with_retry "dispatch exec java-hello-with-classes --input='{\"name\": \"Jon\", \"place\": \"Winterfell\"}' --wait --json | jq -r .output.myField" "Hello, Jon from Winterfell" 5 5
}

@test "Create java function with spring support" {
    run dispatch create image java-spring java-base --runtime-deps ${DISPATCH_ROOT}/examples/java/spring-pom.xml
    assert_success

    run_with_retry "dispatch get image java-spring --json | jq -r .status" "READY" 10 5

    run dispatch create function --image=java-spring spring-fn ${DISPATCH_ROOT}/examples/java/hello-with-deps --handler=io.dispatchframework.examples.HelloSpring
    echo_to_log
    assert_success

    run_with_retry "dispatch get function spring-fn --json | jq -r .status" "READY" 20 5
}

@test "Execute java with spring support" {
    run_with_retry "dispatch exec spring-fn --input='{\"name\":\"Jon\", \"place\":\"Winterfell\"}' --wait --json | jq -r .output.myField" "Hello, Jon from Winterfell" 5 5
}

@test "Cleanup" {
    delete_entities function
    cleanup
}