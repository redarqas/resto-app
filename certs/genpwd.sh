#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

export PW=`pwgen -Bs 10 1`
echo ${PW} > ${DIR}/password
#export PW=`cat password`