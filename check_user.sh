#!/bin/bash

# Configuration file path
CONFIG_FILE="superuser.yml"

# Function to extract configuration values
parse_yaml() {
  server=$(yq '.database_config.server' "$CONFIG_FILE")
  port=$(yq '.database_config.port' "$CONFIG_FILE")
  database=$(yq '.database_config.database' "$CONFIG_FILE")
  username=$(yq '.database_config.username' "$CONFIG_FILE")
}

# Read database config
parse_yaml "$CONFIG_FILE"

# Check for username and password arguments
if [ $# -ne 2 ]; then
  echo "Usage: ./create_user_permissions.sh <username> <password>"
  exit 1
fi

NEW_USERNAME="$1"
NEW_PASSWORD="$2"

# SQL Commands to check if the user exists
CHECK_USER_SQL="USE $database; SELECT name FROM sys.database_principals WHERE name = '$NEW_USERNAME';"

# Run the query using sqlcmd to check if the user exists
sqlcmd -S "tcp:$server,$port" -U "$username" -P "$SQL_PASSWORD" -d "$database" -Q "$CHECK_USER_SQL" | grep -q "$NEW_USERNAME"

# Check the execution status
if [ $? -ne 0 ]; then
  echo "Error checking user."
  exit 2
fi

# Check if the user was found
if [ $? -eq 0 ]; then
  echo "User '$NEW_USERNAME' exists in database '$database'. Adding accesses..."

  # SQL Commands to grant permissions
  GRANT_USER_SQL="
  USE $database;
  GRANT SELECT, INSERT, UPDATE, DELETE, ALTER ON SCHEMA::dbo TO $NEW_USERNAME;
  GRANT CREATE TABLE TO $NEW_USERNAME;
  "

  # Run the query using sqlcmd to grant permissions
  sqlcmd -S "tcp:$server,$port" -U "$username" -P "$SQL_PASSWORD" -d "$database" -Q "$GRANT_USER_SQL"

  # Check the execution status
  if [ $? -ne 0 ]; then
    echo "Error granting permissions to user '$NEW_USERNAME'."
    exit 3
  fi

  echo "Permissions granted to user '$NEW_USERNAME' in database '$database'."

else
  echo "User '$NEW_USERNAME' does not exist in database '$database'. Creating..."

  # SQL Commands to create user and grant permissions
  CREATE_USER_SQL="
  USE $database;
  CREATE USER $NEW_USERNAME FOR LOGIN $NEW_USERNAME;
  GRANT SELECT, INSERT, UPDATE, DELETE, ALTER ON SCHEMA::dbo TO $NEW_USERNAME;
  GRANT CREATE TABLE TO $NEW_USERNAME;
  "

  # Run the query using sqlcmd to create user
  sqlcmd -S "tcp:$server,$port" -U "$username" -P "$SQL_PASSWORD" -d "$database" -Q "$CREATE_USER_SQL"

  # Check the execution status
  if [ $? -ne 0 ]; then
    echo "Error creating user or granting permissions."
    exit 4
  fi

  echo "User '$NEW_USERNAME' created in database '$database', and permissions granted."
fi