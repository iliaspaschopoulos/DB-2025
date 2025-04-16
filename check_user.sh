#!/bin/bash

# Configuration file path
CONFIG_FILE="superuser.yml"

# Function to extract configuration values
parse_yaml() {
  awk -F': ' '/:/ {gsub(/"/, "", $2); print $2}' "$1"
}

# Read database config
CONFIG=$(parse_yaml "$CONFIG_FILE")
read server port database username <<< "$CONFIG"

# SQL Query to alter the user password
SQL_QUERY="ALTER LOGIN deandodger WITH PASSWORD = 'Password123!';"

# Run the query using sqlcmd (using environment variable)
sqlcmd -S "tcp:$server,$port" -U "$username" -P "$SQL_PASSWORD" -d master -Q "$SQL_QUERY"

# Check the execution status
if [ $? -ne 0 ]; then
  echo "Error executing query."
  exit 2
fi

echo "Password for deandodger login altered successfully."