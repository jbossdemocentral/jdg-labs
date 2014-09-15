#!/bin/bash 
DEMO="JDG Workshop Labs - Server environment"
AUTHORS="Thomas Qvarnstrom, Red Hat, @tqvarnst"
JBOSS_HOME=./target/jboss-eap-6.3
JDG_HOME=./target/jboss-datagrid-6.3.0-server
JDG_ONE_HOME=./target/jboss-datagrid-6.3.0-server-one
JDG_TWO_HOME=./target/jboss-datagrid-6.3.0-server-two
SERVER_DIR=$JBOSS_HOME/standalone/deployments/
SERVER_CONF=$JBOSS_HOME/standalone/configuration/
SRC_DIR=./installs
EAP_SERVER=jboss-eap-6.3.0.zip
EAP_SERVER_MD5SUM=
JDG_SERVER=jboss-datagrid-6.3.0-server.zip
JDG_LIBRARY_MODUELS=jboss-datagrid-6.3.0-eap-modules-library.zip
HOTROD_MODULES=jboss-datagrid-6.3.0-eap-modules-hotrod-java-client.zip


function print_header() {
	# wipe screen.
	clear 
	echo

	ASCII_WIDTH=56

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
	printf "##  %-${ASCII_WIDTH}s  ##\n" "  ${AUTHORS}"
	printf "##  %-${ASCII_WIDTH}s  ##\n"
	printf "##  %-${ASCII_WIDTH}s  ##\n"
	printf "##  %-${ASCII_WIDTH}s  ##\n" | sed -e 's/ /#/g'
}


function print_usage() {
	echo "This a init script for setting up $DEMO"
	echo "usage: $0 [--lab[=]<value>]" >&2	
	echo "    --lab=<value> is the lab number to initialize for"
}

