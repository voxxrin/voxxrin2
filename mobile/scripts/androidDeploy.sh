#!/bin/bash

pushd ..
PATH=$(npm bin):$PATH
ionic run android
popd
