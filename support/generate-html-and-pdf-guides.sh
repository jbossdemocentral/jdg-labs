#!/bin/bash
PROG_NAME=`basename $0`
SUPPORT_DIR=`dirname $0`

pushd $SUPPORT_DIR/.. > /dev/null
BASE_DIR=$(pwd)

docker run -it -v ${BASE_DIR}/lab-guides/:/documents/ asciidoctor/docker-asciidoctor /bin/sh -c "for guide in \$(ls *.adoc); do asciidoctor -q \$guide; asciidoctor-pdf -q \$guide; done"
