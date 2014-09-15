BASEDIR=`dirname $0`

pushd $BASEDIR > /dev/null


echo "Base dir is $BASEDIR"

JDG_BASE_URL="http://download.eng.bos.redhat.com/released/JBossDG/6.3.0"
EAP_BASE_URL="http://download.eng.bos.redhat.com/released/JBEAP-6/6.3.0"


DOWNLOADS=(\
	${JDG_BASE_URL}/jboss-datagrid-6.3.0-eap-modules-hotrod-java-client.zip \
	${JDG_BASE_URL}/jboss-datagrid-6.3.0-server.zip \
	${JDG_BASE_URL}/jboss-datagrid-6.3.0-maven-repository.zip \
	${JDG_BASE_URL}/jboss-datagrid-6.3.0-eap-modules-library.zip \
	${EAP_BASE_URL}/jboss-eap-6.3.0.zip \
	${EAP_BASE_URL}/jboss-eap-6.2.4-full-maven-repository.zip \
	${EAP_BASE_URL}/jboss-eap-6.3.0-maven-repository.zip\
)

for download in ${DOWNLOADS[*]}
do
	echo "Will download $download"
	curl -L -O -C - $download
done

popd > /dev/null