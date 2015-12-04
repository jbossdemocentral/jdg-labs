BASEDIR=`dirname $0`

pushd $BASEDIR > /dev/null
. ../support/versions.sh

SHASUMS=(\
	${JDG_BASE_URL}/SHA256SUM \
	${EAP_BASE_URL}/SHA256SUM
)

DOWNLOADS=(\
	${JDG_BASE_URL}/jboss-datagrid-${JDG_VERSION}-eap-modules-remote-java-client.zip \
	${JDG_BASE_URL}/jboss-datagrid-${JDG_VERSION}-server.zip \
	${JDG_BASE_URL}/jboss-datagrid-${JDG_VERSION}-eap-modules-library.zip \
	${EAP_BASE_URL}/jboss-eap-${EAP_VERSION}-full-build.zip \
)


#####
# This metods verifies the files sha sum compared to downloaded sha sum
# paramters:
#  - 1 : The download url
#####
checkIfExists() {
	if [ -f ${1##*/} ]; then
		calulcated_shasum=$(shasum -a 256 ${1##*/} | awk '{ print $1 }')
		downloaded_shasum=$(\
			for sha in ${SHASUMS[*]}
			do
				curl -s $sha
			done | grep ${1##*/} | awk '{print $1}')
		if [ "${calulcated_shasum}" = "${downloaded_shasum}" ]; then
			return 0
		fi
	fi
	return 1
}


for download in ${DOWNLOADS[*]}
do
	if checkIfExists $download; then
		echo "File ${download##*/} already exist"
	else
		curl -L -O $download
	fi
done

popd > /dev/null
