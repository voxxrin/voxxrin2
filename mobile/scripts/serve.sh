#!/bin/bash

pushd ..
PATH=$(npm bin):$PATH
ionic serve
popd
