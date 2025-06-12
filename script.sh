#!/bin/bash

PROJECT_DIR=~/LosAngelous/Project/MindFlow
FRONTEND_DIR=$PROJECT_DIR/frontend
BACKEND_DIR=$PROJECT_DIR/backend
TOMCAT_DIR=$PROJECT_DIR/backend/tomcat

echo "Building Vue frontend project"
cd $FRONTEND_DIR
npm install || {  
    echo "Error: Failed to install"
    exit 1
}

npm run build || {  
    echo "Error: Vue build failed"
    exit 1
}

if [ ! -d "$FRONTEND_DIR/dist" ]; then
    echo "Error: dist not found"
    exit 1
fi

echo "copying  files to backend src/main/webapp..."
cp -r $FRONTEND_DIR/dist/* $BACKEND_DIR/src/main/webapp/ || {
    echo "Error: Failed to copy Vue files"
    exit 1
}

echo "mvn clean package ..."

cd $BACKEND_DIR
mvn clean package || {
    echo "Error: Maven build failed"
    exit 1
} 

if [ ! -f "./target/MindFlow-1.0.war" ]; then
    echo "Error: WAR file not found"
    exit 1
fi

echo "Deploying WAR file to Tomcat"
cp ./target/MindFlow-1.0.war $TOMCAT_DIR/webapps/ROOT.war || {
    echo "Error: Failed to copy WAR"
    exit 1
}

# 重启 Tomcat
echo "Restarting Tomcat server"
sudo $TOMCAT_DIR/bin/shutdown.sh || echo "Warning: Tomcat shutdown"
sudo $TOMCAT_DIR/bin/startup.sh || {
    echo "Error: Failed to start Tomcat"
    exit 1
}

echo "Deployment completed successfully"


