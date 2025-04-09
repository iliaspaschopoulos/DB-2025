#!/bin/bash for Mac/Linux to run sqlcmd as a superuser
# This script reads our database configuration from a YAML file and executes a SQL query using sqlcmd.
# Ensure sqlcmd is installed
# Make sure you chmod +x run_sql.sh
# Usage ./run_sql.sh "SELECT * FROM your_table"

# Parse YAML file
parse_yaml() {
  awk -F': ' '/:/ {gsub(/"/, "", $2); print $2}' "$1"
}

# Read database config
CONFIG=$(parse_yaml superuser.yml)
read server port database username password <<< "$CONFIG"

# Check if SQL Query argument is provided
if [ $# -eq 0 ]; then
  echo "Error: No SQL query provided."
  exit 1
fi
SQL_QUERY=$1

# Run the query using sqlcmd
sqlcmd -S tcp:$server,$port -U $username -P "$password" -d $database -Q "$SQL_QUERY"

# Check the execution status
if [ $? -ne 0 ]; then
  echo "Error executing query."
  exit 2
fi