function setup_eap_with_modules() {
	# make some checks first before proceeding.	
	DOWNLOADS=($EAP_SERVER $JDG_LIBRARY_MODUELS $HOTROD_MODULES)
	
	
	for DONWLOAD in ${DOWNLOADS[@]}
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

	# Create the JBOSS_HOME directory if it does not already exist.
	if [ ! -x ${JBOSS_HOME} ]; then
			echo "  - creating the target directory..."
			echo
			mkdir -p ${JBOSS_HOME}
	else
			echo "  - detected target directory, moving on..."
			echo
	fi

	pushd ${JBOSS_HOME}/.. >/dev/null
	EXTRACT_DIR=`pwd`
	popd >/dev/null

	echo Unpacking new JBoss Enterprise EAP 6...
	echo
		
	unzip -q -d ${EXTRACT_DIR} $SRC_DIR/$EAP_SERVER

	# Creating and admin user with admin-123 as password
	echo "Adding admin user"
	$JBOSS_HOME/bin/add-user.sh -g admin -u admin -p admin-123 -s


	# Adding JBoss Data Grid Library modules to EAP
	echo "Adding JBoss Data Grid Modules to EAP"
	tmpdir=`mktemp -d XXXXXXXX`
	unzip -q -d ${tmpdir} ${SRC_DIR}/${JDG_LIBRARY_MODUELS}
	cp -R ${tmpdir}/jboss-datagrid-6.3.0-eap-modules-library/modules/* $JBOSS_HOME/modules/
	rm -rf  ${tmpdir} 

	# Adding Hotrod modules to EAP
	#echo "Adding Hotrod Modules to EAP"
	#tmpdir=`mktemp -d XXXXXXXX`
	#unzip -q -d ${tmpdir} ${SRC_DIR}/${HOTROD_MODULES}
	#cp -R ${tmpdir}/jboss-datagrid-6.3.0-eap-modules-hotrod-java-client/modules/* $JBOSS_HOME/modules/
	#rm -rf  ${tmpdir}  
	
	echo "Done setting up EAP with modules"	
}

function setup_eap_node_with_modules() {
	NODE_NAME=$1

	ORG_JBOSS_HOME=$JBOSS_HOME
	
	JBOSS_HOME=./target/${NODE_NAME}/jboss-eap-6.3
	
	setup_eap_with_modules
	
	# Reset JBOSS_HOME to it's original value
	JBOSS_HOME=$ORG_JBOSS_HOME
		
}


function setup_jdg_node_one() {
	# make some checks first before proceeding.	
	DOWNLOADS=($JDG_SERVER)

	for DONWLOAD in ${DOWNLOADS[@]}
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

	if [ -x ${JDG_ONE_HOME} ]; then
		echo "  - existing JBoss Data Grid detected..."
		echo
		echo "  - moving existing JBoss Data Grid aside..."
		echo
		rm -rf ${JDG_ONE_HOME}.OLD
		mv ${JDG_ONE_HOME} ${JDG_ONE_HOME}.OLD
	fi
	# Unzip the JBoss DG instance.
	echo Unpacking new JBoss Data Grid instance...
	
	tmpdir=`mktemp -d XXXXXXXX`
	
	echo
	unzip -q -d $tmpdir ${SRC_DIR}/${JDG_SERVER}
	
	mv ${tmpdir}/jboss-datagrid-6.3.0-server ${JDG_ONE_HOME}
	
	rm -rf  ${tmpdir}  
	
	echo "Done setting up JDG Server Node One"
}

function setup_jdg_node_two() {
	DOWNLOADS=($JDG_SERVER)

	for DONWLOAD in ${DOWNLOADS[@]}
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

	if [ -x ${JDG_TWO_HOME} ]; then
		echo "  - existing JBoss Data Grid detected..."
		echo
		echo "  - moving existing JBoss Data Grid aside..."
		echo
		rm -rf ${JDG_TWO_HOME}.OLD
		mv ${JDG_TWO_HOME} ${JDG_TWO_HOME}.OLD
	fi
	# Unzip the JBoss DG instance.
	echo Unpacking new JBoss Data Grid instance...
	
	tmpdir=`mktemp -d XXXXXXXX`
	
	echo
	unzip -q -d $tmpdir ${SRC_DIR}/${JDG_SERVER}
	
	mv ${tmpdir}/jboss-datagrid-6.3.0-server ${JDG_TWO_HOME}
	
	rm -rf  ${tmpdir}  
	
	echo "Done setting up JDG Server Node Two"
}

#### Start the script

print_header

if [ $# -eq 0 ]; then
	echo "$0 requires an argument"
	print_usage
	exit 2
fi

LAB_TO_SETUP=0

while getopts ":h-:" opt; do
  	case "${opt}" in
        -)
            case "${OPTARG}" in
                lab)
                    val="${!OPTIND}"; OPTIND=$(( $OPTIND + 1 ))
                    #echo "Parsing option: '--${OPTARG}', value: '${val}'"
                    LAB_TO_SETUP=${val}
                    ;;
                lab=*)
                    val=${OPTARG#*=}
                    opt=${OPTARG%=$val}
                    #echo "Parsing option: '--${opt}', value: '${val}'" >&2
                    LAB_TO_SETUP=${val}
                    ;;
            	*)
                    if [ "$OPTERR" = 1 ] && [ "${optspec:0:1}" != ":" ]; then
                        echo "Unknown option --${OPTARG}" >&2
                        print_usage
                        exit 2
                    fi
                    ;;
            esac;;
        h)
            print_usage
            exit
            ;;
        *)
            if [ "$OPTERR" != 1 ] || [ "${optspec:0:1}" = ":" ]; then
                echo "Non-option argument: '-${OPTARG}'" >&2
                print_usage
                exit 2
            fi
            ;;
    esac
done




echo
echo "Setting up the ${DEMO} environment for Lab ${LAB_TO_SETUP}"
echo

case "${LAB_TO_SETUP}" in 
	1|2|3)
		setup_eap_with_modules
		;;
	4)
		setup_eap_node_with_modules node1
		setup_eap_node_with_modules node2
		;;
	5)
		setup_eap_with_modules
		setup_jdg_node_one
		;;
	*)
		echo "Unsupported lab"
		;;
esac


