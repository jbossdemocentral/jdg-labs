#!/bin/bash 
DEMO="JDG Workshop Labs"
AUTHORS="Thomas Qvarnstrom, Red Hat <tqvarnst@redhat.com>"
ASCII_WIDTH=52
DEFAULT_INSTALL_DIR=jdg-workshop
INSTALL_DIR=$(mktemp -d XXXXXXXX)/jdg-workshop

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

mkdir -p ${INSTALL_DIR}
mkdir -p ${INSTALL_DIR}/installs
mkdir -p ${INSTALL_DIR}/lab-guides
mkdir -p ${INSTALL_DIR}/projects

cp init-dev.sh ${INSTALL_DIR}/
cp init-lab.sh ${INSTALL_DIR}/
cp example-settings.xml ${INSTALL_DIR}/
cp installs/rh-internal-download.sh ${INSTALL_DIR}/installs

curl -o ${INSTALL_DIR}/lab-guides/lab-setup-guide.pdf https://gitprint.com/rhnordics/jdg-labs/blob/master/lab-guides/lab-setup-guide.md?download

LABS=(lab1 lab2 lab3 lab4 lab5 lab6)

for lab in ${LABS[*]}
do
	pushd projects/${lab} > /dev/null
	mvn clean > /dev/null 
	popd > /dev/null
	cp -R projects/${lab} ${INSTALL_DIR}/projects/
	
	curl -o ${INSTALL_DIR}/lab-guides/${lab}-guide.pdf https://gitprint.com/rhnordics/jdg-labs/blob/master/lab-guides/${lab}-guide.md?download
	
done

popd > /dev/null

	

