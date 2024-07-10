#!/bin/bash

check_mysql() {
  echo "Checking MySQL installation --->"
  if ! command -v mysql &> /dev/null
  then
    echo "MySQL is not installed. Please download and install MySQL from https://dev.mysql.com/downloads/mysql/"
    exit 1
  else
    echo "MySQL installation check successful."
  fi
}

get_mysql_credentials() {
  echo "Getting MySQL user credentials --->"
  read -p "Enter MySQL username: " mysql_user
  read -s -p "Enter MySQL password: " mysql_pass
  echo

  echo "Verifying MySQL credentials --->"
  mysql -u"$mysql_user" -p"$mysql_pass" -e "exit" 2>/dev/null
  if [ $? -ne 0 ]; then
    echo "Invalid MySQL username or password."
    exit 1
  else
    echo "MySQL credentials verification successful."
  fi
}

check_user_privileges() {
  echo "Checking user privileges --->"
  priv_count=$(mysql -u"$mysql_user" -p"$mysql_pass" -e "SHOW GRANTS FOR '$mysql_user'@'localhost';" | grep -c "ALL PRIVILEGES")
  if [ $priv_count -eq 0 ]; then
    echo "The user does not have ALL PRIVILEGES."
    exit 1
  else
    echo "User privileges check successful."
  fi
}

get_email_credentials() {
  echo "Getting email credentials for verification --->"
  read -p "Enter email address: " email_address
  read -s -p "Enter email password: " email_password
  echo
  echo "Email credentials obtained successfully."
}

save_credentials() {
  mysql_data_file="src/main/java/Server/.DatabaseConfig.txt"
  email_data_file="src/main/java/Server/.EmailData.txt"
  echo "username=$mysql_user" > "$mysql_data_file"
  echo "password=$mysql_pass" > "$mysql_data_file"
  echo "Saving email data to $email_data_file --->"
  echo "email=$email_address" > "$email_data_file"
  echo "password=$email_password" > "$email_data_file"
  echo "Credentials saved successfully."
}

echo "Starting script execution --->"
check_mysql
get_mysql_credentials
check_user_privileges
get_email_credentials
save_credentials
echo "Script execution completed successfully."
