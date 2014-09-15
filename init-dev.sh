#!/bin/bash 
DEMO="JDG Workshop Labs - Dev environment "
AUTHORS="Thomas Qvarnstrom, Red Hat <tqvarnst@redhat.com>"
SRC_DIR=./installs
JBDS=jbdevstudio-product-universal-7.1.1.GA-v20140314-2145-B688.jar
MVN_REPO=./target/local_mvn_repos
REPOS=(jboss-eap-6.3.0-maven-repository.zip jboss-datagrid-6.3.0-maven-repository.zip)
EAP_MVN_REPO=jboss-eap-6.3.0-maven-repository.zip
JDG_SERVER=jboss-datagrid-6.3.0-server.zip
JDG_MVN_REPO=jboss-datagrid-6.3.0-maven-repository.zip


# wipe screen.
clear 

echo

ASCII_WIDTH=52

printf "##  %-${ASCII_WIDTH}s  ##\n" | sed -e 's/ /#/g'
printf "##  %-${ASCII_WIDTH}s  ##\n"   
printf "##  %-${ASCII_WIDTH}s  ##\n" "Setting up the ${DEMO}"
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

# make some checks first before proceeding.	

# Check that maven is installed and on the path

mvn -v -q >/dev/null 2>&1 || { echo >&2 "Maven is required but not installed yet... aborting."; exit 1; }

for DONWLOAD in ${REPOS[@]}
do
	if [[ -r $SRC_DIR/$DONWLOAD || -L $SRC_DIR/$DONWLOAD ]]; then
			echo $DONWLOAD are present...
			echo
	else
			echo You need to download $DONWLOAD from the Customer Support Portal 
			echo and place it in the $SRC_DIR directory to proceed...
			echo
			exit
	fi
done

# Create the target directory if it does not already exist.
if [ ! -x target ]; then
		echo "  - creating the target directory..."
		echo
		mkdir target
else
		echo "  - detected target directory, moving on..."
		echo
fi

# Setting up a local maven repo

# Move the old Maven repo, if it exists, to the OLD position.
if [ -x $MVN_REPO ]; then
		echo "  - existing Maven repository found ..."
		echo
		#rm -rf $MVN_REPO.OLD
		#mv $MVN_REPO $MVN_REPO.OLD
else
	# Unzip the maven repo files
	echo Unpacking local maven repos
	echo
	#REPOS=($EAP_MVN_REPO $JDG_MVN_REPO)
	for REPO in ${REPOS[@]}
	do
		echo "Unpacking $REPO"
		unzip -q -d $MVN_REPO $SRC_DIR/$REPO
	done
fi



# Configure settings.xml
echo "Generating settings.xml from the current location"
echo "  - either copy this to ~/.m2/ or use with mvn -s <path to settingsfile>"
cp example-settings.xml target/settings.xml

pushd $MVN_REPO/jboss-datagrid-6.3.0-maven-repository > /dev/null
jdg_mvn_repo_path=`pwd`
popd > /dev/null

pushd $MVN_REPO/jboss-eap-6.3.0.GA-maven-repository > /dev/null
eap63_mvn_repo_path=`pwd`
popd > /dev/null

sed -i '' "s;file:///path/to/repo/jboss-datagrid-maven-repository;file://${jdg_mvn_repo_path};g" target/settings.xml
sed -i '' "s;file:///path/to/repo/jboss-eap-6.3.0.GA-maven-repository;file://${eap63_mvn_repo_path};g" target/settings.xml

echo "Done setting up environment"
