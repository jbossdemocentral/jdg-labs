#!/bin/bash
DEMO="JDG Workshop Labs"
AUTHORS="Thomas Qvarnstrom, Red Hat <tqvarnst@redhat.com>"
ASCII_WIDTH=52
DEFAULT_INSTALL_DIR=jdg-workshop
INSTALL_DIR=$(mktemp -d XXXXXXXX)/jdg-labs

PROG_NAME=`basename $0`
BASE_DIR=`dirname $0`

echo $WORKDIR

# wipe screen.
clear
echo

printf "##  %-${ASCII_WIDTH}s  ##\n" | sed -e 's/ /#/g'
printf "##  %-${ASCII_WIDTH}s  ##\n"
printf "##  %-${ASCII_WIDTH}s  ##\n" "Setup script for ${DEMO}"
printf "##  %-${ASCII_WIDTH}s  ##\n"
printf "##  %-${ASCII_WIDTH}s  ##\n"
printf "##  %-${ASCII_WIDTH}s  ##\n" "    # ####   ###   ###  ###   ###   ###"
printf "##  %-${ASCII_WIDTH}s  ##\n" "    # #   # #   # #    #      #  # #"
printf "##  %-${ASCII_WIDTH}s  ##\n" "    # ####  #   #  ##   ##    #  # #  ##"
printf "##  %-${ASCII_WIDTH}s  ##\n" "#   # #   # #   #    #    #   #  # #   #"
printf "##  %-${ASCII_WIDTH}s  ##\n" " ###  ####   ###  ###  ###    ###   ###"
printf "##  %-${ASCII_WIDTH}s  ##\n"
printf "##  %-${ASCII_WIDTH}s  ##\n"
printf "##  %-${ASCII_WIDTH}s  ##\n"
printf "##  %-${ASCII_WIDTH}s  ##\n" "brought to you by,"
printf "##  %-${ASCII_WIDTH}s  ##\n" "${AUTHORS}"
printf "##  %-${ASCII_WIDTH}s  ##\n"
printf "##  %-${ASCII_WIDTH}s  ##\n"
printf "##  %-${ASCII_WIDTH}s  ##\n" | sed -e 's/ /#/g'


echo
echo "Setting up the ${DEMO} environment..."
echo

pushd ${BASE_DIR} > /dev/null
BASE_DIR=$(pwd)

mkdir -p ${INSTALL_DIR}
mkdir -p ${INSTALL_DIR}/installs
mkdir -p ${INSTALL_DIR}/lab-guides
mkdir -p ${INSTALL_DIR}/projects
mkdir -p ${INSTALL_DIR}/solutions

pushd ${INSTALL_DIR} > /dev/null
INSTALL_DIR=`pwd`
popd > /dev/null

cp init-dev.sh ${INSTALL_DIR}/
cp init-lab.sh ${INSTALL_DIR}/
cp example-settings.xml ${INSTALL_DIR}/
cp installs/rh-internal-download.sh ${INSTALL_DIR}/installs

if docker -v >/dev/null 2>&1; then
    echo "Docker is installed, will use Asciidoctor docker image to generate pdf"
    #docker run -it -v ${LABS_DIR}/:/documents/ asciidoctor/docker-asciidoctor /bin/sh -c "for guide in \$(ls *.adoc); do asciidoctor -q \$guide; asciidoctor-pdf -q \$guide; done"
    sh ${BASE_DIR}/support/generate-html-and-pdf-guides.sh
    cp ${LABS_DIR}/*.html ${INSTALL_DIR}/lab-guides
    cp ${LABS_DIR}/*.pdf ${INSTALL_DIR}/lab-guides

else
    echo "Docker is not installed, cannot generate HTML and PDF versions of the guides if it's not installed, you will need to do this manually and re-run the release script"
    cp ${LABS_DIR}/*.html ${INSTALL_DIR}/lab-guides
    cp ${LABS_DIR}/*.pdf ${INSTALL_DIR}/lab-guides
fi

cp ${LABS_DIR}/*.html ${INSTALL_DIR}/lab-guides
cp ${LABS_DIR}/*.pdf ${INSTALL_DIR}/lab-guides

LABS=(lab1 lab2 lab3 lab4 lab5 lab6 lab7)

for lab in ${LABS[*]}
do
# Clean and copy the lab start directory
	echo "Copying ${lab}"
	pushd projects/${lab} > /dev/null
	mvn clean > /dev/null
	popd > /dev/null
	cp -R projects/${lab} ${INSTALL_DIR}/projects/

	# Clean and compress the lab solution directory
	echo "Compressing ${lab}-solution"
	pushd projects/${lab}-solution > /dev/null
	mvn clean > /dev/null
	zip -r ${INSTALL_DIR}/solutions/${lab}-solution.zip .
	popd > /dev/null
done

cd ..

echo "Compressing the release"
pushd ${INSTALL_DIR}
cd ..
zip -r ../jdg-labs.zip jdg-labs
TMPDIR=`pwd`

popd > /dev/null
popd > /dev/null

rm -rf $TMPDIR
