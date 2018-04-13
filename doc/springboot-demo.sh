#!/bin/sh
appname="springboot-demo.jar"
startCommand="java -jar $appname --spring.profiles.active=prd"
pidlist=`ps -ef|grep $appname|grep -v "grep"|awk '{print $2}'`
if [ $1 == "start" ]
  then
    $startCommand
elif [ "$pidlist" == "" ]
  then
    echo "$appname已经关闭"
elif [ $1 == "stop" ]
  then
    echo "$appname 进程号 :$pidlist"
    kill -9 $pidlist
    echo "关闭$appname  success"
else
  echo "unknowed command"
fi

