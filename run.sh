#!/bin/bash

function help {
    echo "Options"
    echo "  build = Build the app, don't start it"
    echo "  start = Start the app. Will build it if it has not already been built."
    echo ""
    echo "Example Command"
    echo "  bash run.sh postgres start"
    echo ""
    echo "Other"
    echo "  help = The help menu"
}

function run_app {
    # bash setup.sh
    sudo -E docker-compose -f docker/docker-compose.yml up -d
}

function build_app {
    mvn -P prod clean package
    if [[ $? -eq 0 ]]; then
        sudo -E docker-compose -f docker/docker-compose.yml build
    fi
}

function build_or_run {
	case $1 in
		"build") build_app ;;
		"start") run_app ;;
		*)
			echo "Error! Invalid command: $1. Try using the 'help' option for more details"
		;;
	esac
}

if [[ $# -ne 1 ]]; then
    echo "Error! Invalid number of arguments. Try using the 'help' option for more details"
    exit 1
fi

build_or_run $1