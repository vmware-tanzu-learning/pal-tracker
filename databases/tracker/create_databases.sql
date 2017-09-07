DROP DATABASE IF EXISTS tracker_dev;
DROP DATABASE IF EXISTS tracker_test;

CREATE USER 'tracker'@'localhost'
  IDENTIFIED BY '';
GRANT ALL PRIVILEGES ON *.* TO 'tracker' @'localhost';

CREATE DATABASE tracker_dev;
CREATE DATABASE tracker_test;