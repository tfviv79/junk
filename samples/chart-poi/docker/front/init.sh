#!/bin/bash

MODE=${1:-sleep}

if [ "${MODE}" = "sleep" ] ; then
    sleep infinity
    exit 1
fi

ON_SERVE=0
if [ -e on_loop.lock ] ; then
    rm on_loop.lock
fi

if [ "${MODE}" = "loop" ] ; then
    ON_SERVE=1
    touch on_loop.lock
fi
if [ "${MODE}" = "serv" ] ; then
    ON_SERVE=1
fi

if [ $ON_SERVE -eq 1 ] ; then
    while : ; do
        npm run start
        if [ ! -e on_loop.lock ] ; then
            exit 1;
        fi
    done
    exit 1;
fi

sleep infinity
