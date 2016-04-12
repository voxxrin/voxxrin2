#!/bin/bash

pushd ..
PATH=$(npm bin):$PATH
phonegap serve
popd
