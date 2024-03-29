#!/bin/bash


VERSION=0.0.1
echo "1: `pwd`"
pushd ${0%/*}/../
echo "2: `pwd`"


#
# printing
#
COLORS=~/bin/color.sh
if [ -e $COLORS ] ; then
        source $COLORS
        print=print
else
        print=echo
fi


VERSION=0.0.1
xmltool=$(which xmllint)
if [ "$xmltool" == "" ] ; then
    $print INF "xmllint is required for dynamic version detection. using: $VERSION"
else
    if [ -e pom.xml ] ; then
        VERSION=`xmllint --xpath '/*[local-name()="project"]/*[local-name()="version"]/text()' pom.xml`
        $print DBG "version from pom: $VERSION"
    else
        $print DBG "pom not found, using version: $VERSION"
    fi
fi



#
# for usage
#
function usage()
{
    $print ERR "usaage: $0 [-V] [-D] [-R] [-H] xvm"
    $print ERR "  V: verbose running mode"
    $print ERR "  D: pass java debugging args"
    $print ERR "  H: print this help list"
    $print ERR "  I: print info about this process"
}



#
# parse the command args
#
OPTS=`getopt -o vdrhi "$@"`
PROFILE=""
HELP=""
VERBOSE=""
RECORD=""
DEBUG=""
INFO=""
CONFIG="conf/config.xml"
while true; do
    case "$1" in
        -H ) HELP="true"; shift ;;
        -V ) VERBOSE="true"; shift ;;
        -D ) DEBUG="true"; shift ;;
        -R ) RECORD="true"; shift ;;
	-I ) INFO="true"; shift ;;
        -- ) shift; break ;;
        * ) break ;;
    esac
done


#
# print help?
#
if [ "$HELP" == "true" ] ; then
    usage
    exit 0
fi



#
# get the jars
#
wd=`dirname $0`
jar="./target/metrics-$VERSION.jar"
if [ ! -e $jar ] ; then
   jar="./lib/metrics-$VERSION.jar"
   echo $jar
fi

if [ ! -e $jar ] ; then
    $print ERR "cannot find jar: $jar"
    $print ERR "script does not appear to be run from project dir"
    exit 1
fi



if [ "$INFO" == "true" ] ; then
    $print INF "version: $VERSION"
    $print INF "jar....: $jar"
    $print INF "PWD....: `pwd`"
    exit 0
fi



args="-DAPP_CFG=./etc/metrics.xml -Dlogfile.name=metrics.log -Dlogback.configurationFile=./etc/logback.xml kagr.metrics.EntryPoint $@"
if [ "$DEBUG" == "true" ] ; then
    $print INF "debug mode enabled..."
    args="$args -verbose:class -XX:NativeMemoryTracking=detail"
    args="$args"
fi




JAVA_VER=$(java -version 2>&1 | head -1 | cut -d'"' -f2 | sed '/^1\./s///' | cut -d'.' -f1)
if [ "$RECORD" == "true" ] ; then
    $print INF "recording mode enabled"
    if [ "$JAVA_VER" -ge 9 ] ; then
        $print INF "Java GTE(1.9), using new JFR settings"
        args="$args -XX:StartFlightRecording=duration=120s,settings=profile,filename=$XVM.jfr"
    else
        $print ERR "Java LEQ(1.8), using 1.8 JFR ettings"
        args="$args -XX:+UnlockCommercialFeatures -XX:+FlightRecorder -XX:StartFlightRecording=duration=120s,settings=full,filename=$XVM.jfr"
    fi
fi




cmd="java -cp $jar $args"

if [ "$VERBOSE" == "true" ] ; then
    $print DBG $cmd
fi




exec $cmd | ctr

popd

