EAP_MAYOR_VERSION=6
EAP_MINOR_VERSION=4
EAP_PATCH_VERSION=5
EAP_VERSION=${EAP_MAYOR_VERSION}.${EAP_MINOR_VERSION}.${EAP_PATCH_VERSION}

JDG_MAYOR_VERSION=6
JDG_MINOR_VERSION=5
JDG_PATCH_VERSION=1
JDG_VERSION=${JDG_MAYOR_VERSION}.${JDG_MINOR_VERSION}.${JDG_PATCH_VERSION}

JBOSS_HOME=./target/jboss-eap-${EAP_MAYOR_VERSION}.${EAP_MINOR_VERSION}
JDG_HOME=./target/jboss-datagrid-${JDG_VERSION}-server
JDG_ONE_HOME=./target/jboss-datagrid-${JDG_VERSION}-server-one
JDG_TWO_HOME=./target/jboss-datagrid-${JDG_VERSION}-server-two
SERVER_DIR=$JBOSS_HOME/standalone/deployments/
SERVER_CONF=$JBOSS_HOME/standalone/configuration/
SRC_DIR=./installs
EAP_SERVER=jboss-eap-${EAP_VERSION}-full-build.zip
EAP_SERVER_MD5SUM=
JDG_SERVER=jboss-datagrid-${JDG_VERSION}-server.zip
JDG_LIBRARY_MODUELS=jboss-datagrid-${JDG_VERSION}-eap-modules-library.zip
HOTROD_MODULES=jboss-datagrid-${JDG_VERSION}-eap-modules-remote-java-client.zip

JDG_BASE_URL="http://download.eng.bos.redhat.com/released/JBossDG/${JDG_VERSION}"
EAP_BASE_URL="http://download.eng.bos.redhat.com/released/JBEAP-6/${EAP_VERSION}"
