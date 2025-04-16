#!/bin/bash

# Configuration file path
CONFIG_FILE="superuser.yml"

# Function to extract configuration values
parse_yaml() {
  server=$(yq '.database_config.server' "$CONFIG_FILE")
  port=$(yq '.database_config.port' "$CONFIG_FILE")
  database=$(yq '.database_config.database' "$CONFIG_FILE")
  username=$(yq '.database_config.username' "$CONFIG_FILE")
  password=$(yq '.database_config.password' "$CONFIG_FILE")
}

# Read database config
parse_yaml "$CONFIG_FILE"

# Check for username and password arguments
if [ $# -ne 2 ]; then
  echo "Usage: ./check_user.sh <username> <password>"
  exit 1
fi

NEW_USERNAME="$1"
NEW_PASSWORD="$2"

# SQL Query to check if the login exists
CHECK_LOGIN_SQL="SELECT name FROM sys.server_principals WHERE name = '$NEW_USERNAME';"

# Run the query using sqlcmd to check if the login exists
sqlcmd -S "tcp:$server,$port" -U "$username" -P "$SQL_PASSWORD" -d master -Q "$CHECK_LOGIN_SQL"

# Check the execution status
if [ $? -ne 0 ]; then
  echo "Error checking login."
  exit 2
fi

# Check if the user was found
if grep -q "$NEW_USERNAME"; then
  echo "Login '$NEW_USERNAME' exists."
else
  echo "Login '$NEW_USERNAME' does not exist. Creating login and user..."

  # SQL Commands to create login and user
  CREATE_LOGIN_SQL="
  IF NOT EXISTS (SELECT 1 FROM sys.server_principals WHERE name = '$NEW_USERNAME')
  BEGIN
      CREATE LOGIN $NEW_USERNAME WITH PASSWORD = '$NEW_PASSWORD';
  END
  "

  CREATE_USER_SQL="
  USE $database;
  IF NOT EXISTS (SELECT 1 FROM sys.database_principals WHERE name = '$NEW_USERNAME')
  BEGIN
      CREATE USER $NEW_USERNAME FOR LOGIN $NEW_USERNAME;
  END;
  ALTER ROLE db_datareader ADD MEMBER $NEW_USERNAME;
  GRANT SELECT, INSERT, UPDATE, DELETE ON SCHEMA::dbo TO $NEW_USERNAME;
  "

  # Run the query using sqlcmd to create login
  sqlcmd -S "tcp:$server,$port" -U "$username" -P "$SQL_PASSWORD" -d master -Q "$CREATE_LOGIN_SQL"

  # Check the execution status
  if [ $? -ne 0 ]; then
    echo "Error creating login."
    exit 3
  fi

  # Run the query using sqlcmd to create user
  sqlcmd -S "tcp:$server,$port" -U "$username" -P "$SQL_PASSWORD" -d "$database" -Q "$CREATE_USER_SQL"

  # Check the execution status
  if [ $? -ne 0 ]; then
    echo "Error creating user or granting permissions."
    exit 4
  fi

  echo "Login '$NEW_USERNAME' created in master, user '$NEW_USERNAME' created in database '$database', and permissions granted."
fi