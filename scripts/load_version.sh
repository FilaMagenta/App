#!/usr/bin/env bash

file="./version.properties"

function prop {
    grep "${1}" "${file}" | cut -d'=' -f2
}

{
  echo SHARED_VERSION_NAME="$(prop 'shared.versionName')";
  echo ANDROID_VERSION_NAME="$(prop 'android.versionName')";
  echo ANDROID_VERSION_CODE="$(prop 'android.versionCode')";
  echo IOS_VERSION_NAME="$(prop 'ios.versionName')"
  echo IN_APP_UPDATE_PRIORITY="$(python3 scripts/get_latest_version.py)"
} >> $GITHUB_ENV

#if [ -f "$file" ]
#then
#  while IFS='=' read -r key value
#  do
#    key=$(echo "$key" | tr '.' '_')
#    eval "${key}=${value}"
#  done < "$file"
#else
#  echo "$file not found."
#  exit 1
#fi
